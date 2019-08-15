package com.lh.hermes.service;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.lh.hermes.aidl.HermesAidl;

/**
 * user：LuHao
 * time：2019/8/15 10:13
 * describe：跨进程通信的服务端
 */
public class HermesService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return new HermesAidl();
    }
}
