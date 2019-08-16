package com.lh.hermes.interfaces;

import com.lh.hermes.bean.HermesMethodBean;
import com.library.hermes.annotation.ClassId;

/**
 * user：LuHao
 * time：2019/8/16 11:10
 * describe：提供给不同进程回调的接口，因为跨进程回调只能是接口不能是抽象类
 */
@ClassId("IHermesOtherListener")
public interface IHermesOtherListener {

    void getVoid();

    int getBackInteger();

    void setInteger(int i);

    String getBackString();

    void setString(String str);

    HermesMethodBean getHermesMethod();

    void setHermesMethod(HermesMethodBean hermesMethod);
}

