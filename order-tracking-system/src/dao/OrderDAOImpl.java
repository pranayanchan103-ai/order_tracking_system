package dao;

import enums.OrderStatus;
import model.Order;
import model.TrackingEvent;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** JDBC implementation of OrderDAO. Every query uses a PreparedStatement. */
public class OrderDAOImpl implements OrderDAO {

    private static final String FIND_ORDER_SQL =
            "SELECT order_id, customer_name, product_name, current_status, created_at "
            + "FROM orders WHERE order_id = ?";

    private static final String FIND_HISTORY_SQL =
            "SELECT status, location, remarks, event_time "
            + "FROM tracking_history WHERE order_id = ? "
            + "ORDER BY event_time ASC, history_id ASC";

    @Override
    public Optional<Order> findById(String orderId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ORDER_SQL)) {

            ps.setString(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                Order order = new Order(
                        rs.getString("order_id"),
                        rs.getString("customer_name"),
                        rs.getString("product_name"),
                        OrderStatus.fromString(rs.getString("current_status")),
                        rs.getObject("created_at", LocalDateTime.class));
                return Optional.of(order);
            }
        }
    }

    @Override
    public List<TrackingEvent> findHistoryByOrderId(String orderId) throws SQLException {
        List<TrackingEvent> events = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_HISTORY_SQL)) {

            ps.setString(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    events.add(new TrackingEvent(
                            orderId,
                            OrderStatus.fromString(rs.getString("status")),
                            rs.getString("location"),
                            rs.getString("remarks"),
                            rs.getObject("event_time", LocalDateTime.class)));
                }
            }
        }
        return events;
    }
}
