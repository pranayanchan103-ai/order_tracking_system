package dao;

import model.Order;
import model.TrackingEvent;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Contract for reading order data. The handler depends on this interface,
 * not on JDBC. Note: there is deliberately no update or delete method for history.
 */
public interface OrderDAO {

    /** Returns the order, or Optional.empty() if no order has this ID. */
    Optional<Order> findById(String orderId) throws SQLException;

    /** Returns every tracking event of the order, oldest first. */
    List<TrackingEvent> findHistoryByOrderId(String orderId) throws SQLException;
}
