package com.lh.hermes.service;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.lh.hermes.interfaces.IUserBean;
import com.library.hermes.Hermes;
import com.library.hermes.HermesListener;

/**
 * user：LuHao
 * time：2019/8/15 10:13
 * describe：跨进程通信的Hermes
 */
public class ThreadService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        //在连接之前给Hermes设置监听器
        Hermes.setHermesListener(new HermesListener() {
            @Override
            public void onHermesConnected(Class<? extends com.library.hermes.HermesService> service) {
                //连接成功，首先获取单例
                IUserBean iUserBean = Hermes.newInstance(IUserBean.class, 1, "admin", 9999D);
                Log.i("appjson", "当前进程传递的值：" + iUserBean.getName());
            }
        });
        //连接Hermes服务
        Hermes.connect(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //断开Hermes服务
        Hermes.disconnect(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
