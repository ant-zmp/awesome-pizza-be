package com.portfolio.awesomepizzabe.service;

import com.portfolio.awesomepizzabe.dto.ProductTypeDetailDTO;
import com.portfolio.awesomepizzabe.model.ProductType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductTypeService {
    ProductType createProductType(ProductType productType);
    ProductType updateProductType(String id, ProductType productType);
    void deleteProductType(String id);
    ProductType findProductType(String id);
    Page<ProductType> findAllProductTypes(Pageable pageable);
    List<ProductTypeDetailDTO> findAllProductTypeDetails();
}
