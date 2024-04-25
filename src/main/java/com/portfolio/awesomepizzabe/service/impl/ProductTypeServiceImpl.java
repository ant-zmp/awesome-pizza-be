package com.portfolio.awesomepizzabe.service.impl;

import com.portfolio.awesomepizzabe.config.exceptions.AlreadyExistsException;
import com.portfolio.awesomepizzabe.config.exceptions.AssociatedEntityException;
import com.portfolio.awesomepizzabe.config.exceptions.status.NotFoundException;
import com.portfolio.awesomepizzabe.dto.ProductTypeDetailDTO;
import com.portfolio.awesomepizzabe.model.Product;
import com.portfolio.awesomepizzabe.model.ProductType;
import com.portfolio.awesomepizzabe.repository.ProductRepository;
import com.portfolio.awesomepizzabe.repository.ProductTypeRepository;
import com.portfolio.awesomepizzabe.service.ProductTypeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ProductTypeServiceImpl implements ProductTypeService {
    
    private final ProductTypeRepository productTypeRepository;
    private final ProductRepository productRepository;

    public ProductTypeServiceImpl(ProductTypeRepository productTypeRepository, ProductRepository productRepository) {
        this.productTypeRepository = productTypeRepository;
        this.productRepository = productRepository;
    }

    /**
     * The createProductType method creates a Product Type in the database.
     * If there is already a Product Type with the same name, the method throws an AlreadyExistException.
     * @param productType
     * @throws AlreadyExistsException if there is a Product Type with the same name in the database
     * @return the Product Type after it has been saved on the database.
     */
    @Override
    public ProductType createProductType(ProductType productType) {
        if(productTypeRepository.existsByNameIgnoreCase(productType.getName()))
            throw new AlreadyExistsException(String.format("Product type with name %s already exists", productType.getName()));

        productType.setId(null);
        return productTypeRepository.save(productType);
    }

    /**
     * The updateProductType method updates a Product Type in the database. An id must be inputed in order to find the Product Type to edit.
     * If there is no Product Type with the given id, the method throws a NotFoundException.
     * If there is already a Product Type with the same name, the method throws an AlreadyExistException.
     * @param id
     * @param productType
     * @throws AlreadyExistsException if there is a Product Type with the same name in the database
     * @throws NotFoundException if there is no corresponding Product Type to the given id.
     * @return the Product Type after it has been saved on the database.
     */
    @Override
    public ProductType updateProductType(String id, ProductType productType) {
        ProductType onDb = findProductType(id);

        if(!Objects.equals(onDb.getName(),productType.getName())){
            if(productTypeRepository.existsByNameIgnoreCase(productType.getName()))
                throw new AlreadyExistsException(String.format("Product type with name %s already exists", productType.getName()));
            else onDb.setName(productType.getName());
        }

        onDb.setDescription(productType.getDescription());
        return productTypeRepository.save(onDb);
    }

    /**
     * The deleteProductType method allows to delete a Product Type from the database.
     * The Product Type is found by querying the database with the given id. If no Product Type is found, the method throws a NotFoundException.
     * If there are entities (Products) linked to the Product Type, the method throws an AssociatedEntityException.
     * @param id
     * @throws NotFoundException if there is no corresponding Product Type to the given id.
     * @throws AssociatedEntityException if there are Products linked to the corresponding Product Type.
     */
    @Override
    public void deleteProductType(String id) {
        ProductType onDb = findProductType(id);
        if(productRepository.existsByProductTypeId(id))
            throw new AssociatedEntityException(String.format("There are products linked to the product type with id %s", id));
        productTypeRepository.delete(onDb);
    }

    /**
     * The getProductType method allows to find a Product Type in the database.
     * The Product Type is found by querying the database with the given id. If no Product Type is found, the method throws a NotFoundException.
     * @param id
     * @throws NotFoundException if there is no corresponding Product Type to the given id.
     * @return the Product Type with the given id.
     */
    @Override
    public ProductType findProductType(String id) {
        return productTypeRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Product type with id %s not found", id)));
    }

    /**
     * The getAllProductType method gives a Page containing all Product Types in the database.
     * @return a Page containing all Product Types.
     */
    @Override
    public Page<ProductType> findAllProductTypes(Pageable pageable) {
        return productTypeRepository.findAll(pageable);
    }

    /**
     * The getAllProductTypeDetails method gives a List containing all Product Types in the database with the associated Product count and a picture, if available.
     * @return a List containing all Product Types.
     */
    @Override
    public List<ProductTypeDetailDTO> findAllProductTypeDetails() {
        List<ProductType> productTypes = productTypeRepository.findAll();
        return productTypes.stream().map( productType -> {
            ProductTypeDetailDTO productTypeDetailDTO = new ProductTypeDetailDTO();
            productTypeDetailDTO.setId(productType.getId());
            productTypeDetailDTO.setName(productType.getName());
            productTypeDetailDTO.setDescription(productType.getDescription());
            productTypeDetailDTO.setCount(productRepository.countByProductTypeId(productType.getId()));
            if(productTypeDetailDTO.getCount() > 0){
                Product product = productRepository.findFirstByProductTypeIdAndAvailableTrueAndImageIdNotNull(productType.getId());
                if(Objects.nonNull(product))
                    productTypeDetailDTO.setImageId(product.getImageId());
            }
            return productTypeDetailDTO;
        }).toList();
    }
}
