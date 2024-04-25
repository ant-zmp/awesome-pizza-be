package com.portfolio.awesomepizzabe.rest;

import com.portfolio.awesomepizzabe.config.exceptions.AlreadyExistsException;
import com.portfolio.awesomepizzabe.config.exceptions.AssociatedEntityException;
import com.portfolio.awesomepizzabe.config.exceptions.status.NotFoundException;
import com.portfolio.awesomepizzabe.dto.ProductTypeDTO;
import com.portfolio.awesomepizzabe.dto.ProductTypeInputDTO;
import com.portfolio.awesomepizzabe.mapper.ProductTypeInputMapper;
import com.portfolio.awesomepizzabe.mapper.ProductTypeMapper;
import com.portfolio.awesomepizzabe.service.ProductTypeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/product-type")
public class ProductTypeController {

    private final ProductTypeMapper productTypeMapper;
    private final ProductTypeService productTypeService;
    private final ProductTypeInputMapper productTypeInputMapper;

    public ProductTypeController(ProductTypeMapper productTypeMapper, ProductTypeService productTypeService, ProductTypeInputMapper productTypeInputMapper) {
        this.productTypeMapper = productTypeMapper;
        this.productTypeService = productTypeService;
        this.productTypeInputMapper = productTypeInputMapper;
    }


    /**
     * The saveProductType method allows to save a new Product Type in the database.
     * The ProductType must have a unique name. If a name that has already been taken is chosen, the server will reply with a 409.
     *
     * @param dto
     * @return the input after it has been inserted in the database. (201 CREATED)
     * @throws MethodArgumentNotValidException if the DTO has an empty or null name (400 BAD REQUEST)
     * @throws AlreadyExistsException          if the name has already been taken (409 CONFLICT)
     */
    @PostMapping
    public ResponseEntity<ProductTypeDTO> saveProductType(@RequestBody @Valid ProductTypeInputDTO dto) {
        return ResponseEntity.status(201).body(productTypeMapper.toDTO(productTypeService.createProductType(productTypeInputMapper.toModel(dto))));
    }

    /**
     * The updateProductType method allows to update a Product Type present in the database.
     * The updated Product Type is found by querying the database with the given id. If no Product Type is found, the server replies with a 404.
     * If the name of the Product Type changes, it must be unique. If a name that has already been taken is chosen by a different Product type, the server will reply with a 409.
     *
     * @param id
     * @param dto
     * @return the input after it has been updated. (200 OK)
     * @throws MethodArgumentNotValidException if the DTO has an empty or null name (400 BAD REQUEST)
     * @throws NotFoundException               if there is no corresponding Product Type to the given id (404 NOT FOUND)
     * @throws AlreadyExistsException          if the name has already been taken (409 CONFLICT)
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductTypeDTO> updateProductType(@PathVariable String id, @RequestBody @Valid ProductTypeInputDTO dto) {
        return ResponseEntity.ok(productTypeMapper.toDTO(productTypeService.updateProductType(id, productTypeInputMapper.toModel(dto))));
    }

    /**
     * The deleteProductType method allows to delete a Product Type from the database.
     * The Product Type is found by querying the database with the given id. If no Product Type is found, the server replies with a 404.
     * If there are Products linked to the specified Product Type,the server replies with a 409.
     *
     * @param id
     * @return a string asserting the successful deletion of the Product Type. (204 OK)
     * @throws NotFoundException         if there is no corresponding Product Type to the given id (404 NOT FOUND)
     * @throws AssociatedEntityException if there are Products associated with the Product Type (409 CONFLICT)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProductType(@PathVariable String id) {
        productTypeService.deleteProductType(id);
        return ResponseEntity.status(204).body("Product type deleted successfully.");
    }

    /**
     * The getProductType method allows to find a Product Type in the database.
     * The Product Type is found by querying the database with the given id. If no Product Type is found, the server replies with a 404.
     *
     * @param id
     * @return the Product Type with the given id. (200 OK)
     * @throws NotFoundException if there is no corresponding Product Type to the given id (404 NOT FOUND)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductTypeDTO> getProductType(@PathVariable String id) {
        return ResponseEntity.ok(productTypeMapper.toDTO(productTypeService.findProductType(id)));
    }

    /**
     * The getProductType method allows to find all the Product Type in the database as a Page.
     *
     * @return a Page of Product Types. (200 OK)
     */
    @GetMapping
    public ResponseEntity<Page<ProductTypeDTO>> getAllProductTypes(
            @PageableDefault(page = 0, size = 50)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "available", direction = Sort.Direction.ASC),
                    @SortDefault(sort = "id", direction = Sort.Direction.ASC)
            }) Pageable pageable) {
        return ResponseEntity.ok(productTypeMapper.toDTO(productTypeService.findAllProductTypes(pageable)));
    }


}
