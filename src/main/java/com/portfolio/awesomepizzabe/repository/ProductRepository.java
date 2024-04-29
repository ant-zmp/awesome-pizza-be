package com.portfolio.awesomepizzabe.repository;

import com.portfolio.awesomepizzabe.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {
    boolean existsByNameIgnoreCase(String name);
    boolean existsByProductTypeId(String productTypeId);

    List<Product> findAllByIdInAndAvailableTrue(Collection<String> ids);

    Page<Product> findAllByAvailableTrue(Pageable pageable);
    Page<Product> findAllByProductTypeIdAndAvailableTrue(String productTypeId, Pageable pageable);

    int countByProductTypeId(String productTypeId);
    Product findFirstByProductTypeIdAndAvailableTrueAndImageIdNotNull(String productTypeId);
}
