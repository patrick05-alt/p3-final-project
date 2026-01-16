package com.uvt.newcomerassistant.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class UvtNewcomerAssistantApplication {

    public static void main(String[] args) {
        SpringApplication.run(UvtNewcomerAssistantApplication.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Bean
    public AppData appData(DataRepository dataRepository) throws DataLoadException {
        return dataRepository.loadData();
    }

}
