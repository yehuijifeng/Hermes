package com.lh.hermes.bean;

import com.library.hermes.annotation.ClassId;

/**
 * user：LuHao
 * time：2019/8/16 9:53
 * describe：测试跨进程hermes的实体类。对应方法：Hermes.getUtilityClass(Class )
 * doc: 该方法指向的是class中的工具方法，必须是static
 */
@ClassId("HermesUtilityBean")
public class HermesUtilityBean {
//
//    private HermesUtilityBean() {
//        throw new UnsupportedOperationException("cannot be instance");
//    }

    public static int add(int a, int b) {
        return a + b;
    }

    public static int sub(int a, int b) {
        return a - b;
    }

    public static int multiple(int a, int b) {
        return a * b;
    }

    public static int division(int a, int b) {
        return a / b;
    }
}
