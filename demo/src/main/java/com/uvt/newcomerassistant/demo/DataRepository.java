package com.uvt.newcomerassistant.demo;

public interface DataRepository {
    AppData loadData() throws DataLoadException;
    void saveData(AppData data) throws DataSaveException;
}
