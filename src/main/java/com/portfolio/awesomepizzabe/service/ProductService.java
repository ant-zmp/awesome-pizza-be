package com.portfolio.awesomepizzabe.service;

import com.portfolio.awesomepizzabe.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Product createProduct(Product product);
    Product updateProduct(String id, Product product);
    void deleteProduct(String id);
    Product findProduct(String id);
    Page<Product> findAllProducts(Pageable pageable);
    Page<Product> findAllAvailableProducts(Pageable pageable);
    Page<Product> findAllAvailableProductsByType(String productTypeId, Pageable pageable);
}
