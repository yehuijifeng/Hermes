package com.lh.hermes.interfaces;

import com.library.hermes.annotation.ClassId;

/**
 * User: LuHao
 * Date: 2019/8/15 22:43
 * Describe:UserUpperBean的对应接口
 */
@ClassId("UserInstanceBean")
public interface IUserInstanceBean {

    int getId();

    void setId(int id);

    String getName();

    void setName(String name);

}
