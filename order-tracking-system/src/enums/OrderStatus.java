package enums;

import java.util.Locale;

/**
 * The five stages of an order. Each constant overrides getDescription(),
 * which is runtime polymorphism without any if/else chain.
 */
public enum OrderStatus {

    PLACED("Order Placed") {
        @Override
        public String getDescription() {
            return "We have received your order and it is being processed.";
        }
    },

    PACKED("Confirmed & Packed") {
        @Override
        public String getDescription() {
            return "Your order is confirmed and packed. It will be handed to the delivery partner soon.";
        }
    },

    SHIPPED("Shipped / In Transit") {
        @Override
        public String getDescription() {
            return "Your order has left our dispatch center and is on its way to you.";
        }
    },

    OUT_FOR_DELIVERY("Out for Delivery") {
        @Override
        public String getDescription() {
            return "Your order has reached your local delivery hub and will be delivered today.";
        }
    },

    DELIVERED("Delivered") {
        @Override
        public String getDescription() {
            return "Your order has been delivered successfully. Thank you for shopping with us!";
        }
    };

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /** Position of this status in the journey: PLACED = 1 ... DELIVERED = 5. */
    public int getStep() {
        return ordinal() + 1;
    }

    /** Customer-facing sentence for this status (each constant provides its own). */
    public abstract String getDescription();

    /** Converts the text stored in MySQL (e.g. "OUT_FOR_DELIVERY") into the enum. */
    public static OrderStatus fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Status value is null");
        }
        return OrderStatus.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}
