package com.waly.auth_service.dtos;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UriDTO {

    private String uri;

    public UriDTO() {}
    public UriDTO(String uri) {
        this.uri = uri;
    }

}
