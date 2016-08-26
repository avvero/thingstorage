package com.avvero.thingstorage.service;

import com.avvero.thingstorage.domain.EntityFile;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by avvero on 12.02.14.
 */
public class UploadedFile {

    private EntityFile item;
    private byte[] data;
    private Map<String, String> params;

    public EntityFile getItem() {
        return item;
    }

    public void setItem(EntityFile item) {
        this.item = item;
    }

    public Map<String, String> getParams() {
        if (params == null) {
            params = new HashMap<>();
        }
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
