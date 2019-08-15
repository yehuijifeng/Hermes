package com.lh.hermes.aidl;

import android.util.Log;

import com.lh.hermes.bean.HermesBean;
import com.lh.hermes.bean.IHermesService;

/**
 * user：LuHao
 * time：2019/8/15 11:57
 * describe：实现adil的接口，暴露给客户端使用
 */
public class HermesAidl extends IHermesService.Stub {
    @Override
    public HermesBean getHermesBean() {
        return new HermesBean(1, "admin", 9999D);
    }

    @Override
    public void setHermesBean(int id, String name, double money) {
        HermesBean hermesBean = new HermesBean(id, name, money);
        Log.i("appjson", "其他进程传递进来的：" + hermesBean.toString());
    }
}
