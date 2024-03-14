package com.waly.kanban;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class configs {

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }
}
