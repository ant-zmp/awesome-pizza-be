package com.portfolio.awesomepizzabe.service;

import com.portfolio.awesomepizzabe.dto.OrderDetailDTO;
import com.portfolio.awesomepizzabe.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    Order placeOrder(Order requestOrder);

    Order findOrder(String id);

    OrderDetailDTO checkOrderStatus(String orderCode);

    Order confirmDelivery(String id);

    Order changeOrderStatus(String id);

    Order denyOrder(String id, String reason);

    Order findNextOrder();

    Page<Order> findAllOrders(Pageable pageable);

}
