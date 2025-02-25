package com.example.aws.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public interface DocumentService {
    void uploadDocument( String fileName, MultipartFile file) throws IOException;

    ByteArrayOutputStream downloadDocument(String documentId) throws IOException;
}
