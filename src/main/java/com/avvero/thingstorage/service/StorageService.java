package com.avvero.thingstorage.service;

import com.avvero.thingstorage.dao.StoredFileRepository;
import com.avvero.thingstorage.domain.StoredFile;
import com.avvero.thingstorage.exception.ThingStorageException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by avvero on 27.08.2016.
 */
@Slf4j
@Service
public class StorageService {

    @Value("${file.store.originals}")
    public String fileStoreOriginals;
    @Value("#{'${file.types.allowed}'.split(',')}")
    public List<String> allowedTypes;
    @Value("${file.maxsize}")
    public Long fileMaxSize;
    @Autowired
    StoredFileRepository storedFileRepository;

    public StoredFile upload(MultipartFile file) throws ThingStorageException {
        if (!file.isEmpty()) {
            if (!allowedTypes.contains(file.getContentType())) {
                throw new ThingStorageException(String.format("Unsupported content type: %s. Supports only: %s.",
                        file.getContentType(), Arrays.toString(allowedTypes.toArray())));
            }
            if (fileMaxSize < file.getSize()) {
                throw new ThingStorageException(String.format("The file exceeds its maximum permitted size of %s bytes. ",
                        fileMaxSize));
            }
            try {
                String guid = getGuid();
                String ext = FilenameUtils.getExtension(file.getOriginalFilename());
                String fileName = String.format("%s.%s", guid, ext);

                StoredFile entryFile = new StoredFile();
                entryFile.setUserId(null); //TODO
                entryFile.setGuid(guid);
                entryFile.setName(fileName);
                entryFile.setSize(file.getSize());
                entryFile.setType(file.getContentType());
                entryFile.setCreated(new Date());

                Files.copy(file.getInputStream(), Paths.get(fileStoreOriginals, fileName));
                storedFileRepository.save(entryFile);
                return entryFile;
            } catch (IOException e) {
                log.error(e.getLocalizedMessage(), e);
                throw new ThingStorageException("Failed to upload " + file.getOriginalFilename() + " => "
                        + e.getMessage());
            }
        } else {
            throw new ThingStorageException("Failed to upload " + file.getOriginalFilename()
                    + " because it was empty");
        }
    }

    public void remove(String guid) {
        StoredFile storedFile = storedFileRepository.findOneByGuid(guid);
        if (storedFile != null) {
            Path path = Paths.get(fileStoreOriginals, storedFile.getName());
            try {
                Files.delete(path);
            } catch (IOException e) {
                log.error(e.getLocalizedMessage(), e);
                throw new ThingStorageException("Failed to remove " + path + " => "
                        + e.getMessage());
            }
            storedFileRepository.delete(storedFile);
        } else {
            log.warn(String.format("File %s does not exists. Skip removing.", guid));
        }
    }

    public String getGuid() {
        String guid;
        do {
            guid = UUID.randomUUID().toString();
        } while (storedFileRepository.findOneByGuid(guid) != null);
        return guid;
    }
}
