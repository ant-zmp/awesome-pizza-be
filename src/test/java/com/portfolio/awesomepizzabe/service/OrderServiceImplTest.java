package com.portfolio.awesomepizzabe.service;

import com.portfolio.awesomepizzabe.AwesomePizzaBeApplication;
import com.portfolio.awesomepizzabe.config.exceptions.status.BadRequestException;
import com.portfolio.awesomepizzabe.config.exceptions.status.ConflictException;
import com.portfolio.awesomepizzabe.config.exceptions.status.NotFoundException;
import com.portfolio.awesomepizzabe.dto.OrderDetailDTO;
import com.portfolio.awesomepizzabe.model.OrderStatus;
import com.portfolio.awesomepizzabe.model.Product;
import com.portfolio.awesomepizzabe.model.Order;
import com.portfolio.awesomepizzabe.repository.OrderRepository;
import com.portfolio.awesomepizzabe.repository.ProductRepository;
import com.portfolio.awesomepizzabe.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = {AwesomePizzaBeApplication.class})
public class OrderServiceImplTest {

    @Autowired
    private OrderServiceImpl orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    Product testProductOne;
    Product testProductTwo;

    Order testOrderOne;
    Order testOrderTwo;

    @BeforeAll
    public void setUp() {


        testProductOne = new Product();
        testProductOne.setName("Product One");
        testProductOne.setAvailable(true);
        testProductOne.setPrice(3.20);

        testProductOne = productRepository.save(testProductOne);

        testProductTwo = new Product();
        testProductTwo.setName("Product Two");
        testProductTwo.setAvailable(false);
        testProductTwo.setPrice(5.00);

        testProductTwo = productRepository.save(testProductTwo);


    }

