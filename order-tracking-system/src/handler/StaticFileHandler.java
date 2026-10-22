package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

/** Serves the files inside the public/ folder (index.html, style.css, script.js). */
public class StaticFileHandler implements HttpHandler {

    private final Path root;

    public StaticFileHandler(String folder) {
        this.root = Paths.get(folder).toAbsolutePath().normalize();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendText(exchange, 405, "Method not allowed");
            return;
        }

        String requestPath = exchange.getRequestURI().getPath();
        if (requestPath == null || requestPath.equals("/")) {
            requestPath = "/index.html";
        }

        Path file;
        try {
            file = root.resolve(requestPath.substring(1)).normalize();
        } catch (InvalidPathException e) {
            sendText(exchange, 404, "404 Not Found");
            return;
        }

        // Block "../" tricks: the file must stay inside the public folder
        if (!file.startsWith(root) || !Files.isRegularFile(file)) {
            sendText(exchange, 404, "404 Not Found");
            return;
        }

        byte[] bytes = Files.readAllBytes(file);
        exchange.getResponseHeaders().set("Content-Type", contentTypeOf(file));
        exchange.getResponseHeaders().set("Cache-Control", "no-cache");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private String contentTypeOf(Path file) {
        String name = file.getFileName().toString().toLowerCase(Locale.ROOT);
        if (name.endsWith(".html")) {
            return "text/html; charset=UTF-8";
        }
        if (name.endsWith(".css")) {
            return "text/css; charset=UTF-8";
        }
        if (name.endsWith(".js")) {
            return "application/javascript; charset=UTF-8";
        }
        return "application/octet-stream";
    }

    private void sendText(HttpExchange exchange, int statusCode, String text) throws IOException {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
