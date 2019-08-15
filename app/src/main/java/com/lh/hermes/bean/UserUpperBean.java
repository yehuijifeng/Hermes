package com.lh.hermes.bean;

import com.library.hermes.annotation.ClassId;

/**
 * user：LuHao
 * time：2019/8/15 17:19
 * describe：单例进程
 */
@ClassId("UserUpperBean")
public class UserUpperBean {
    private int id;
    private String name;
    private static UserUpperBean instance;

    private UserUpperBean() {
        id = 1;
        name = "无参单例";
    }

    public synchronized UserUpperBean getInstance() {
        if (instance == null) {
            instance = new UserUpperBean();
        }
        return instance;
    }

    public synchronized UserUpperBean getInstance(int id) {
        if (instance == null) {
            instance = new UserUpperBean();
        }
        this.id = id;
        return instance;
    }

    public synchronized UserUpperBean getInstance(String name) {
        if (instance == null) {
            instance = new UserUpperBean();
        }
        this.name = name;
        return instance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
