package com.lh.hermes.interfaces;

import com.library.hermes.annotation.ClassId;
import com.library.hermes.annotation.MethodId;

/**
 * user：LuHao
 * time：2019/8/15 15:12
 * describe：测试hermes的接口，使用newInstance去new一个对象，方法不需要使用static
 */
@ClassId("HermesMethodBean")
public interface IHermesMethodBean {

    @MethodId("getId")
    int getId();

    @MethodId("setId")
    void setId(int id);

    @MethodId("getName")
    String getName();

    @MethodId("setName")
    void setName(String name);

    @MethodId("getMoney")
    double getMoney();

    @MethodId("setMoney")
    void setMoney(double money);

    @MethodId("toString")
    String toString();
}
