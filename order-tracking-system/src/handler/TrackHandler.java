package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.OrderDAO;
import enums.OrderStatus;
import model.Order;
import model.TrackingEvent;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Handles GET /api/track?orderId=ORD-1001 and answers with JSON.
 * 200 = found, 400 = empty ID, 404 = no such order, 500 = server/database problem.
 */
public class TrackHandler implements HttpHandler {

    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a", Locale.ENGLISH);

    private final OrderDAO orderDAO;

    // Receives the interface type, so any OrderDAO implementation can be plugged in
    public TrackHandler(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJson(exchange, 405, errorJson("Only GET requests are allowed."));
                return;
            }

            String orderId = readOrderId(exchange.getRequestURI().getRawQuery());
            if (orderId.isEmpty()) {
                sendJson(exchange, 400, errorJson("Please enter an order ID."));
                return;
            }

            Optional<Order> found = orderDAO.findById(orderId);
            if (found.isEmpty()) {
                sendJson(exchange, 404, errorJson(
                        "No order found with ID \"" + orderId + "\". Please check the ID and try again."));
                return;
            }

            Order order = found.get();
            List<TrackingEvent> history = orderDAO.findHistoryByOrderId(orderId);
            sendJson(exchange, 200, buildOrderJson(order, history));

        } catch (SQLException e) {
            e.printStackTrace();
            sendJson(exchange, 500, errorJson(
                    "Database error. Check that MySQL is running and the password in DBConnection.java is correct."));
        } catch (RuntimeException e) {
            e.printStackTrace();
            sendJson(exchange, 500, errorJson("Unexpected server error. See the console for details."));
        }
    }

    // ---------- reading the request ----------

    /** Extracts orderId from the query string; returns "" if missing. Result is trimmed and upper-cased. */
    private String readOrderId(String rawQuery) {
        if (rawQuery == null) {
            return "";
        }
        for (String pair : rawQuery.split("&")) {
            int eq = pair.indexOf('=');
            String key = eq >= 0 ? pair.substring(0, eq) : pair;
            if ("orderId".equals(key)) {
                String value = eq >= 0 ? pair.substring(eq + 1) : "";
                try {
                    return URLDecoder.decode(value, StandardCharsets.UTF_8).trim().toUpperCase(Locale.ROOT);
                } catch (IllegalArgumentException e) {
                    return "";
                }
            }
        }
        return "";
    }

    // ---------- building the response ----------

    private String buildOrderJson(Order order, List<TrackingEvent> history) {
        OrderStatus status = order.getCurrentStatus();
        StringBuilder json = new StringBuilder();

        json.append("{");
        json.append(field("orderId", order.getOrderId())).append(",");
        json.append(field("customerName", order.getCustomerName())).append(",");
        json.append(field("productName", order.getProductName())).append(",");
        json.append(field("currentStatus", status.name())).append(",");
        json.append(field("currentStatusLabel", status.getLabel())).append(",");
        json.append(field("currentStatusDescription", status.getDescription())).append(",");
        json.append("\"currentStep\":").append(status.getStep()).append(",");
        json.append("\"totalSteps\":").append(OrderStatus.values().length).append(",");

        json.append("\"steps\":[");
        OrderStatus[] all = OrderStatus.values();
        for (int i = 0; i < all.length; i++) {
            if (i > 0) {
                json.append(",");
            }
            json.append("\"").append(escape(all[i].getLabel())).append("\"");
        }
        json.append("],");

        json.append(field("placedAt", order.getCreatedAt().format(DISPLAY_FORMAT))).append(",");

        json.append("\"timeline\":[");
        for (int i = 0; i < history.size(); i++) {
            TrackingEvent event = history.get(i);
            if (i > 0) {
                json.append(",");
            }
            json.append("{");
            json.append(field("status", event.getStatus().name())).append(",");
            json.append(field("statusLabel", event.getStatus().getLabel())).append(",");
            json.append(field("location", event.getLocation())).append(",");
            json.append(field("remarks", event.getRemarks())).append(",");
            json.append(field("eventTime", event.getEventTime().format(DISPLAY_FORMAT)));
            json.append("}");
        }
        json.append("]");

        json.append("}");
        return json.toString();
    }

    private String errorJson(String message) {
        return "{" + field("error", message) + "}";
    }

    /** Builds one  "key":"value"  pair with the value safely escaped. */
    private String field(String key, String value) {
        return "\"" + key + "\":\"" + escape(value) + "\"";
    }

    /** Escapes characters that would break a JSON string. null becomes an empty string. */
    private String escape(String text) {
        if (text == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    private void sendJson(HttpExchange exchange, int statusCode, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Cache-Control", "no-store");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
