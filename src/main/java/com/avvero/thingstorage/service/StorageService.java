package com.avvero.thingstorage.service;

import com.avvero.thingstorage.dao.StoredFileRepository;
import com.avvero.thingstorage.domain.StoredFile;
import com.avvero.thingstorage.exception.ThingStorageException;
import com.avvero.thingstorage.utils.CommonUtils;
import com.avvero.thingstorage.utils.ImageUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by avvero on 27.08.2016.
 */
@Slf4j
@Service
public class StorageService {

    @Value("${file.store.originals}")
    public String fileStoreOriginals;
    @Value("${file.store.compressed}")
    public String fileStoreCompressed;
    @Value("${file.store.cached}")
    public String fileStoreCached;
    @Value("#{'${file.types.allowed}'.split(',')}")
    public List<String> allowedTypes;
    @Value("${file.maxsize}")
    public Long fileMaxSize;
    @Value("${file.cache.method}")
    public String cacheMethod;
    @Autowired
    StoredFileRepository storedFileRepository;

    /**
     * Upload
     * @param file
     * @return
     * @throws ThingStorageException
     */
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
                long startTime = System.currentTimeMillis();

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
                log.info(String.format("Storing of the file %s is complete in %s ms", fileName,
                        System.currentTimeMillis() - startTime));
                // Make compressed copy
                compress(fileStoreOriginals, fileStoreCompressed, fileName);
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

    /**
     * Remove
     * @param guid
     */
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

    /**
     * Original file
     * @param name
     * @return
     */
    public Pair<StoredFile, File> getOriginal(String name) {
        StoredFile storedFile = storedFileRepository.findOneByGuid(name);
        if (storedFile != null) {
            File file = new File(Paths.get(fileStoreOriginals, storedFile.getName()).toUri());
            return new ImmutablePair<>(storedFile, file);
        } else {
            throw new ThingStorageException(String.format("File %s does not exists.", name));
        }
    }

    /**
     * Compressed file
     * @param name
     * @return
     */
    public Pair<StoredFile, File> getCompressed(String name) {
        StoredFile storedFile = storedFileRepository.findOneByGuid(name);
        if (storedFile != null) {
            File file = new File(Paths.get(fileStoreCompressed, storedFile.getName()).toUri());
            return new ImmutablePair<>(storedFile, file);
        } else {
            throw new ThingStorageException(String.format("File %s does not exists.", name));
        }
    }

    /**
     * Get cached
     * @param name
     * @param w
     * @param h
     * @return
     */
    public Pair<StoredFile, File> getCached(String name, int w, int h) {
        StoredFile storedFile = storedFileRepository.findOneByGuid(name);
        if (storedFile != null) {
            String cachedDir = String.format("%s/%sx%s", fileStoreCached, w, h);
            File file = new File(Paths.get(cachedDir, storedFile.getName()).toUri());
            if (!file.exists()) {
                cache(storedFile, w, h);
                file = new File(Paths.get(cachedDir, storedFile.getName()).toUri());
            }
            return new ImmutablePair<>(storedFile, file);
        } else {
            throw new ThingStorageException(String.format("File %s does not exists.", name));
        }
    }

    private void cache(StoredFile storedFile, int w, int h) {
        long startTime = System.currentTimeMillis();

        String cachedDir = String.format("%s/%sx%s", fileStoreCached, w, h);
        String ext = FilenameUtils.getExtension(storedFile.getName());
        ImageUtils.resizeThroughScalr(fileStoreOriginals, cachedDir, storedFile.getGuid(), ext, w, h, cacheMethod);

        log.info(String.format("Resizing of the file %s is complete in %s ms", storedFile.getName(),
                System.currentTimeMillis() - startTime));
    }

    public String getGuid() {
        String guid;
        do {
            guid = UUID.randomUUID().toString();
        } while (storedFileRepository.findOneByGuid(guid) != null);
        return guid;
    }

    /**
     * File compression with quality = 0.7f
     * @param fileStoreOriginals
     * @param fileStoreCompressed
     * @param fileName
     * @throws ThingStorageException
     */
    public static void compress(String fileStoreOriginals, String fileStoreCompressed, String fileName)
            throws ThingStorageException {
        long startTime = System.currentTimeMillis();
        File imageFile = new File(Paths.get(fileStoreOriginals, fileName).toUri());
        File compressedImageFile = new File(Paths.get(fileStoreCompressed, fileName).toUri());

        try (InputStream is = new FileInputStream(imageFile);
             OutputStream os = new FileOutputStream(compressedImageFile)) {
            ImageOutputStream ios = ImageIO.createImageOutputStream(os);

            float quality = 0.7f;
            BufferedImage image = ImageIO.read(is);
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
            if (!writers.hasNext())
                throw new IllegalStateException("No writers found");

            ImageWriter writer = writers.next();
            writer.setOutput(ios);
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);
            writer.write(null, new IIOImage(image, null, null), param);
            writer.dispose();
        } catch (IOException e) {
            throw new ThingStorageException(e);
        }
        log.info(String.format("Compression of the file %s is complete in %s ms", fileName,
                System.currentTimeMillis() - startTime));
    }
}
