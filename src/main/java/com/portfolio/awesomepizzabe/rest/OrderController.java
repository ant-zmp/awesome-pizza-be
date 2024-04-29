package com.portfolio.awesomepizzabe.rest;

import com.portfolio.awesomepizzabe.config.exceptions.status.BadRequestException;
import com.portfolio.awesomepizzabe.config.exceptions.status.ConflictException;
import com.portfolio.awesomepizzabe.config.exceptions.status.NotFoundException;
import com.portfolio.awesomepizzabe.dto.OrderDTO;
import com.portfolio.awesomepizzabe.mapper.OrderMapper;
import com.portfolio.awesomepizzabe.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/order")
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    public OrderController(OrderService orderService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

    /**
     * the changeOrderStatus method advances the status for the order with the given id.
     * If no order exists with the given id, the server replies with a 404.
     * If the status cannot be further advanced, the server replies with a 400.
     * The method returns a response containing the order after it has been edited.
     *
     * @param id
     * @throws NotFoundException if there is no order corresponding to the given id (404 NOT FOUND).
     * @throws BadRequestException if it is not possible to advance the order (400 BAD REQUEST).
     * @return the edited order (200).
     */
    @PutMapping("/{id}/change-status")
    public ResponseEntity<OrderDTO> changeOrderStatus(@PathVariable("id") String id) {
        return ResponseEntity.ok(orderMapper.toDTO(orderService.changeOrderStatus(id)));
    }

    /**
     * the denyOrder method cancels an order.
     * If the reason is null or empty, the server replies with a 400.
     * If no order exists with the given id, the server replies with a 404.
     * If the status cannot be further changed, the server replies with a 409.
     * The method returns a response containing the order after it has been denied.
     *
     * @param id
     * @param reason
     * @throws BadRequestException if the reason is null or empty (400 BAD REQUEST).
     * @throws NotFoundException if there is no order corresponding to the given id (404 NOT FOUND).
     * @throws ConflictException if the status cannot be changed (409 CONFLICT)
     * @return the edited order (200)
     */
    @PutMapping("/{id}/deny")
    public ResponseEntity<OrderDTO> denyOrder(@PathVariable("id") String id, @RequestParam("reason") String reason) {
        return ResponseEntity.ok(orderMapper.toDTO(orderService.denyOrder(id, reason)));
    }

    /**
     * The findOrder method allows to find an Order in the database.
     * The Order is found by querying the database with the given id. If no Order is found, the server replies with a 404.
     *
     * @param id
     * @return the Order with the given id. (200 OK)
     * @throws NotFoundException if there is no corresponding Order to the given id (404 NOT FOUND)
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> findOrder(@PathVariable("id") String id) {
        return ResponseEntity.ok(orderMapper.toDTO(orderService.findOrder(id)));
    }

    /**
     * the findNextOrder method returns the next order that needs to be evaded.
     * If there are no orders that need to be evaded, the server replies with a 404.
     *
     * @throws NotFoundException if there are no orders that need to be evaded (404 NOT FOUND).
     * @return the next Order (200 OK)
     */
    @GetMapping("/find-next")
    public ResponseEntity<OrderDTO> findNextOrder() {
        return ResponseEntity.ok(orderMapper.toDTO(orderService.findNextOrder()));
    }

    /**
     * The findAllOrders method allows to find all the Orders in the database as a Page.
     *
     * @return a Page of Orders. (200 OK)
     */
    @GetMapping
    public ResponseEntity<Page<OrderDTO>> findAllOrders(
            @PageableDefault(page = 0, size = 50)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "orderDate", direction = Sort.Direction.DESC)
            }) Pageable pageable
    ) {
        return ResponseEntity.ok(orderMapper.toDTO(orderService.findAllOrders(pageable)));
    }

}
