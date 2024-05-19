package com.waly.kanban.services;

import com.waly.kanban.dto.UriDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

    @Autowired
    private S3Service s3Service;

    public UriDTO uploadFile(MultipartFile file) {
        return new UriDTO(s3Service.uploadFile(file).toString());
    }
}
