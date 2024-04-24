package com.portfolio.awesomepizzabe.repository;

import com.portfolio.awesomepizzabe.model.ProductType;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductTypeRepository extends MongoRepository<ProductType,String> {
    boolean existsByName(String name);
}
