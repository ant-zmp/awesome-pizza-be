package com.portfolio.awesomepizzabe.rest;

import com.portfolio.awesomepizzabe.dto.ProductTypeDTO;
import com.portfolio.awesomepizzabe.mapper.ProductTypeMapper;
import com.portfolio.awesomepizzabe.service.ProductTypeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/product-type")
public class ProductTypeController {

    private final ProductTypeService productTypeService;
    private final ProductTypeMapper productTypeMapper;

    public ProductTypeController(ProductTypeService productTypeService, ProductTypeMapper productTypeMapper) {
        this.productTypeService = productTypeService;
        this.productTypeMapper = productTypeMapper;
    }

    /**
     * The saveProductType allows to save a new Product Type in the database.
     * The ProductType must have a unique name. If a name that has already been taken is chosen, the server will reply with a 409.
     * @param dto
     * @throws MethodArgumentNotValidException if the DTO has an empty or null name (400 BAD REQUEST)
     * @throws com.portfolio.awesomepizzabe.config.exceptions.AlreadyExistsException if the name has already been taken (409 CONFLICT)
     * @return the input after it has been inserted in the database. (201 CREATED)
     */
    @PostMapping
    public ResponseEntity<ProductTypeDTO> saveProductType(@RequestBody @Valid ProductTypeDTO dto) {
        return ResponseEntity.status(201).body(productTypeMapper.toDTO(productTypeService.createProductType(productTypeMapper.toModel(dto))));
    }

    /**
     * The updateProductType allows to update a Product Type present in the database.
     * The updated Product Type is found by querying the database with the given id. If no Product Type is found, the server replies with a 404.
     * If the name of the Product Type changes, it must be unique. If a name that has already been taken is chosen by a different Product type, the server will reply with a 409.
     * @param id
     * @param dto
     * @throws MethodArgumentNotValidException if the DTO has an empty or null name (400 BAD REQUEST)
     * @throws com.portfolio.awesomepizzabe.config.exceptions.NotFoundException if there is no corresponding Product Type to the given id (404 NOT FOUND)
     * @throws com.portfolio.awesomepizzabe.config.exceptions.AlreadyExistsException if the name has already been taken (409 CONFLICT)
     * @return the input after it has been updated. (200 OK)
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductTypeDTO> updateProductType(@PathVariable String id, @RequestBody @Valid ProductTypeDTO dto) {
        return ResponseEntity.ok(productTypeMapper.toDTO(productTypeService.updateProductType(id, productTypeMapper.toModel(dto))));
    }

    /**
     * The deleteProductType allows to delete a Product Type from the database.
     * The Product Type is found by querying the database with the given id. If no Product Type is found, the server replies with a 404.
     * @param id
     * @throws com.portfolio.awesomepizzabe.config.exceptions.NotFoundException if there is no corresponding Product Type to the given id (404 NOT FOUND)
     * @return a string asserting the successful deletion of the Product Type. (204 OK)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProductType(@PathVariable String id) {
        productTypeService.deleteProductType(id);
        return ResponseEntity.status(204).body("Product type deleted successfully.");
    }

    /**
     * The getProductType allows to find a Product Type in the database.
     * The Product Type is found by querying the database with the given id. If no Product Type is found, the server replies with a 404.
     * @param id
     * @throws com.portfolio.awesomepizzabe.config.exceptions.NotFoundException if there is no corresponding Product Type to the given id (404 NOT FOUND)
     * @return the Product Type with the given id. (200 OK)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductTypeDTO> getProductType(@PathVariable String id) {
        return ResponseEntity.ok(productTypeMapper.toDTO(productTypeService.findProductType(id)));
    }

    /**
     * The getProductType allows to find all the Product Type in the database as a List.
     * @return a List of Product Types. (200 OK)
     */
    @GetMapping
    public ResponseEntity<List<ProductTypeDTO>> getAllProductTypes() {
        return ResponseEntity.ok(productTypeMapper.toDTO(productTypeService.findAllProductTypes()));
    }


}
