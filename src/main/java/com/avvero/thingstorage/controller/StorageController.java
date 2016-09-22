package com.avvero.thingstorage.controller;

import com.avvero.thingstorage.domain.StoredFile;
import com.avvero.thingstorage.exception.ThingStorageException;
import com.avvero.thingstorage.service.StorageService;
import com.avvero.thingstorage.utils.CommonUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * Created by avvero on 26.08.2016.
 */
@RestController
public class StorageController {

    @Value("${file.expire_duration_millis}")
    public Long fileExpireDurationMillis;

    @Autowired
    StorageService storageService;

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public StoredFile upload(@RequestParam("file") MultipartFile file) throws ThingStorageException {
        return storageService.upload(file);
    }

    @RequestMapping(value = "/remove/{name}", method = RequestMethod.DELETE)
    public void remove(@PathVariable String name) throws ThingStorageException {
        storageService.remove(name);
    }

    @RequestMapping(value = "/original/{name}", method = RequestMethod.GET)
    public void originals(@PathVariable String name, HttpServletResponse response) throws ThingStorageException {
        Pair<StoredFile, File> pair = storageService.getOriginal(name);
        writeFileToResponse(pair.getKey(), pair.getValue(), response);
    }

    @RequestMapping(value = "/compressed/{name}", method = RequestMethod.GET)
    public void compressed(@PathVariable String name, HttpServletResponse response) throws ThingStorageException {
        Pair<StoredFile, File> pair = storageService.getCompressed(name);
        writeFileToResponse(pair.getKey(), pair.getValue(), response);
    }

    @RequestMapping(value = "/cache/{wxh}/{name}", method = RequestMethod.GET)
    public void compressed(@PathVariable String wxh, @PathVariable String name, HttpServletResponse response)
            throws ThingStorageException {
        Pair<Integer, Integer> dimensions = CommonUtils.getDimensions(wxh);
        Pair<StoredFile, File> pair = storageService.getCached(name, dimensions.getLeft(), dimensions.getRight());
        writeFileToResponse(pair.getKey(), pair.getValue(), response);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<StoredFile> list() throws ThingStorageException {
        return storageService.list();
    }

    /**
     * Выгрузка файла
     * @param storedFile
     * @param file
     * @param response
     * @throws ThingStorageException
     */
    public void writeFileToResponse(StoredFile storedFile, File file, HttpServletResponse response)
            throws ThingStorageException {
        int length;
        try (DataInputStream in = new DataInputStream(new FileInputStream(file));
             ServletOutputStream outStream = response.getOutputStream()) {

            // sets HTTP header
            response.setHeader("Accept-Ranges", "bytes");
            response.setHeader("Content-Disposition", String.format("inline;filename=\"%s\"", storedFile.getName()));
            response.setDateHeader("Last-Modified", storedFile.getCreated().getTime());
            response.setDateHeader("Expires", System.currentTimeMillis() + fileExpireDurationMillis);
            response.setContentType(storedFile.getType());
            response.setContentLength((int) file.length());

            byte[] byteBuffer = new byte[4096];

            // reads the file's bytes and writes them to the response stream
            while ((in != null) && ((length = in.read(byteBuffer)) != -1)) {
                outStream.write(byteBuffer, 0, length);
            }
            in.close();
            outStream.close();
        } catch (IOException e) {
            throw new ThingStorageException(e);
        }
    }
}
