package model;

import enums.OrderStatus;

import java.time.LocalDateTime;

/** One row of the tracking_history table. The event time is stored in BaseEntity.createdAt. */
public class TrackingEvent extends BaseEntity {

    private final String orderId;
    private final OrderStatus status;
    private final String location;
    private final String remarks;

    public TrackingEvent(String orderId, OrderStatus status, String location,
                         String remarks, LocalDateTime eventTime) {
        super(eventTime);
        this.orderId = orderId;
        this.status = status;
        this.location = location;
        this.remarks = remarks;
    }

    public String getOrderId() {
        return orderId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public String getLocation() {
        return location;
    }

    public String getRemarks() {
        return remarks;
    }

    public LocalDateTime getEventTime() {
        return getCreatedAt();
    }

    @Override
    public String describe() {
        return orderId + ": " + status.getLabel() + " at " + location;
    }
}
