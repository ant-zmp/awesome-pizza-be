package com.portfolio.awesomepizzabe.service.impl;

import com.portfolio.awesomepizzabe.config.WorkhourProperties;
import com.portfolio.awesomepizzabe.config.exceptions.AlreadyExistsException;
import com.portfolio.awesomepizzabe.config.exceptions.status.BadRequestException;
import com.portfolio.awesomepizzabe.config.exceptions.status.ConflictException;
import com.portfolio.awesomepizzabe.config.exceptions.status.NotFoundException;
import com.portfolio.awesomepizzabe.dto.OrderDetailDTO;
import com.portfolio.awesomepizzabe.dto.ProductDetailDTO;
import com.portfolio.awesomepizzabe.model.Order;
import com.portfolio.awesomepizzabe.model.OrderStatus;
import com.portfolio.awesomepizzabe.model.Product;
import com.portfolio.awesomepizzabe.repository.OrderRepository;
import com.portfolio.awesomepizzabe.repository.ProductRepository;
import com.portfolio.awesomepizzabe.service.OrderService;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.portfolio.awesomepizzabe.config.GenericConstants.ALPHA_NUMERIC_STRING;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final WorkhourProperties workhourProperties;

    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository, WorkhourProperties workhourProperties) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.workhourProperties = workhourProperties;
    }

    /**
     * The placeOrder method allows to create an order. The order must contain a list of available product ids with the associated quantities.
     * If the list of Product ids and the returned Products do not match in size, the method throws a BadRequestException.
     * The method calculates the total price of the Order and associates a new code for the day to it.
     * Then, the method returns the Order after it has been saved.
     *
     * @param requestOrder
     * @return the Order after it has been saved.
     * @throws BadRequestException when the Products in the database do not match the required Products.
     */
    @Override
    public Order placeOrder(Order requestOrder) {
        Set<String> productIds = requestOrder.getProductQuantity().keySet();
        Map<String, Product> products = productRepository.findAllByIdInAndAvailableTrue(productIds)
                .stream().collect(Collectors.toMap(Product::getId, product -> product));

        if (productIds.size() != products.size()) {
            throw new BadRequestException("The required products do not match all available products!");
        }

        Order order = new Order();

        double totalPrice = 0;

        for (String id : productIds) {
            Product product = products.get(id);
            int quantity = requestOrder.getProductQuantity().get(id);
            totalPrice += product.getPrice() * quantity;
        }

        order.setProductQuantity(requestOrder.getProductQuantity());
        order.setTotalPrice(totalPrice);
        order.setOrderCode(generateOrderCode());
        order.setNotes(requestOrder.getNotes());

        return orderRepository.save(order);
    }

    /**
     * the findOrder method returns an Order with the given id from the database.
     * If there is no Order associated to the id, the method throws a NotFoundException.
     *
     * @param id
     * @return the associated Order
     * @throws NotFoundException when the id is not associated to any Order in the database.
     */
    @Override
    public Order findOrder(String id) {
        return orderRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Order with id '%s' not found.", id)));
    }

    /**
     * the checkOrderStatus allows to get an Order details given an orderCode.
     * The Order details include a complete overview of the ordered Products with their quantities and how many Orders are in line before it.
     * If there is no order associated to the orderCode for the day, the method throws a NotFoundException.
     *
     * @param orderCode
     * @return the Order with all its details
     * @throws NotFoundException if there is no order with the given code for the day.
     */
    @Override
    public OrderDetailDTO checkOrderStatus(String orderCode) {
        LocalDate now = LocalDate.now();
        Order onDb = orderRepository.findByOrderCodeAndOrderDateBetween(
                        orderCode, now.atStartOfDay(), now.plusDays(1).atStartOfDay())
                .orElseThrow(() -> new NotFoundException(String.format("Order with code '%s' not found.", orderCode)));

        Set<ProductDetailDTO> productQuantities = new HashSet<>();

        Map<String, Product> productMap = productRepository.findAllByIdInAndAvailableTrue(onDb.getProductQuantity().keySet())
                .stream().collect(Collectors.toMap(Product::getId, product -> product));

        onDb.getProductQuantity().keySet()
                .forEach(id -> productQuantities.add(new ProductDetailDTO(productMap.get(id), onDb.getProductQuantity().get((id))))
                );

        OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
        orderDetailDTO.setId(onDb.getId());
        orderDetailDTO.setOrderCode(orderCode);
        orderDetailDTO.setOrderDate(onDb.getOrderDate());
        orderDetailDTO.setTotalPrice(onDb.getTotalPrice());
        orderDetailDTO.setNotes(onDb.getNotes());
        orderDetailDTO.setStatus(onDb.getStatus());
        orderDetailDTO.setReason(onDb.getReason());
        orderDetailDTO.setProductQuantity(productQuantities);
        orderDetailDTO.setInLineBefore(orderRepository.countByOrderDateBeforeAndStatusIn(onDb.getOrderDate(), List.of(OrderStatus.PLACED, OrderStatus.IN_PROGRESS)));
        return orderDetailDTO;
    }

    /**
     * the changeOrderStatus advances the state of the order with the given id. The status can be changed only the order is PLACED or IN_PROGRESS.
     * If there is no order associated to the given id, throws a NotFoundException.
     * If the status of the order is not PLACED or IN_PROGRESS, the method throws a BadRequestException.
     * Finally, the method returns the order after the status has been changed and the order saved on the database.
     *
     * @param id
     * @return the order with the new status.
     * @throws NotFoundException   if there is no order with the given id.
     * @throws BadRequestException if the Order is not in status PLACED or IN_PROGRESS
     */
    @Override
    public Order changeOrderStatus(String id) {
        Order order = findOrder(id);
        switch (order.getStatus()) {
            case PLACED:
                order.setStatus(OrderStatus.IN_PROGRESS);
                break;
            case IN_PROGRESS:
                order.setStatus(OrderStatus.DISPATCHED);
                break;
            default:
                throw new BadRequestException(String.format("It is not possible to change status for order '%s'.", id));
        }

        return orderRepository.save(order);
    }

    /**
     * the confirmDelivery method advances the state of the order with the given id, and can be used only to change it from DISPATCHED to COMPLETED.
     * If there is no order associated to the given id, throws a NotFoundException.
     * If the status of the order is not PLACED or IN_PROGRESS, the method throws a BadRequestException.
     * The method changes status and then returns the order after it has been saved on the database.
     *
     * @param id
     * @return the order with the new status.
     * @throws NotFoundException   if there is no order with the given id.
     * @throws BadRequestException if the Order is not in status DISPATCHED
     */
    @Override
    public Order confirmDelivery(String id) {
        Order order = findOrder(id);
        if (!order.getStatus().equals(OrderStatus.DISPATCHED)) {
            throw new BadRequestException(String.format("It is not possible to change status from '%s' to COMPLETED for order '%s'.", order.getStatus(), id));
        }

        order.setStatus(OrderStatus.COMPLETED);
        return orderRepository.save(order);
    }


    /**
     * The denyOder method changes the status of the order with the given id to "CANCELLED".
     * If no reason is provided, the method throws a BadRequestException.
     * The order is found from the database. If there is no order associated to the given id, throws a NotFoundException.
     * If the status of the order is COMPLETED or CANCELLED, the method throws a ConflictException.
     * The method returns the order after it has been cancelled.
     *
     * @param id
     * @param reason
     * @return the order after it has been edited
     * @throws BadRequestException    - the reason is null;
     * @throws NotFoundException      - there is no Order for the given id;
     * @throws AlreadyExistsException - the status is already COMPLETED or CANCELLED
     */
    @Override
    public Order denyOrder(String id, String reason) {
        if (StringUtils.isEmpty(reason)) {
            throw new BadRequestException("A reason is required to perform this action.");
        }

        Order order = findOrder(id);
        switch (order.getStatus()) {
            case COMPLETED:
                throw new ConflictException("It is not possible to change status for a completed order.");
            case CANCELLED:
                throw new ConflictException("This order has already been cancelled.");
            default:
                order.setStatus(OrderStatus.CANCELLED);
                order.setReason(reason);

                return orderRepository.save(order);
        }

    }

    /**
     * the findNextOrder method returns the next order that needs to be evaded. The order can be either PLACED or IN_PROGRESS.
     * If there are no orders that need to be evaded, the method throws a NotFoundException.
     *
     * @return the next Order
     * @throws NotFoundException if there are no orders that need to be evaded
     */
    @Override
    public Order findNextOrder() {
        return orderRepository.findFirstByStatusInOrderByOrderDateAsc(List.of(OrderStatus.PLACED, OrderStatus.IN_PROGRESS))
                .orElseThrow(() -> new NotFoundException("There are no orders to evade!"));
    }

    /**
     * the findAllOrders method returns a page of Orders.
     *
     * @param pageable
     * @return a page of orders from the database.
     */
    @Override
    public Page<Order> findAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    /**
     * the generateOrderCode generates a string that uniquely identifies an Order for the day.
     * The Order Code is composed by two parts, both 3 characters long, that are separated by a dash "-" (A1C-1B3).
     * The code is generated the first time, then a check to validate if it does not exist for the current day is performed.
     * This is highly unlikely (a chance on 2.176.782.336), therefore the do-while cycle should not run more than once.
     *
     * @return a 7-character long string.
     */
    public String generateOrderCode() {

        String result;
        StringBuilder stringBuilder;

        do {
            stringBuilder = new StringBuilder();

            for (int i = 0; i < 3; i++) {
                stringBuilder.append(ALPHA_NUMERIC_STRING.charAt((int) (ALPHA_NUMERIC_STRING.length() * Math.random())));
            }
            stringBuilder.append("-");

            for (int i = 0; i < 3; i++) {
                stringBuilder.append(ALPHA_NUMERIC_STRING.charAt((int) (ALPHA_NUMERIC_STRING.length() * Math.random())));
            }
            result = stringBuilder.toString();

        } while (orderRepository.existsByOrderCodeAndOrderDateBetween(result, LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay()));

        return result;
    }

    /**
     * The checkWorkhours method checks if it is possible to place an order. If not, the method throws a BadRequestException;
     *
     * @throws BadRequestException if the order is placed before or after the specified work hours.
     */
    @Override
    public void checkWorkHours() {
        LocalTime now = LocalTime.now();
        if (now.isBefore(LocalTime.parse(workhourProperties.getStart())) || now.isAfter(LocalTime.parse(workhourProperties.getEnd()))) {
            throw new BadRequestException(workhourProperties.getReasonMessage());
        }
    }
}
