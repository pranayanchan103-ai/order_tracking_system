import com.sun.net.httpserver.HttpServer;
import dao.OrderDAO;
import dao.OrderDAOImpl;
import handler.StaticFileHandler;
import handler.TrackHandler;
import util.DBConnection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;

/** Starts the web server on http://localhost:8080 */
public class Main {

    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {


        if (!Files.isDirectory(Paths.get("public"))) {
            System.out.println("WARNING: 'public' folder not found in " + Paths.get("").toAbsolutePath());
            System.out.println("         Run the program from the order-tracking-system folder.");
        }
        try (Connection conn = DBConnection.getConnection()) {
            if (conn.isValid(2)) {
                System.out.println("MySQL connection OK.");
            }
        } catch (SQLException e) {
            System.out.println("WARNING: cannot connect to MySQL -> " + e.getMessage());
            System.out.println("         Check that MySQL is running and the password in DBConnection.java is correct.");
        }

        OrderDAO orderDAO = new OrderDAOImpl();   // interface type on the left, implementation on the right

        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/api/track", new TrackHandler(orderDAO));
        server.createContext("/", new StaticFileHandler("public"));
        server.setExecutor(null);   // default single-threaded executor is enough for a demo
        server.start();

        System.out.println("Order Tracker running at http://localhost:" + PORT);
        System.out.println("Press Ctrl+C to stop.");
    }
}
// for running the main:
// java -cp "bin;lib/mysql-connector-j-26.7.0.jar" Main
// or:
/* cd C:\Users\prana\Desktop\order-tracking-system
javac -d bin -sourcepath src src\Main.java
java -cp "bin;lib/mysql-connector-j-26.7.0.jar" Main */