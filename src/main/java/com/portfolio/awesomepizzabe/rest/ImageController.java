package com.portfolio.awesomepizzabe.rest;

import com.portfolio.awesomepizzabe.config.exceptions.status.BadRequestException;
import com.portfolio.awesomepizzabe.config.exceptions.status.NotFoundException;
import com.portfolio.awesomepizzabe.service.FileService;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/image")
public class ImageController {

    private final FileService fileService;
    public ImageController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * The uploadImage method allows to upload an image in the database. The image is set as temporary, so that if not linked to any entity it will be deleted.
     * If the uploaded file is not an image, the server will reply with a 400.
     * The method returns the id of the file after it has been saved.
     * @param file
     * @throws IOException (500 INTERNAL SERVER ERROR)
     * @throws BadRequestException if the uploaded file is not an image (400 BAD REQUEST)
     * @return the file id.
     */
    @PostMapping
    public ResponseEntity<String> uploadImage(@RequestPart("file") MultipartFile file) throws IOException {
        return ResponseEntity.status(201).body(fileService.saveFile(file));
    }

    /**
     * The downloadImage method allows to download an image from the database.
     * If the provided id is not linked to any Image on the database, the server replies with a 404.
     * The method returns a file.
     * @param id
     * @throws IOException (500 INTERNAL SERVER ERROR)
     * @throws NotFoundException there is no image linked to the given id (404 NOT FOUND)
     * @return the image from the database
     */
    @GetMapping("/{id}")
    public ResponseEntity<GridFsResource> downloadImage(@PathVariable String id) throws IOException {
        GridFsResource resource = fileService.getFileAsStream(id);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(resource.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+resource.getFilename()+"\"")
                .header(HttpHeaders.CONTENT_TYPE, resource.getContentType())
                .body(resource);
    }
}
