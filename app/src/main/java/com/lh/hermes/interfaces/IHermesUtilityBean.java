package com.lh.hermes.interfaces;

import com.library.hermes.annotation.ClassId;

/**
 * user：LuHao
 * time：2019/8/16 10:51
 * describe：测试hermes的接口，使用getUtilityClass去调用方法，方法需要使用static
 */
@ClassId("HermesUtilityBean")
public interface IHermesUtilityBean {

    int add(int a, int b);

    int sub(int a, int b);

    int multiple(int a, int b);

    int division(int a, int b);
}
