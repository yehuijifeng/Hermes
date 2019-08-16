package com.lh.hermes.bean;

import android.util.Log;

import com.library.hermes.annotation.ClassId;
import com.library.hermes.annotation.MethodId;

/**
 * user：LuHao
 * time：2019/8/15 15:05
 * describe：测试跨进程hermes的实体类。对应方法：Hermes.newInstance(Class,Object…)
 * doc:该方法相当于new，所以被标记的class中的方法不需要static
 */
@ClassId("HermesMethodBean")
public class HermesMethodBean {
    private int id;
    private String name;
    private double money;

    public HermesMethodBean() {
    }

    public HermesMethodBean(int id, String name, double money) {
        this.id = id;
        this.name = name;
        this.money = money;
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
        return "HermesMethodBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", money=" + money +
                '}';
    }
}
