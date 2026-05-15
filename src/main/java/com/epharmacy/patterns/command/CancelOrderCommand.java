package com.epharmacy.patterns.command;

import com.epharmacy.models.Order;
import com.epharmacy.services.OrderService;

/** Command: Cancel an order → sets status to CANCELLED. */
public class CancelOrderCommand implements OrderCommand {

    private final OrderService orderService;
    private final int          orderId;
    private Order.Status       previousStatus;

    public CancelOrderCommand(OrderService orderService, int orderId) {
        this.orderService = orderService;
        this.orderId      = orderId;
    }

    @Override
    public void execute() {
        Order order = orderService.findById(orderId);
        if (order != null) {
            previousStatus = order.getStatus();
            orderService.updateStatus(orderId, Order.Status.CANCELLED);
            System.out.println("[Command] Order #" + orderId + " CANCELLED.");
        }
    }

    @Override
    public void undo() {
        if (previousStatus != null) {
            orderService.updateStatus(orderId, previousStatus);
            System.out.println("[Command] Undo cancel → Order #" + orderId + " restored to " + previousStatus);
        }
    }
}
