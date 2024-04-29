package com.portfolio.awesomepizzabe.config;

import com.portfolio.awesomepizzabe.model.Order;
import com.portfolio.awesomepizzabe.model.OrderStatus;
import com.portfolio.awesomepizzabe.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class JobSchedulator {

    private final GridFsTemplate gridFsTemplate;
    private final OrderRepository orderRepository;
    private final WorkhourProperties workhourProperties;

    public JobSchedulator(GridFsTemplate gridFsTemplate, OrderRepository orderRepository, WorkhourProperties workhourProperties) {
        this.gridFsTemplate = gridFsTemplate;
        this.orderRepository = orderRepository;
        this.workhourProperties = workhourProperties;
    }

    /**
     * The removeTemp method represents a scheduled job. Every 15 minutes starting from 00, all images that are not associated to any product are deleted.
     * The method searches for all the file with field 'temp' set to true and deletes them.
     */
    @Scheduled(cron = "0 */15 * * * ?")
    public void removeTemp() {
        log.info("Temporary file cleanup...");
        gridFsTemplate.delete(Query.query(Criteria.where("metadata.temp").is(true)));
    }

    /**
     * The closeOrders method represents a scheduled job. Every day, at midnight, all the orders that are still open get closed.
     * The method searches for all the orders which state is PLACED, IN_PROGRESS and DISPATCHED.
     * If the order has been PLACED or is IN_PROGRESS, it gets CANCELLED.
     * If the order has been DISPATCHED, it gets COMPLETED.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void closeOrders() {
        log.info("Order cleanup...");
        List<Order> orders = orderRepository.findAllByStatusIn(List.of(OrderStatus.PLACED,OrderStatus.IN_PROGRESS,OrderStatus.DISPATCHED));
        for (Order order : orders) {
            if (List.of(OrderStatus.IN_PROGRESS, OrderStatus.PLACED).contains(order.getStatus())) {
                order.setStatus(OrderStatus.CANCELLED);
                order.setReason(workhourProperties.getReasonMessage());
            }
        }
    }
}
