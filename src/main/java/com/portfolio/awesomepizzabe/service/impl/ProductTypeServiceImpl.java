package com.portfolio.awesomepizzabe.service.impl;

import com.portfolio.awesomepizzabe.config.exceptions.AlreadyExistsException;
import com.portfolio.awesomepizzabe.config.exceptions.NotFoundException;
import com.portfolio.awesomepizzabe.model.ProductType;
import com.portfolio.awesomepizzabe.repository.ProductTypeRepository;
import com.portfolio.awesomepizzabe.service.ProductTypeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ProductTypeServiceImpl implements ProductTypeService {
    
    private final ProductTypeRepository productTypeRepository;

    public ProductTypeServiceImpl(ProductTypeRepository productTypeRepository) {
        this.productTypeRepository = productTypeRepository;
    }

    /**
     * The createProductType creates a Product Type in the database.
     * If there is already a Product Type with the same name, the method throws an AlreadyExistException.
     * @param productType
     * @throws AlreadyExistsException
     * @return the Product Type after it has been saved on the database.
     */
    @Override
    public ProductType createProductType(ProductType productType) {
        if(productTypeRepository.existsByName(productType.getName()))
            throw new AlreadyExistsException(String.format("Product type with name %s already exists", productType.getName()));

        productType.setId(null);
        return productTypeRepository.save(productType);
    }

    @Override
    public ProductType updateProductType(String id, ProductType productType) {
        ProductType onDb = findProductType(id);

        if(!Objects.equals(onDb.getName(),productType.getName())){
            if(productTypeRepository.existsByName(productType.getName()))
                throw new AlreadyExistsException(String.format("Product type with name %s already exists", productType.getName()));
            else onDb.setName(productType.getName());
        }

        onDb.setDescription(productType.getDescription());
        return productTypeRepository.save(onDb);
    }

    @Override
    public void deleteProductType(String id) {
        ProductType onDb = findProductType(id);
        productTypeRepository.delete(onDb);
    }

    @Override
    public ProductType findProductType(String id) {
        return productTypeRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Product type with id %s not found", id)));
    }

    @Override
    public List<ProductType> findAllProductTypes() {
        return productTypeRepository.findAll();
    }
}