    @AfterAll
    public void tearDown() {
        productRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    public void generateOrderCodeTest() {
        LocalDate now = LocalDate.now();
        String orderCode = orderService.generateOrderCode();
        assertFalse(orderRepository.existsByOrderCodeAndOrderDateBetween(orderCode, now.atStartOfDay(), now.plusDays(1).atStartOfDay()));

        Order order = new Order();
        order.setOrderCode(orderCode);
        order.setOrderDate(LocalDateTime.now());

        order = orderRepository.save(order);

        assertTrue(orderRepository.existsByOrderCodeAndOrderDateBetween(orderCode, now.atStartOfDay(), now.plusDays(1).atStartOfDay()));

        String newOrderCode = orderService.generateOrderCode();
        assertNotEquals(orderCode, newOrderCode);

        order.setOrderDate(order.getOrderDate().plusDays(1));
        testOrderTwo = orderRepository.save(order);
        assertFalse(orderRepository.existsByOrderCodeAndOrderDateBetween(orderCode, now.atStartOfDay(), now.plusDays(1).atStartOfDay()));

        orderRepository.deleteAll();
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    public void placeOrderTest() {
        testOrderOne = new Order();
        Map<String, Integer> productQuantity = new HashMap<>();

        productQuantity.put(testProductOne.getId(), 2);
        productQuantity.put(testProductTwo.getId(), 1);
        productQuantity.put("fake_id", 1);

        testOrderOne.setProductQuantity(productQuantity);
        assertThrows(BadRequestException.class, () -> orderService.placeOrder(testOrderOne));

        testOrderOne.getProductQuantity().remove("fake_id");
        assertThrows(BadRequestException.class, () -> orderService.placeOrder(testOrderOne));

        testProductTwo.setAvailable(true);
        testProductTwo = productRepository.save(testProductTwo);
        Order order = assertDoesNotThrow(() -> orderService.placeOrder(testOrderOne));

        assertEquals(11.40, order.getTotalPrice());
        assertEquals(2, order.getProductQuantity().size());
        assertEquals(testOrderOne.getNotes(), order.getNotes());

        testOrderOne= order;

        Order newOrder = assertDoesNotThrow(() -> orderService.placeOrder(testOrderOne));
        assertNotNull(newOrder.getOrderCode(), order.getOrderCode());
        assertTrue(order.getOrderDate().isBefore(newOrder.getOrderDate()));

        testOrderTwo = newOrder;
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    public void findOrderTest() {
        assertThrows(NotFoundException.class, () -> orderService.findOrder("random id"));
        Order fetched = assertDoesNotThrow(() -> orderService.findOrder(testOrderOne.getId()));
        asserter(testOrderOne, fetched);
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    public void findNextOrderOneTest() {
        OrderDetailDTO fetched = assertDoesNotThrow(() -> orderService.findNextOrder());
        assertEquals(testOrderOne.getId(), fetched.getId());
        assertEquals(testOrderOne.getOrderCode(), fetched.getOrderCode());
        assertTrue(testOrderOne.getOrderDate().truncatedTo(ChronoUnit.MILLIS).isEqual(fetched.getOrderDate().truncatedTo(ChronoUnit.MILLIS)));
        assertEquals(testOrderOne.getStatus(), fetched.getStatus());
        assertEquals(testOrderOne.getTotalPrice(), fetched.getTotalPrice());
        assertEquals(testOrderOne.getProductQuantity().size(), fetched.getProductQuantity().size());
        assertEquals(testOrderOne.getNotes(), fetched.getNotes());
        assertEquals(testOrderOne.getReason(), fetched.getReason());
        assertEquals(testOrderOne.getAddress(), fetched.getAddress());
    }

    @Test
    @org.junit.jupiter.api.Order(5)
    public void checkOrderStatusTest() {
        assertThrows(NotFoundException.class, () -> orderService.checkOrderStatus("random id"));
        OrderDetailDTO detail = assertDoesNotThrow(() -> orderService.checkOrderStatus(testOrderOne.getOrderCode()));

        assertEquals(testOrderOne.getId(), detail.getId());
        assertEquals(testOrderOne.getOrderCode(), detail.getOrderCode());
        assertTrue(testOrderOne.getOrderDate().truncatedTo(ChronoUnit.MILLIS).isEqual(detail.getOrderDate().truncatedTo(ChronoUnit.MILLIS)));
        assertEquals(testOrderOne.getStatus(), detail.getStatus());
        assertEquals(testOrderOne.getTotalPrice(), detail.getTotalPrice());
        assertEquals(testOrderOne.getProductQuantity().size(), detail.getProductQuantity().size());
        assertEquals(testOrderOne.getNotes(), detail.getNotes());
        assertEquals(testOrderOne.getReason(), detail.getReason());
        assertEquals(testOrderOne.getAddress(), detail.getAddress());
        assertEquals(0, detail.getInLineBefore());

        detail = assertDoesNotThrow(() -> orderService.checkOrderStatus(testOrderTwo.getOrderCode()));
        assertEquals(1, detail.getInLineBefore());


    }

    @Test
    @org.junit.jupiter.api.Order(6)
    public void changeOrderStatusTest() {
        assertThrows(NotFoundException.class, () -> orderService.changeOrderStatus("random id"));
        testOrderOne = assertDoesNotThrow(() -> orderService.changeOrderStatus(testOrderOne.getId()));
        assertEquals(testOrderOne.getStatus(), OrderStatus.IN_PROGRESS);
        testOrderOne = assertDoesNotThrow(() -> orderService.changeOrderStatus(testOrderOne.getId()));
        assertEquals(testOrderOne.getStatus(), OrderStatus.DISPATCHED);
        assertThrows(BadRequestException.class, () -> orderService.changeOrderStatus(testOrderOne.getId()));
    }

    @Test
    @org.junit.jupiter.api.Order(7)
    public void findNextOrderTwoTest() {
        OrderDetailDTO fetched = assertDoesNotThrow(() -> orderService.findNextOrder());

        assertEquals(testOrderTwo.getId(), fetched.getId());
        assertEquals(testOrderTwo.getOrderCode(), fetched.getOrderCode());
        assertTrue(testOrderTwo.getOrderDate().truncatedTo(ChronoUnit.MILLIS).isEqual(fetched.getOrderDate().truncatedTo(ChronoUnit.MILLIS)));
        assertEquals(testOrderTwo.getStatus(), fetched.getStatus());
        assertEquals(testOrderTwo.getTotalPrice(), fetched.getTotalPrice());
        assertEquals(testOrderTwo.getProductQuantity().size(), fetched.getProductQuantity().size());
        assertEquals(testOrderTwo.getNotes(), fetched.getNotes());
        assertEquals(testOrderTwo.getReason(), fetched.getReason());
        assertEquals(testOrderTwo.getAddress(), fetched.getAddress());

        testOrderTwo = assertDoesNotThrow(() -> orderService.changeOrderStatus(testOrderTwo.getId()));
        testOrderTwo = assertDoesNotThrow(() -> orderService.changeOrderStatus(testOrderTwo.getId()));
        assertThrows(NotFoundException.class, () -> orderService.findNextOrder());
    }

    @Test
    @org.junit.jupiter.api.Order(8)
    public void findAllTest(){
        Page<Order> orders = assertDoesNotThrow(() -> orderService.findAllOrders(Pageable.unpaged()));
        assertEquals(2,orders.getTotalElements());
    }

    @Test
    @org.junit.jupiter.api.Order(9)
    public void confirmDeliveryTest(){
        assertThrows(NotFoundException.class, () -> orderService.confirmDelivery("random id"));
        testOrderOne = assertDoesNotThrow(() -> orderService.confirmDelivery(testOrderOne.getId()));

        assertEquals(OrderStatus.COMPLETED, testOrderOne.getStatus());
        assertThrows(BadRequestException.class, () -> orderService.confirmDelivery(testOrderOne.getId()));
    }

    @Test
    @org.junit.jupiter.api.Order(10)
    public void denyOrderTest(){
        assertThrows(BadRequestException.class, () -> orderService.denyOrder("random id",null));
        assertThrows(NotFoundException.class, () -> orderService.denyOrder("random id","Reason"));
        assertThrows(ConflictException.class, () -> orderService.denyOrder(testOrderOne.getId(), "Reason"));

        testOrderTwo = assertDoesNotThrow(() -> orderService.denyOrder(testOrderTwo.getId(),"Reason"));

        assertEquals(OrderStatus.CANCELLED, testOrderTwo.getStatus());
        assertEquals("Reason", testOrderTwo.getReason());
    }


    @Test
    @org.junit.jupiter.api.Order(11)
    public void orderVersioningTest(){
        Order order = new Order();

        order = orderRepository.save(order);
        Order clone = new Order();
        clone.setId(order.getId());
        clone.setVersion(order.getVersion());

        order.setNotes("edit");
        orderRepository.save(order);

        assertThrows(ConcurrencyFailureException.class, () -> orderRepository.save(clone));

    }

    private void asserter(Order expected, Order actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getOrderCode(), actual.getOrderCode());
        assertTrue(expected.getOrderDate().truncatedTo(ChronoUnit.MILLIS).isEqual(actual.getOrderDate().truncatedTo(ChronoUnit.MILLIS)));
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getTotalPrice(), actual.getTotalPrice());
        assertEquals(expected.getProductQuantity(), actual.getProductQuantity());
        assertEquals(expected.getNotes(), actual.getNotes());
        assertEquals(expected.getReason(), actual.getReason());
    }


}
