package com.waly.kanban.controllers;

import com.waly.kanban.dto.UriDTO;
import com.waly.kanban.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService service;

    @PostMapping("/image")
    public ResponseEntity<UriDTO> uploadImage(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(service.uploadFile(file));
    }

}
