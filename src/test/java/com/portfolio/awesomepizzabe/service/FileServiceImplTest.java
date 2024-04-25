package com.portfolio.awesomepizzabe.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.model.Filters;
import com.portfolio.awesomepizzabe.AwesomePizzaBeApplication;
import com.portfolio.awesomepizzabe.config.exceptions.status.BadRequestException;
import com.portfolio.awesomepizzabe.config.exceptions.status.NotFoundException;
import com.portfolio.awesomepizzabe.service.impl.FileServiceImpl;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = {AwesomePizzaBeApplication.class})
public class FileServiceImplTest {

    @Autowired
    private FileServiceImpl fileService;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    MongoOperations mongoOperations;

    @Autowired
    private ResourceLoader resourceLoader;

    String id;
    MultipartFile image;
    MultipartFile text;

    @BeforeAll
    public void setUp() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:pizza_margherita.jpg");
        image = new MockMultipartFile(
                Objects.requireNonNull(resource.getFilename()).split(",")[0],
                Objects.requireNonNull(resource.getFilename()),
                "image/jpg",
                resource.getInputStream());
        resource = resourceLoader.getResource("classpath:not_an_image.txt");
        text = new MockMultipartFile(
                Objects.requireNonNull(resource.getFilename()).split(",")[0],
                Objects.requireNonNull(resource.getFilename()),
                "text/plain",
                resource.getInputStream());
    }

    @Test
    @Order(1)
    public void saveFileTest() throws IOException {
        assertThrows(BadRequestException.class, () -> fileService.saveFile(text));
        id = assertDoesNotThrow(() -> fileService.saveFile(image) );

        Bson filter = Filters.eq("_id", new ObjectId(id));
        Document doc = mongoOperations.getCollection("fs.files").find(filter).first();
        assertNotNull(doc);
        assertNotNull(doc.get("metadata"));
        assertTrue((Boolean)((Map<String,Object>)doc.get("metadata")).get("temp"));

    }

    @Test
    @Order(2)
    public void getFileAsStream() {
        assertThrows(NotFoundException.class, () -> fileService.getFileAsStream("id_does_not_exist"));
        assertDoesNotThrow(() -> fileService.getFileAsStream(id));
    }

    @Test
    @Order(3)
    public void setTemp() {
        assertThrows(NotFoundException.class, () -> fileService.setTemp("id_does_not_exist", false));
        assertDoesNotThrow(() -> fileService.setTemp(id, false));

        Bson filter = Filters.eq("_id", new ObjectId(id));
        Document doc = mongoOperations.getCollection("fs.files").find(filter).first();
        assertNotNull(doc);
        assertNotNull(doc.get("metadata"));
        assertFalse((Boolean)((Map<String,Object>)doc.get("metadata")).get("temp"));
    }

    @Test
    @Order(4)
    public void deleteFile() {
        assertDoesNotThrow(() -> fileService.deleteFile(id));
        assertNull(gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(id))));
    }


}
