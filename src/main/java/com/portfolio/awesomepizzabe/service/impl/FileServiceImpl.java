package com.portfolio.awesomepizzabe.service.impl;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.portfolio.awesomepizzabe.config.exceptions.status.BadRequestException;
import com.portfolio.awesomepizzabe.config.exceptions.status.NotFoundException;
import com.portfolio.awesomepizzabe.service.FileService;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class FileServiceImpl implements FileService {

    private final GridFsTemplate gridFsTemplate;
    private final MongoOperations mongoOperations;

    public FileServiceImpl(GridFsTemplate gridFsTemplate, MongoOperations mongoOperations) {
        this.gridFsTemplate = gridFsTemplate;
        this.mongoOperations = mongoOperations;
    }


    /**
     * The saveFile method saves the given image on the server. The image is saved with a metadata map that contains a key-value pair of "temp":true.
     * If the given file is not an image, the method throws a BadRequestException.
     * @param file
     * @return
     * @throws IOException
     */
    @Override
    public String saveFile(MultipartFile file) throws IOException {
        if (Objects.requireNonNull(file.getContentType()).startsWith("image")) {
            return gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType(), new HashMap<>(Map.of("temp", true))).toString();
        } else {
            throw new BadRequestException("File type not supported");
        }
    }

    /**
     * the getFileAsStream returns the file as a resource from the database.
     * The file is found with the getFileFromDB method, and then transformed into a resource.
     * If there is no file linked to the given id, the method throws a NotFoundException
     * @param id
     * @throws NotFoundException if the file is not found.
     * @return the file as a resource
     */
    @Override
    public GridFsResource getFileAsStream(String id) {
        GridFSFile file = getFileFromDB(id);
        return gridFsTemplate.getResource(file);
    }

    /**
     * the setTemp method allows to set the value of the temp variable in the metadata of a file in the database.
     * The file is found from the database in order to get the full metadata map, which gets updated. Then, the file gets updated directly by using MongoOperations.
     * If there is no file linked to the given id, the method throws a NotFoundException.
     * @param id
     * @param temp
     * @throws NotFoundException if the file is not found.
     */
    @Override
    public void setTemp(String id, boolean temp) {

        GridFSFile file = getFileFromDB(id);
        Map<String, Object> metadata = file.getMetadata();
        metadata.put("temp", temp);

        Query query = Query.query(Criteria.where("_id").is(id));
        Update update = new Update().set("metadata", metadata);

        mongoOperations.updateFirst(query, update, "fs.files");
    }

    /**
     * the deleteFile deletes a file with the given id. It applies the operation directly, without checking if the file exists or not.
     * @param id
     */
    @Override
    public void deleteFile(String id) {
        gridFsTemplate.delete(new Query(Criteria.where("_id").is(id)));
    }

    /**
     * the getFileAsStream returns the file as a GridFSFile from the database.
     * If there is no file linked to the given id, the method throws a NotFoundException
     * @param fileId
     * @throws NotFoundException if the file is not found.
     * @return the GridFSFile
     */
    private GridFSFile getFileFromDB(String fileId) {
        GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(fileId)));
        if (file != null) {
            return file;
        }
        throw new NotFoundException("The file could not be found, too much time passed since the file insertion.");

    }
}
