package model;

import enums.OrderStatus;

import java.time.LocalDateTime;

/** One row of the orders table. Fields are private and final: read-only after creation. */
public class Order extends BaseEntity {

    private final String orderId;
    private final String customerName;
    private final String productName;
    private final OrderStatus currentStatus;

    public Order(String orderId, String customerName, String productName,
                 OrderStatus currentStatus, LocalDateTime createdAt) {
        super(createdAt);
        this.orderId = orderId;
        this.customerName = customerName;
        this.productName = productName;
        this.currentStatus = currentStatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getProductName() {
        return productName;
    }

    public OrderStatus getCurrentStatus() {
        return currentStatus;
    }

    @Override
    public String describe() {
        return orderId + " - " + productName + " for " + customerName
                + " (" + currentStatus.getLabel() + ")";
    }
}
