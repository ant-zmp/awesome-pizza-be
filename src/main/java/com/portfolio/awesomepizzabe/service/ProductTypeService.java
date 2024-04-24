package com.portfolio.awesomepizzabe.service;

import com.portfolio.awesomepizzabe.model.ProductType;

import java.util.List;

public interface ProductTypeService {
    ProductType createProductType(ProductType productType);
    ProductType updateProductType(String id, ProductType productType);
    void deleteProductType(String id);
    ProductType findProductType(String id);
    List<ProductType> findAllProductTypes();
}
