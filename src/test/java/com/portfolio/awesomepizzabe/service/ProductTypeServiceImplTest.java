package com.portfolio.awesomepizzabe.service;

import com.portfolio.awesomepizzabe.AwesomePizzaBeApplication;
import com.portfolio.awesomepizzabe.config.exceptions.AlreadyExistsException;
import com.portfolio.awesomepizzabe.config.exceptions.AssociatedEntityException;
import com.portfolio.awesomepizzabe.config.exceptions.status.NotFoundException;
import com.portfolio.awesomepizzabe.dto.ProductTypeDetailDTO;
import com.portfolio.awesomepizzabe.model.Product;
import com.portfolio.awesomepizzabe.model.ProductType;
import com.portfolio.awesomepizzabe.repository.ProductRepository;
import com.portfolio.awesomepizzabe.repository.ProductTypeRepository;
import com.portfolio.awesomepizzabe.service.impl.ProductTypeServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = {AwesomePizzaBeApplication.class})
public class ProductTypeServiceImplTest {

    @Autowired
    private ProductTypeRepository productTypeRepository;

    @Autowired
    private ProductTypeServiceImpl productTypeService;

    @Autowired
    private ProductRepository productRepository;

    Product testProduct;
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
        productRepository.deleteAll();
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
        assertThrows(NotFoundException.class, () -> productTypeService.updateProductType("random id",testSubjectSecond));
        assertThrows(AlreadyExistsException.class, () -> productTypeService.updateProductType(testSubjectFirst.getId(), testSubjectSecond));

        testSubjectFirst.setDescription("Edited Subject Description");

        ProductType edited = assertDoesNotThrow(() -> productTypeService.updateProductType(testSubjectFirst.getId(), testSubjectFirst));
        assertEquals(edited.getId(), testSubjectFirst.getId());
        assertEquals(edited.getName(), testSubjectFirst.getName());
        assertEquals(edited.getDescription(), testSubjectFirst.getDescription());

        testSubjectFirst.setName("Edited Subject Name");
        edited = assertDoesNotThrow(() -> productTypeService.updateProductType(testSubjectFirst.getId(), testSubjectFirst));
        assertEquals(edited.getId(), testSubjectFirst.getId());
        assertEquals(edited.getName(), testSubjectFirst.getName());
        assertEquals(edited.getDescription(), testSubjectFirst.getDescription());
    }

    @Test
    @Order(4)
    public void findAllProductTypes() {
        Page<ProductType> productTypes = assertDoesNotThrow(() -> productTypeService.findAllProductTypes(Pageable.unpaged()));
        assertEquals(productTypes.getTotalElements(), 2);
    }

    @Test
    @Order(5)
    public void deleteProductTypeThrows() {
        assertThrows(NotFoundException.class, () -> productTypeService.deleteProductType("random id"));

        testProduct = new Product();
        testProduct.setProductType(testSubjectFirst);
        testProduct.setImageId("not-a-real-id");
        testProduct = productRepository.save(testProduct);

        assertThrows(AssociatedEntityException.class, () -> productTypeService.deleteProductType(testSubjectFirst.getId()));
    }

    @Test
    @Order(6)
    public void findAllProductTypeDetails() {
        List<ProductTypeDetailDTO> productTypes = assertDoesNotThrow(() -> productTypeService.findAllProductTypeDetails());
        assertEquals(productTypes.size(), 2);

        ProductTypeDetailDTO dto = productTypes.stream()
                .filter(type -> type.getId().equals(testSubjectFirst.getId()))
                .findFirst().orElseThrow();

        assertEquals(dto.getId(), testSubjectFirst.getId());
        assertEquals(dto.getName(), testSubjectFirst.getName());
        assertEquals(dto.getDescription(), testSubjectFirst.getDescription());
        assertEquals(dto.getCount(), 1);
        assertEquals(dto.getImageId(), testProduct.getImageId());

        testProduct.setImageId(null);
        testProduct = productRepository.save(testProduct);

        productTypes = assertDoesNotThrow(() -> productTypeService.findAllProductTypeDetails());
        assertEquals(productTypes.size(), 2);

        dto = productTypes.stream()
                .filter(type -> type.getId().equals(testSubjectFirst.getId()))
                .findFirst().orElseThrow();

        assertEquals(dto.getId(), testSubjectFirst.getId());
        assertEquals(dto.getName(), testSubjectFirst.getName());
        assertEquals(dto.getDescription(), testSubjectFirst.getDescription());
        assertEquals(dto.getCount(), 1);
        assertNull(dto.getImageId());

        productRepository.delete(testProduct);

        productTypes = assertDoesNotThrow(() -> productTypeService.findAllProductTypeDetails());
        assertEquals(productTypes.size(), 2);

        dto = productTypes.stream()
                .filter(type -> type.getId().equals(testSubjectFirst.getId()))
                .findFirst().orElseThrow();

        assertEquals(dto.getId(), testSubjectFirst.getId());
        assertEquals(dto.getName(), testSubjectFirst.getName());
        assertEquals(dto.getDescription(), testSubjectFirst.getDescription());
        assertEquals(dto.getCount(), 0);
        assertNull(dto.getImageId());

    }

    @Test
    @Order(7)
    public void deleteProductTypeSuccess() {
        assertDoesNotThrow(() -> productTypeService.deleteProductType(testSubjectFirst.getId()));
        assertDoesNotThrow(() -> productTypeService.deleteProductType(testSubjectSecond.getId()));
    }

    @Test
    @Order(8)
    public void findAllProductTypesEmpty() {
        List<ProductType> productTypes = assertDoesNotThrow(() -> productTypeRepository.findAll());
        assertEquals(productTypes.size(), 0);
    }

}
