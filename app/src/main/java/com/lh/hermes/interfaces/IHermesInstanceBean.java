package com.lh.hermes.interfaces;

import com.library.hermes.annotation.ClassId;

/**
 * User: LuHao
 * Date: 2019/8/15 22:43
 * Describe:UserUpperBean的对应接口。使用getInstance去获取对象，获取的方法必须是static且带有@GetInstance注解
 */
@ClassId("HermesInstanceBean")
public interface IHermesInstanceBean {

    int getId();

    void setId(int id);

    String getName();

    void setName(String name);

}
