package com.avvero.thingstorage.dao;

import com.avvero.thingstorage.domain.StoredFile;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by avvero on 26.08.2016.
 */
public interface StoredFileRepository extends CrudRepository<StoredFile, Integer> {

    StoredFile findOneByGuid(String guid);

}
