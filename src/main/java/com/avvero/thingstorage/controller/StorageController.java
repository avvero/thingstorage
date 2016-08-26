package com.avvero.thingstorage.controller;

import com.avvero.thingstorage.dao.EntityFileRepository;
import com.avvero.thingstorage.domain.EntityFile;
import com.avvero.thingstorage.exception.ThingStorageException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.UUID;

/**
 * Created by avvero on 26.08.2016.
 */
@RestController
public class StorageController {

    @Value("${file.store}")
    public String fileStore;
    @Autowired
    EntityFileRepository entityFileRepository;

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public EntityFile saveRationEntry(@RequestParam("file") MultipartFile file)
            throws ThingStorageException {
        if (!file.isEmpty()) {
            try {
                String guid = UUID.randomUUID().toString(); //TODO проверять на уникальность
                EntityFile entryFile = new EntityFile();
                entryFile.setUserId(null); //TODO
                entryFile.setGuid(guid);
                entryFile.setSize(file.getSize());
                entryFile.setType(file.getContentType());
                entryFile.setCreated(new Date());

                String ext = FilenameUtils.getExtension(file.getOriginalFilename());
                String fileName = String.format("%s.%s", guid, ext);
                Files.copy(file.getInputStream(), Paths.get(fileStore, fileName));
                entityFileRepository.save(entryFile);
                return entryFile;
            } catch (IOException e) {
                throw new ThingStorageException("Failed to upload " + file.getOriginalFilename() + " => "
                        + e.getMessage());
            }
        } else {
            throw new ThingStorageException("Failed to upload " + file.getOriginalFilename()
                    + " because it was empty");
        }
    }

}
