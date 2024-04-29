package com.portfolio.awesomepizzabe.service.impl;

import com.portfolio.awesomepizzabe.config.exceptions.AlreadyExistsException;
import com.portfolio.awesomepizzabe.config.exceptions.status.ConflictException;
import com.portfolio.awesomepizzabe.config.exceptions.status.BadRequestException;
import com.portfolio.awesomepizzabe.config.exceptions.status.NotFoundException;
import com.portfolio.awesomepizzabe.model.Product;
import com.portfolio.awesomepizzabe.repository.ProductRepository;
import com.portfolio.awesomepizzabe.repository.ProductTypeRepository;
import com.portfolio.awesomepizzabe.service.FileService;
import com.portfolio.awesomepizzabe.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductTypeRepository productTypeRepository;
    private final FileService fileService;

    public ProductServiceImpl(ProductRepository productRepository, ProductTypeRepository productTypeRepository, FileService fileService) {
        this.productRepository = productRepository;
        this.productTypeRepository = productTypeRepository;
        this.fileService = fileService;
    }

    /**
     * The createProduct method creates a Product in the database.
     * There is a validation for the product price and for the Product Type id. If they are not following certain rules, the method throws a BadRequestException.
     * If there is already a Product with the same name, the method throws an AlreadyExistException.
     * If the inputed Product Type does not exist, the method throws a NotFoundException.
     * This is also the case if the imageId in input does not link to any file.
     * @param product
     * @throws ConflictException
     * @throws BadRequestException
     * @throws NotFoundException
     * @return the Product after it has been saved on the database.
     */
    @Override
    public Product createProduct(Product product) {
        if(product.getPrice() <= 0){
            throw new BadRequestException("Price must be greater than zero");
        }

        if (Objects.requireNonNull(product.getProductType()).getId() == null){
            throw new BadRequestException("Product type must have an id");
        }

        if(productRepository.existsByNameIgnoreCase(product.getName()))
            throw new ConflictException(String.format("Product with name %s already exists", product.getName()));


        if(!productTypeRepository.existsById(product.getProductType().getId()))
            throw new NotFoundException(String.format("Product Type with id %s does not exist", product.getProductType().getId()));

        if(Objects.nonNull(product.getImageId())){
            fileService.setTemp(product.getImageId(),false);
        }


        product.setId(null);
        return productRepository.save(product);
    }

    /**
     * The updateProduct method updates a Product in the database. An id must be inputed in order to find the Product to edit.
     * There is a validation for the product price and for the Product Type id. If they are not following certain rules, the method throws a BadRequestException.
     * If there is no Product with the given id, the method throws a NotFoundException.
     * If there is already a Product with the same name, the method throws an AlreadyExistException.
     * @param id
     * @param product
     * @throws ConflictException
     * @throws BadRequestException
     * @throws NotFoundException
     * @return the Product Type after it has been saved on the database.
     */
    @Override
    public Product updateProduct(String id, Product product) {

        if(product.getPrice() <= 0){
            throw new BadRequestException("Price must be greater than zero");
        }

        if (Objects.requireNonNull(product.getProductType()).getId() == null){
            throw new BadRequestException("Product type must have an id");
        }

        Product onDb = findProduct(id);

        if(!Objects.equals(onDb.getName(),product.getName())){
            if(productRepository.existsByNameIgnoreCase(product.getName()))
                throw new AlreadyExistsException(String.format("Product type with name %s already exists", product.getName()));
            else onDb.setName(product.getName());
        }

        if(!Objects.equals(onDb.getProductType().getId(),product.getProductType().getId())){
            if(!productTypeRepository.existsById(product.getProductType().getId()))
                throw new NotFoundException(String.format("Product Type with id %s does not exist", product.getProductType().getId()));
            else onDb.setProductType(product.getProductType());
        }

        if(!Objects.equals(onDb.getImageId(),product.getImageId())){
            if(onDb.getImageId() != null)
                fileService.setTemp(onDb.getImageId(), true);

            if(product.getImageId() != null)
                fileService.setTemp(product.getImageId(), false);

            onDb.setImageId(product.getImageId());
        }

        onDb.setDescription(product.getDescription());
        onDb.setIngredients(product.getIngredients());
        onDb.setPrice(product.getPrice());
        onDb.setAvailable(product.isAvailable());

        return productRepository.save(onDb);
    }

    /**
     * The deleteProduct method allows to delete a Product from the database.
     * The Product is found by querying the database with the given id. If no Product is found, the method throws a NotFoundException.
     * It is not advised to delete a product, it is always better to set its availability to 'false', because this will edit all the orders.
     * @param id
     * @throws NotFoundException if there is no corresponding Product to the given id.
     */
    @Override
    public void deleteProduct(String id) {
        Product onDb = findProduct(id);
        if(Objects.nonNull(onDb.getImageId()))
            fileService.setTemp(onDb.getImageId(), true);
        productRepository.delete(onDb);
    }

    /**
     * The findProduct method allows to find a Product in the database.
     * The Product is found by querying the database with the given id. If no Product is found, the method throws a NotFoundException.
     * @param id
     * @throws NotFoundException if there is no corresponding Product to the given id.
     * @return the Product with the given id.
     */
    @Override
    public Product findProduct(String id) {
        return productRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Product type with id %s not found", id)));
    }

    /**
     * The findAllProducts method gives a Page containing all Products in the database given a pageable.
     * @return a Page containing all Product Types.
     */
    @Override
    public Page<Product> findAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    /**
     * The findAllAvailableProducts method gives a Page containing all Products with field 'available' set to true.
     * @return a Page containing all Product Types.
     */
    @Override
    public Page<Product> findAllAvailableProducts(Pageable pageable) {
        return productRepository.findAllByAvailableTrue(pageable);
    }

    /**
     * The findAllAvailableProductsByType method gives a Page containing all Products associated to a certain Product Type with field 'available' set to true.
     * @return a Page containing all Product Types.
     */
    @Override
    public Page<Product> findAllAvailableProductsByType(String productTypeId, Pageable pageable) {
        return productRepository.findAllByProductTypeIdAndAvailableTrue(productTypeId,pageable);
    }
}
