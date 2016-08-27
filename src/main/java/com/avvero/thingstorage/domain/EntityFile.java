package com.avvero.thingstorage.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by avvero on 26.08.2016.
 */
@Data
@Entity
public class EntityFile {

    @Id
    @GeneratedValue
    private Integer id;
    private String guid;
    private String name;
    private String type;
    private Long size;
    private Date created;

    private Integer userId;

}
