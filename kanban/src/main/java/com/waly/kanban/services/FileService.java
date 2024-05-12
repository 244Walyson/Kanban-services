package com.waly.kanban.services;

import com.waly.kanban.dto.FileResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

    @Autowired
    private S3Service s3Service;

    public FileResponseDTO uploadFile(MultipartFile file) {
        return new FileResponseDTO(s3Service.uploadFile(file).toString());
    }
}
