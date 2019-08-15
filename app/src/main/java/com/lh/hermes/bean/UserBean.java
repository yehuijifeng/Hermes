package com.lh.hermes.bean;

import android.util.Log;

import com.library.hermes.annotation.ClassId;
import com.library.hermes.annotation.MethodId;

/**
 * user：LuHao
 * time：2019/8/15 15:05
 * describe：测试跨进程hermes的实体类
 */
@ClassId("UserBean")
public class UserBean {
    private int id;
    private String name;
    private double money;

    public UserBean() {
    }

    public UserBean(int id, String name, double money) {
        this.id = id;
        this.name = name;
        this.money = money;
        Log.i("appjson", id + ">" + name + ">" + money);
    }

    public static int add(int a, int b) {
        return a + b;
    }

    @MethodId("getId")
    public int getId() {
        return id;
    }

    @MethodId("setId")
    public void setId(int id) {
        this.id = id;
    }

    @MethodId("getName")
    public String getName() {
        return name;
    }

    @MethodId("setName")
    public void setName(String name) {
        this.name = name;
    }

    @MethodId("getMoney")
    public double getMoney() {
        return money;
    }

    @MethodId("setMoney")
    public void setMoney(double money) {
        this.money = money;
    }

    @MethodId("toString")
    @Override
    public String toString() {
        return "UserBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", money=" + money +
                '}';
    }
}
