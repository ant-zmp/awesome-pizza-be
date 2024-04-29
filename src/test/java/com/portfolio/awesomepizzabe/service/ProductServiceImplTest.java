package com.portfolio.awesomepizzabe.service;

import com.portfolio.awesomepizzabe.AwesomePizzaBeApplication;
import com.portfolio.awesomepizzabe.config.exceptions.AlreadyExistsException;
import com.portfolio.awesomepizzabe.config.exceptions.status.BadRequestException;
import com.portfolio.awesomepizzabe.config.exceptions.status.ConflictException;
import com.portfolio.awesomepizzabe.config.exceptions.status.NotFoundException;
import com.portfolio.awesomepizzabe.model.Product;
import com.portfolio.awesomepizzabe.model.ProductType;
import com.portfolio.awesomepizzabe.repository.ProductRepository;
import com.portfolio.awesomepizzabe.repository.ProductTypeRepository;
import com.portfolio.awesomepizzabe.service.impl.ProductServiceImpl;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = {AwesomePizzaBeApplication.class})
public class ProductServiceImplTest {

    @Autowired
    private ProductTypeRepository productTypeRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private ResourceLoader resourceLoader;

    ProductType testProductType;

    Product testSubjectFirst;
    Product testSubjectSecond;

    String fileIdOne;
    String fileIdTwo;

    @BeforeAll
    public void setUp() throws Exception {

        testProductType = productTypeRepository.save(
                new ProductType(null, "Pizza", "Lorem ipsum dolor")
        );

        Resource resource = resourceLoader.getResource("classpath:pizza_margherita.jpg");
        fileIdOne = gridFsTemplate.store(resource.getInputStream(), resource.getFilename(), "image/jpeg", Map.of("temp", true)).toString();

        resource = resourceLoader.getResource("classpath:pizza_carrettiera.jpg");
        fileIdTwo = gridFsTemplate.store(resource.getInputStream(), resource.getFilename(), "image/jpeg", Map.of("temp", true)).toString();

        testSubjectFirst = new Product();
        testSubjectFirst.setName("Margherita");
        testSubjectFirst.setDescription("Pizza Margherita");
        testSubjectFirst.setProductType(testProductType);
        testSubjectFirst.setPrice(3.50);
        testSubjectFirst.setAvailable(true);
        testSubjectFirst.setIngredients(List.of("Pomodoro", "Fiordilatte", "Basilico", "Olio EVO"));
        testSubjectFirst.setImageId(fileIdOne);

        testSubjectSecond = new Product();
        testSubjectSecond.setPrice(0);
        testSubjectSecond.setProductType(new ProductType(null, "", ""));
        testSubjectSecond.setName("Margherita");
        testSubjectSecond.setImageId("id_does_not_exist");
    }

    @AfterAll
    public void tearDown() {
        productTypeRepository.deleteAll();
        productRepository.deleteAll();

        gridFsTemplate.delete(new Query(Criteria.where("_id").is(new ObjectId(fileIdOne))));
        gridFsTemplate.delete(new Query(Criteria.where("_id").is(new ObjectId(fileIdTwo))));
    }

    @Test
    @Order(1)
    public void createProduct() {
        Product saved = assertDoesNotThrow(() -> productService.createProduct(testSubjectFirst));

        assertNotNull(saved.getId());
        asserter(testSubjectFirst, saved);

        assertThrows(BadRequestException.class, () -> productService.createProduct(testSubjectSecond));
        testSubjectSecond.setPrice(5.0);
        assertThrows(BadRequestException.class, () -> productService.createProduct(testSubjectSecond));
        testSubjectSecond.setProductType(new ProductType("id_does_not_exist", "", ""));
        assertThrows(ConflictException.class, () -> productService.createProduct(testSubjectSecond));
        testSubjectSecond.setName("Salsiccia e Friarielli");
        assertThrows(NotFoundException.class, () -> productService.createProduct(testSubjectSecond));
        testSubjectSecond.setProductType(testProductType);
        assertThrows(NotFoundException.class, () -> productService.createProduct(testSubjectSecond));
        testSubjectSecond.setImageId(null);

        testSubjectFirst.setId(saved.getId());
        testSubjectSecond = assertDoesNotThrow(() -> productService.createProduct(testSubjectSecond));
    }

