package com.lh.hermes.bean;

import com.library.hermes.annotation.ClassId;
import com.library.hermes.annotation.GetInstance;

/**
 * user：LuHao
 * time：2019/8/15 17:19
 * describe：单例进程，并且没有使用@MethodId注解
 */
@ClassId("UserInstanceBean")
public class UserInstanceBean {
    private int id;
    private String name;
    private static UserInstanceBean instance;

    private UserInstanceBean(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @GetInstance
    public synchronized static UserInstanceBean getInstance() {
        if (instance == null) {
            instance = new UserInstanceBean(1, "admin");
        }
        return instance;
    }

    @GetInstance
    public synchronized static UserInstanceBean getInstance(int id) {
        if (instance == null) {
            instance = new UserInstanceBean(id, "只有id");
        }
        return instance;
    }

    @GetInstance
    public synchronized static UserInstanceBean getInstance(String name) {
        if (instance == null) {
            instance = new UserInstanceBean(100, name);
        }
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
