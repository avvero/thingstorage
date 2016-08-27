package com.avvero.thingstorage.controller;

import com.avvero.thingstorage.domain.EntityFile;
import com.avvero.thingstorage.exception.ThingStorageException;
import com.avvero.thingstorage.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by avvero on 26.08.2016.
 */
@RestController
public class StorageController {

    @Autowired
    StorageService storageService;

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public EntityFile upload(@RequestParam("file") MultipartFile file) throws ThingStorageException {
        return storageService.upload(file);
    }

    @RequestMapping(value = "/remove/{name}", method = RequestMethod.DELETE)
    public void remove(@PathVariable String name) throws ThingStorageException {
        storageService.remove(name);
    }

}
