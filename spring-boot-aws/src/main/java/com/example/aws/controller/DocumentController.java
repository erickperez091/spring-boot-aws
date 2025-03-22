package com.example.aws.controller;

import com.example.aws.service.DocumentService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/document/v1")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(name = "upload_document", value = "/", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE },
            produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> uploadDocument( @RequestParam("file") MultipartFile file) throws IOException {
        documentService.uploadDocument( file.getOriginalFilename(), file);
        return ResponseEntity.ok(file.getOriginalFilename());
    }

    @GetMapping(name = "download_document", value = "/{document_id}", produces = { MediaType.APPLICATION_OCTET_STREAM_VALUE })
    public ResponseEntity< Resource > getDocument( @PathVariable String document_id) throws IOException {
        ByteArrayResource resource = new ByteArrayResource( documentService.downloadDocument( document_id ).toByteArray() );
        return new ResponseEntity<>( resource, HttpStatus.FOUND );

        // Different Approch for downloading a file from S3
        /*
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=img.jpg");
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");
        return ResponseEntity.status(HttpStatus.FOUND)
                .headers(header)
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .body(resource);
         */

    }
}
