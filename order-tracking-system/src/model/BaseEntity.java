package model;

import java.time.LocalDateTime;

/**
 * Abstract parent of every model class. Holds what all entities share (a timestamp)
 * and forces each child to describe itself.
 */
public abstract class BaseEntity {

    private LocalDateTime createdAt;

    protected BaseEntity(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /** Every child class must say how it describes itself (abstraction). */
    public abstract String describe();

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + describe() + "]";
    }
}
