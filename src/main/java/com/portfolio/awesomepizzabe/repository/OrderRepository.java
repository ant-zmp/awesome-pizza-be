package com.portfolio.awesomepizzabe.repository;

import com.portfolio.awesomepizzabe.model.Order;
import com.portfolio.awesomepizzabe.model.OrderStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends MongoRepository<Order, String> {
    boolean existsByOrderCodeAndOrderDateBetween(String orderCode, LocalDateTime start, LocalDateTime end);
    Optional<Order> findByOrderCodeAndOrderDateBetween(String orderCode, LocalDateTime start, LocalDateTime end);
    Optional<Order> findFirstByStatusInOrderByOrderDateAsc(List<OrderStatus> statuses);
    int countByOrderDateBeforeAndStatusIn(LocalDateTime orderDate, List<OrderStatus> statuses);
}
