package com.portfolio.awesomepizzabe.rest;

import com.portfolio.awesomepizzabe.config.exceptions.status.BadRequestException;
import com.portfolio.awesomepizzabe.config.exceptions.status.NotFoundException;
import com.portfolio.awesomepizzabe.dto.OrderDTO;
import com.portfolio.awesomepizzabe.dto.OrderDetailDTO;
import com.portfolio.awesomepizzabe.dto.ProductDTO;
import com.portfolio.awesomepizzabe.dto.ProductTypeDetailDTO;
import com.portfolio.awesomepizzabe.mapper.OrderMapper;
import com.portfolio.awesomepizzabe.mapper.ProductMapper;
import com.portfolio.awesomepizzabe.service.OrderService;
import com.portfolio.awesomepizzabe.service.ProductService;
import com.portfolio.awesomepizzabe.service.ProductTypeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {
    private final OrderMapper orderMapper;
    private final OrderService orderService;
    private final ProductMapper productMapper;
    private final ProductService productService;
    private final ProductTypeService productTypeService;

    public CustomerController(OrderMapper orderMapper, OrderService orderService, ProductMapper productMapper, ProductService productService, ProductTypeService productTypeService) {
        this.orderMapper = orderMapper;
        this.orderService = orderService;
        this.productMapper = productMapper;
        this.productService = productService;
        this.productTypeService = productTypeService;
    }

    /**
     * the placeOrder method allows to place an order.
     * If the productQuantity map is empty, the server replies with a 400.
     * If the chosen products do not exist or are no longer available, the server replies with a 400.
     *
     * @param orderDTO
     * @throws BadRequestException if the inputed DTO is not valid or the chosen Products are not available (400 BAD REQUEST).
     * @return the order after it has been placed (201 CREATED).
     */
    @PostMapping
    public ResponseEntity<OrderDTO> placeOrder(@RequestBody @Valid OrderDTO orderDTO) {
        return ResponseEntity.status(201).body(orderMapper.toDTO(orderService.placeOrder(orderMapper.toModel(orderDTO))));
    }

    /**
     * the checkOrderStatus allows to get the details about an Order.
     * If there is no order associated to the given order code, for the day, the server replies with a 404.
     *
     * @param orderCode
     * @throws NotFoundException if there is no order with the given code for the day (404 NOT FOUND)
     * @return the Order with its details (200 OK).
     */
    @GetMapping("/order/{orderCode}")
    public ResponseEntity<OrderDetailDTO> checkOrderStatus(@PathVariable("orderCode") String orderCode) {
        return ResponseEntity.ok(orderService.checkOrderStatus(orderCode));
    }

    /**
     * the confirmDelivery method advances the state of an order from DISPATCHED to COMPLETED.
     * If there are no orders associated to the given id, the server replies with a 404.
     * If the status of the order in the database is not DISPATCHED, the server replies with a 400.
     *
     * @param id
     * @throws BadRequestException if the order is not DISPATCHED (400 BAD REQUEST)
     * @throws NotFoundException if there is no order with the given id (404 NOT FOUND)
     * @return the order after it has been edited (200 OK).
     */
    @PutMapping("/order/{id}/confirm-delivery")
    public ResponseEntity<OrderDTO> confirmDelivery(@PathVariable("id") String id) {
        return ResponseEntity.ok(orderMapper.toDTO(orderService.confirmDelivery(id)));
    }

    /**
     * the getAllAvailableProducts returns a page with all the Products with their field available set to true.
     *
     * @param pageable
     * @return a page containing all available Products (200 OK).
     */
    @GetMapping("/products")
    public ResponseEntity<Page<ProductDTO>> getAllAvailableProducts(
            @PageableDefault(page = 0, size = 50)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "productType.id", direction = Sort.Direction.ASC),
                    @SortDefault(sort = "name", direction = Sort.Direction.ASC)
            }) Pageable pageable) {
        return ResponseEntity.ok(productMapper.toDTO(productService.findAllAvailableProducts(pageable)));
    }

    /**
     * the getAllAvailableProductsByType returns a page with all the available products by product type, given the id of the product type.
     *
     * @param id
     * @param pageable
     * @return a page containing all available Products by Product Type (200 OK).
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<Page<ProductDTO>> getAllAvailableProductsByType(
            @PathVariable("id") String id,
            @PageableDefault(page = 0, size = 50)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "productType.id", direction = Sort.Direction.ASC),
                    @SortDefault(sort = "name", direction = Sort.Direction.ASC)
            }) Pageable pageable) {
        return ResponseEntity.ok(productMapper.toDTO(productService.findAllAvailableProductsByType(id, pageable)));
    }

    /**
     * the getAllProductTypes returns a list of all the Product Types in the database.
     *
     * @return a list containing all Product Types (200 OK).
     */
    @GetMapping("/product-types")
    public ResponseEntity<List<ProductTypeDetailDTO>> getAllProductTypes() {
        return ResponseEntity.ok(productTypeService.findAllProductTypeDetails());
    }

}
