package com.epharmacy.patterns.command;

import com.epharmacy.models.Order;
import com.epharmacy.services.OrderService;

/** Command: Approve a pending order → sets status to APPROVED. */
public class ApproveOrderCommand implements OrderCommand {

    private final OrderService orderService;
    private final int          orderId;
    private Order.Status       previousStatus;

    public ApproveOrderCommand(OrderService orderService, int orderId) {
        this.orderService = orderService;
        this.orderId      = orderId;
    }

    @Override
    public void execute() {
        Order order = orderService.findById(orderId);
        if (order != null) {
            previousStatus = order.getStatus();
            orderService.updateStatus(orderId, Order.Status.APPROVED);
            System.out.println("[Command] Order #" + orderId + " APPROVED.");
        }
    }

    @Override
    public void undo() {
        if (previousStatus != null) {
            orderService.updateStatus(orderId, previousStatus);
            System.out.println("[Command] Undo approve → Order #" + orderId + " restored to " + previousStatus);
        }
    }
}
