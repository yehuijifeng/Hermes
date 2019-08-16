package com.lh.hermes.interfaces;


import com.lh.hermes.bean.HermesMethodBean;
import com.library.hermes.annotation.Background;
import com.library.hermes.annotation.ClassId;
import com.library.hermes.annotation.WeakRef;

/**
 * user：LuHao
 * time：2019/8/16 11:34
 * describe：测试Hermes的其他注解功能
 */
@ClassId("HermesOtherBean")
public interface IHermesOtherBean {
    void setString1(String str);

    //传递的自定义参数不能是
    //1. new A(){}匿名类
    //2. private 私有类
    String customMethod(HermesMethodBean hermesMethodBean);

    //@WeakRef:持有当前回调的弱引用
    //@Background:不让回调函数运行在主线程
    void customBackMethod(HermesMethodBean hermesMethodBean, @WeakRef @Background IHermesOtherListener iHermesOtherListener);
}
