package com.portfolio.awesomepizzabe.rest;

import com.portfolio.awesomepizzabe.dto.ProductDTO;
import com.portfolio.awesomepizzabe.dto.ProductTypeDetailDTO;
import com.portfolio.awesomepizzabe.mapper.ProductMapper;
import com.portfolio.awesomepizzabe.service.ProductService;
import com.portfolio.awesomepizzabe.service.ProductTypeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {
    private final ProductMapper productMapper;
    private final ProductService productService;
    private final ProductTypeService productTypeService;

    public CustomerController(ProductMapper productMapper, ProductService productService, ProductTypeService productTypeService) {
        this.productMapper = productMapper;
        this.productService = productService;
        this.productTypeService = productTypeService;
    }

    @GetMapping("/products")
    public ResponseEntity<Page<ProductDTO>> getAllAvailableProducts(
            @PageableDefault(page = 0, size = 50)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "productType.id", direction = Sort.Direction.ASC),
                    @SortDefault(sort = "name", direction = Sort.Direction.ASC)
            }) Pageable pageable) {
        return ResponseEntity.ok(productMapper.toDTO(productService.findAllAvailableProducts(pageable)));
    }

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

    @GetMapping("/product-types")
    public ResponseEntity<List<ProductTypeDetailDTO>> getAllProductTypes() {
        return ResponseEntity.ok(productTypeService.findAllProductTypeDetails());
    }

}
