package com.avvero.thingstorage.service;

import com.avvero.thingstorage.dao.EntityFileRepository;
import com.avvero.thingstorage.domain.EntityFile;
import com.avvero.thingstorage.exception.ThingStorageException;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Created by avvero on 26.08.2016.
 */
@Service
public class StorageService {

    @Autowired
    EntityFileRepository entityFileRepository;
    @Value("${file.types.allowed}")
    public List<String> allowedTypes;
    @Value("${file.store}")
    public String fileStore;
    @Value("${file.repo}")
    public String fileRepo;
    @Value("${file.maxsize}")
    public Integer maxSize;

    public UploadedFile uploadHandler(HttpServletRequest request) throws ThingStorageException {
        UploadedFile uploadedImage = new UploadedFile();
        EntityFile entryFile = null;
        DiskFileItemFactory factory = new DiskFileItemFactory();
        File repository = new File(fileRepo);
        factory.setRepository(repository);
        ServletFileUpload upload = new ServletFileUpload(factory);
        String fileName = null;
        try {
            // Parse the request
            List<FileItem> items = upload.parseRequest(request);
            // Process the uploaded items
            Iterator<FileItem> iter = items.iterator();
            while (iter.hasNext()) {
                FileItem item = iter.next();
                if (!item.isFormField()) {
                    fileName = item.getName();
                    entryFile = new EntityFile();
                    entryFile.setUserId(null); //TODO
                    entryFile.setGuid(UUID.randomUUID().toString());
                    entryFile.setSize(item.getSize());
                    entryFile.setType(item.getContentType());
                    //TODO
//                    if (FileDAO.isFileNameBusy(user, item.getName(), DownloadUtils.wrapParent(parentId))) {
//                        String msg = String.format("Файл с именем %s уже присутствует в папке. Файл не будет загружен.",
//                                item.getName());
//                        throw new ThingStorageException(msg);
//                    }
                    if (maxSize < (item.getSize())) {
                        String msg = String.format("Размер файла %s превышает допустимый (%s). Файл не будет загружен.",
                                item.getName(), maxSize);
                        throw new ThingStorageException(msg);
                    }
                    // Проверка файлов по типам
                    if (allowedTypes != null && allowedTypes.size() > 0) {
                        if (!allowedTypes.contains(entryFile.getType())) {
                            throw new ThingStorageException("Файл типа '" + entryFile.getType() + "' загрузить нельзя.");
                        }
                    }
                    entryFile.setCreated(new Date());
                    // Обрабатываем родителя
                    String filePath = fileStore;
                    String ext = FilenameUtils.getExtension(item.getName());
                    File uploadedFile = new File(filePath, entryFile.getGuid() + "." + ext);
                    uploadedFile.getParentFile().mkdirs(); //создадим все директории
                    item.write(uploadedFile);

                    entityFileRepository.save(entryFile);
                } else {
                    uploadedImage.getParams().put(item.getFieldName(), item.getString());
                }
            }
        } catch (ThingStorageException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new ThingStorageException("Не удалось загрузить файл " + fileName, exception);
        }
        uploadedImage.setItem(entryFile);
        return uploadedImage;
    }
}
