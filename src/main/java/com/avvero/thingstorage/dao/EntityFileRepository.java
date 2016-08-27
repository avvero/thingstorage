package com.avvero.thingstorage.dao;

import com.avvero.thingstorage.domain.EntityFile;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by avvero on 26.08.2016.
 */
public interface EntityFileRepository extends CrudRepository<EntityFile, Integer> {

    EntityFile findOneByGuid(String guid);

    EntityFile findOneByName(String fileName);

}
