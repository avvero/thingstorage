package com.avvero.thingstorage.controller;

import com.avvero.thingstorage.domain.EntityFile;
import com.avvero.thingstorage.service.StorageService;
import com.avvero.thingstorage.service.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by avvero on 26.08.2016.
 */
@RestController
public class StorageController {

    @Autowired
    private StorageService storageService;

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public EntityFile saveRationEntry(HttpServletRequest request) {
        UploadedFile uploadedFile = storageService.uploadHandler(request);
        return uploadedFile.getItem();
    }

}
