package com.portfolio.awesomepizzabe.service;

import com.portfolio.awesomepizzabe.AwesomePizzaBeApplication;
import com.portfolio.awesomepizzabe.config.exceptions.AlreadyExistsException;
import com.portfolio.awesomepizzabe.config.exceptions.NotFoundException;
import com.portfolio.awesomepizzabe.model.ProductType;
import com.portfolio.awesomepizzabe.repository.ProductTypeRepository;
import com.portfolio.awesomepizzabe.service.impl.ProductTypeServiceImpl;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(classes = {AwesomePizzaBeApplication.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductTypeServiceImplTest {

    @Autowired
    private ProductTypeRepository productTypeRepository;

    @Autowired
    private ProductTypeServiceImpl productTypeService;

    ProductType testSubjectFirst;
    ProductType testSubjectSecond;

    @BeforeAll
    public void setUp() {
        testSubjectFirst = new ProductType();
        testSubjectFirst.setName("Test Subject");
        testSubjectFirst.setDescription("Test Description");

        testSubjectSecond = new ProductType();
        testSubjectSecond.setName("Test Subject");
        testSubjectSecond.setDescription("Test Description");
    }

    @AfterAll
    public void tearDown() {
        productTypeRepository.deleteAll();
    }

    @Test
    @Order(1)
    public void createProductType() {
        ProductType saved = assertDoesNotThrow(() -> productTypeService.createProductType(testSubjectFirst));

        assertNotNull(saved.getId());
        assertEquals(saved.getName(), testSubjectFirst.getName());
        assertEquals(saved.getDescription(), testSubjectFirst.getDescription());

        assertThrows(AlreadyExistsException.class, () -> productTypeService.createProductType(testSubjectSecond));

        testSubjectFirst.setId(saved.getId());
        testSubjectSecond.setName("Second Subject Name");
        testSubjectSecond = assertDoesNotThrow(() -> productTypeService.createProductType(testSubjectSecond));
    }

    @Test
    @Order(2)
    public void findProductType() {
        assertThrows(NotFoundException.class, () -> productTypeService.findProductType("random id"));
        ProductType fetched = assertDoesNotThrow(() -> productTypeService.findProductType(testSubjectFirst.getId()));

        assertEquals(fetched.getId(), testSubjectFirst.getId());
        assertEquals(fetched.getName(), testSubjectFirst.getName());
        assertEquals(fetched.getDescription(), testSubjectFirst.getDescription());
    }

    @Test
    @Order(3)
    public void updateProductType() {
        assertThrows(NotFoundException.class, () -> productTypeService.findProductType("random id"));
        assertThrows(AlreadyExistsException.class, () -> productTypeService.updateProductType(testSubjectFirst.getId(), testSubjectSecond));

        testSubjectFirst.setName("Edited Subject Name");
        testSubjectFirst.setDescription("Edited Subject Description");

        ProductType edited = assertDoesNotThrow(() -> productTypeService.updateProductType(testSubjectFirst.getId(), testSubjectFirst));
        assertEquals(edited.getId(), testSubjectFirst.getId());
        assertEquals(edited.getName(), testSubjectFirst.getName());
        assertEquals(edited.getDescription(), testSubjectFirst.getDescription());
    }

    @Test
    @Order(4)
    public void findAllProductType() {
        List<ProductType> productTypes = assertDoesNotThrow(() -> productTypeRepository.findAll());
        assertEquals(productTypes.size(), 2);
    }

    @Test
    @Order(5)
    public void deleteProductType() {
        assertThrows(NotFoundException.class, () -> productTypeService.deleteProductType("random id"));
        assertDoesNotThrow(() -> productTypeService.deleteProductType(testSubjectFirst.getId()));
        assertDoesNotThrow(() -> productTypeService.deleteProductType(testSubjectSecond.getId()));
    }

    @Test
    @Order(6)
    public void findAllProductTypesEmpty() {
        List<ProductType> productTypes = assertDoesNotThrow(() -> productTypeRepository.findAll());
        assertEquals(productTypes.size(), 0);
    }

}
