package com.unity.resource.entity;

import lombok.Data;

/**
 * Created by kazaff on 2014/12/1.
 */
@Data
public class FileDTO {

    private String md5;
    private int chunkIndex;
    private String size;
    private String name;
    private String userId;
    private String id;
    private int chunks;
    private int chunk;
    private String lastModifiedDate;
    private String type;
    private String ext;

    public FileDTO(){}

    public FileDTO(String name, String size, int chunkIndex){
        this.name = name;
        this.size = size;
        this.chunkIndex = chunkIndex;
    }

    public FileDTO(String userId, String id){
        this.userId = userId;
        this.id = id;
    }

    public FileDTO(String md5){
        this.md5 = md5;
    }

    public FileDTO(int chunks, int chunk, String userId, String id, String name, String size, String lastModifiedDate, String type){
        this.userId = userId;
        this.id = id;
        this.name = name;
        this.size = size;
        this.chunks = chunks;
        this.chunk = chunk;
        this.lastModifiedDate = lastModifiedDate;
        this.type = type;
    }

    public FileDTO(String name, int chunks, String ext, String md5){
        this.name = name;
        this.chunks = chunks;
        this.ext = ext;
        this.md5 = md5;
    }


}
