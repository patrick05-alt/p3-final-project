package com.uvt.newcomerassistant.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;

@Repository
public class JsonDataRepository implements DataRepository {
    private final Resource dataResource;
    private final ObjectMapper objectMapper;

    @Autowired
    public JsonDataRepository(@Value("classpath:data.json") Resource dataResource, ObjectMapper objectMapper) {
        this.dataResource = dataResource;
        this.objectMapper = objectMapper;
    }

    @Override
    public AppData loadData() throws DataLoadException {
        if (!dataResource.exists()) {
            return new AppData();
        }
        try (InputStream inputStream = dataResource.getInputStream()) {
            return objectMapper.readValue(inputStream, AppData.class);
        } catch (IOException e) {
            throw new DataLoadException("Failed to load data from " + dataResource.getDescription(), e);
        }
    }

    @Override
    public void saveData(AppData data) throws DataSaveException {
        throw new UnsupportedOperationException("Saving data is not supported in this implementation.");
    }
}
