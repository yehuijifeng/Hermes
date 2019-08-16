package com.lh.hermes.bean;

import android.util.Log;

import com.lh.hermes.interfaces.IHermesOtherListener;
import com.library.hermes.annotation.ClassId;
import com.library.hermes.annotation.MethodId;

/**
 * user：LuHao
 * time：2019/8/16 11:02
 * describe：测试Hermes的其他注解和注意事项
 */
//@WithinProcess//不让其他进程访问
@ClassId("HermesOtherBean")
public class HermesOtherBean {

    public void setString1(String str) {
        Log.i("appjson", "setString:" + str);
    }

    //传递的自定义参数不能是
    //1. new A(){}匿名类
    //2. private 私有类
    @MethodId("customMethod")
    public String customMethod(HermesMethodBean hermesMethodBean) {
        if (hermesMethodBean != null) {
            return hermesMethodBean.getId() % 2 == 0 ? "用户id为偶数" : "用户id为奇数";
        }
        return "HermesMethodBean==null";
    }

    //@WeakRef:持有当前回调的弱引用
    //@Background:不让回调函数运行在主线程
    public void customBackMethod(HermesMethodBean hermesMethodBean, IHermesOtherListener iHermesOtherListener) {
        if (hermesMethodBean != null) {
            iHermesOtherListener.getVoid();
            iHermesOtherListener.setInteger(100);
            int i = iHermesOtherListener.getBackInteger();
            iHermesOtherListener.setString("customBackMethod");
            String str = iHermesOtherListener.getBackString();
            iHermesOtherListener.setHermesMethod(hermesMethodBean);
            Log.i("appjson", "getbackInteger:" + i + ";getBackString:" + str);
        }
    }
}
