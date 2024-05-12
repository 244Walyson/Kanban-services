package com.waly.kanban.dto;

import org.springframework.web.multipart.MultipartFile;


public class FileResponseDTO {

    private String uri;

    public FileResponseDTO(String uri) {
        this.uri = uri;
    }
    public FileResponseDTO() {
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
