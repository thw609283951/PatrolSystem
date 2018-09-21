package com.supersit.common.base;

/**
 * Created by Administrator on 2018/1/27.
 */
public class BaseEntity {
    private int id;
    private String name;
    public BaseEntity(int id, String name) {
        this.id = id;
        this.name = name;
    }
    public int getId() {
        return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
