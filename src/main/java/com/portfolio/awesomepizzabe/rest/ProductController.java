package com.portfolio.awesomepizzabe.rest;

import com.portfolio.awesomepizzabe.config.exceptions.AlreadyExistsException;
import com.portfolio.awesomepizzabe.config.exceptions.status.NotFoundException;
import com.portfolio.awesomepizzabe.dto.ProductDTO;
import com.portfolio.awesomepizzabe.mapper.ProductMapper;
import com.portfolio.awesomepizzabe.service.ProductService;
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
@RequestMapping("/admin/product")
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    public ProductController(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    /**
     * The saveProduct method allows to save a new Product  in the database.
     * The Product must have a unique name. If a name that has already been taken is chosen, the server will reply with a 409.
     * The Product must also have a valid Product Type. If the inputed product type does not exist, the server will reply with a 404.
     *
     * @param dto
     * @return the input after it has been inserted in the database. (201 CREATED)
     * @throws MethodArgumentNotValidException if the DTO has an empty or null name or product type (400 BAD REQUEST)
     * @throws AlreadyExistsException          if the name has already been taken (409 CONFLICT)
     * @throws NotFoundException               if the inputed Product Type does not exist (404 NOT FOUND)
     */
    @PostMapping
    public ResponseEntity<ProductDTO> saveProduct(@RequestBody @Valid ProductDTO dto) {
        return ResponseEntity.status(201).body(productMapper.toDTO(productService.createProduct(productMapper.toModel(dto))));
    }

    /**
     * The updateProduct method allows to update a Product that exists in the database.
     * The updated Product is found by querying the database with the given id. If no Product is found, the server replies with a 404.
     * If the inputed Product Type does not exist, the server will also reply with a 404.
     * If the name of the Product changes, it must be unique. If a name that has already been taken is chosen by a different Product, the server will reply with a 409.
     *
     * @param id
     * @param dto
     * @return the input after it has been updated. (200 OK)
     * @throws MethodArgumentNotValidException if the DTO has an empty or null name or Product Type (400 BAD REQUEST)
     * @throws NotFoundException               if there is no corresponding Product to the given id OR the inputed Product Type does not exist (404 NOT FOUND)
     * @throws AlreadyExistsException          if the name has already been taken (409 CONFLICT)
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable String id, @RequestBody @Valid ProductDTO dto) {
        return ResponseEntity.ok(productMapper.toDTO(productService.updateProduct(id, productMapper.toModel(dto))));
    }

    /**
     * The deleteProduct method allows to delete a Product  from the database.
     * The Product is found by querying the database with the given id. If no Product is found, the server replies with a 404.
     *
     * @param id
     * @return a string asserting the successful deletion of the Product. (204 OK)
     * @throws NotFoundException if there is no corresponding Product to the given id (404 NOT FOUND)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.status(204).body("Product deleted successfully.");
    }

    /**
     * The getProduct method allows to find a Product in the database.
     * The Product is found by querying the database with the given id. If no Product is found, the server replies with a 404.
     *
     * @param id
     * @return the Product with the given id. (200 OK)
     * @throws NotFoundException if there is no corresponding Product to the given id (404 NOT FOUND)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable String id) {
        return ResponseEntity.ok(productMapper.toDTO(productService.findProduct(id)));
    }

    /**
     * The getAllProducts method allows to find all the Products in the database as a Page.
     *
     * @return a Page of Products. (200 OK)
     */
    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getAllProducts(
            @PageableDefault(page = 0, size = 50)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "productType.id", direction = Sort.Direction.ASC),
                    @SortDefault(sort = "name", direction = Sort.Direction.ASC)
            }) Pageable pageable) {
        return ResponseEntity.ok(productMapper.toDTO(productService.findAllProducts(pageable)));
    }


}
