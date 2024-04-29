package com.portfolio.awesomepizzabe.model;

public enum OrderStatus {
    PLACED("Placed"),
    IN_PROGRESS("In Progress"),
    DISPATCHED("Dispathced"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    String name;

    public String getName() {
        return this.name;
    }

    OrderStatus(String name) {
        this.name = name;
    }

}
