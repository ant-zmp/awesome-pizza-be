package com.portfolio.awesomepizzabe.service;

import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    String saveFile(MultipartFile file) throws IOException;
    GridFsResource getFileAsStream(String id);
    void setTemp(String id, boolean temp);
    void deleteFile(String id);
}