    @Test
    @Order(2)
    public void findProduct() {
        assertThrows(NotFoundException.class, () -> productService.findProduct("random id"));
        Product fetched = assertDoesNotThrow(() -> productService.findProduct(testSubjectFirst.getId()));

        assertEquals(testSubjectFirst.getId(), fetched.getId());
        asserter(testSubjectFirst, fetched);
    }

    @Test
    @Order(3)
    public void updateProduct() {
        Product testSubjectUpdate = new Product();
        testSubjectUpdate.setName("Margherita");
        testSubjectUpdate.setDescription("Pizza Carrettiera");
        testSubjectUpdate.setProductType(new ProductType(null, "", ""));
        testSubjectUpdate.setImageId(fileIdTwo);
        testSubjectUpdate.setIngredients(List.of("Salsiccia", "Fior di latte", "Friarielli"));

        testSubjectUpdate.setPrice(-4);
        assertThrows(BadRequestException.class, () -> productService.updateProduct(testSubjectSecond.getId(), testSubjectUpdate));

        testSubjectUpdate.setPrice(6.20);
        assertThrows(BadRequestException.class, () -> productService.updateProduct(testSubjectSecond.getId(), testSubjectUpdate));

        testSubjectUpdate.setProductType(new ProductType("id_does_not_exist", "", ""));
        assertThrows(NotFoundException.class, () -> productService.updateProduct("random id", testSubjectUpdate));

        assertThrows(AlreadyExistsException.class, () -> productService.updateProduct(testSubjectSecond.getId(), testSubjectUpdate));
        testSubjectUpdate.setName(testSubjectSecond.getName());

        assertThrows(NotFoundException.class, () -> productService.updateProduct(testSubjectSecond.getId(), testSubjectUpdate));
        testSubjectUpdate.setProductType(testProductType);

        testSubjectSecond = assertDoesNotThrow(() -> productService.updateProduct(testSubjectSecond.getId(), testSubjectUpdate));

        testSubjectUpdate.setName("Carrettiera");
        testSubjectUpdate.setAvailable(false);
        testSubjectUpdate.setImageId(null);

        testSubjectSecond = assertDoesNotThrow(() -> productService.updateProduct(testSubjectSecond.getId(), testSubjectUpdate));

    }

    @Test
    @Order(4)
    public void findAllProducts() {
        Page<Product> products = assertDoesNotThrow(() -> productService.findAllProducts(Pageable.unpaged()));
        assertEquals(products.getTotalElements(), 2);

        products = assertDoesNotThrow(() -> productService.findAllAvailableProducts(Pageable.unpaged()));
        assertEquals(products.getTotalElements(), 1);

        asserter(testSubjectFirst, products.getContent().get(0));

        products = assertDoesNotThrow(() -> productService.findAllAvailableProductsByType(testProductType.getId(), Pageable.unpaged()));
        assertEquals(products.getTotalElements(), 1);

        asserter(testSubjectFirst, products.getContent().get(0));
    }

    @Test
    @Order(5)
    public void deleteProduct() {
        assertThrows(NotFoundException.class, () -> productService.deleteProduct("random id"));
        assertDoesNotThrow(() -> productService.deleteProduct(testSubjectFirst.getId()));
        assertDoesNotThrow(() -> productService.deleteProduct(testSubjectSecond.getId()));
    }

    @Test
    @Order(6)
    public void findAllProductsEmpty() {
        Page<Product> productTypes = assertDoesNotThrow(() -> productService.findAllProducts(Pageable.unpaged()));
        assertEquals(productTypes.getTotalElements(), 0);
    }

    private void asserter(Product beforeSave, Product saved) {
        assertEquals(beforeSave.getName(), saved.getName());
        assertEquals(beforeSave.getDescription(), saved.getDescription());
        assertEquals(beforeSave.getProductType().getId(), saved.getProductType().getId());
        assertEquals(beforeSave.getPrice(), saved.getPrice());
        assertEquals(beforeSave.isAvailable(), saved.isAvailable());
        assertEquals(beforeSave.getIngredients(), saved.getIngredients());
        assertEquals(beforeSave.getImageId(), saved.getImageId());
    }

}
