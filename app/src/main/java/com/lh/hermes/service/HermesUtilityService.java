package com.lh.hermes.service;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.lh.hermes.interfaces.IHermesInstanceBean;
import com.lh.hermes.interfaces.IHermesMethodBean;
import com.lh.hermes.interfaces.IHermesUtilityBean;
import com.library.hermes.Hermes;
import com.library.hermes.HermesListener;
import com.library.hermes.HermesService;

/**
 * user：LuHao
 * time：2019/8/15 10:13
 * describe：跨进程通信的Herms，测试工具类静态方法
 */
public class HermesUtilityService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        //在连接之前给Hermes设置监听器
        Hermes.setHermesListener(new HermesListener() {
            @Override
            public void onHermesConnected(Class<? extends HermesService> service) {
                //连接成功，首先获取单例
                try {
                    IHermesUtilityBean maths = Hermes.getUtilityClass(IHermesUtilityBean.class);
                    if (maths == null)
                        Log.i("appjson", "当前IHermesUtilityBean==null");
                    else {
                        Log.i("appjson", "当前IHermesUtilityBean传递的值(+)：" + maths.add(1, 2));
                        Log.i("appjson", "当前IHermesUtilityBean传递的值(-)：" + maths.sub(1, 2));
                        Log.i("appjson", "当前IHermesUtilityBean传递的值(*)：" + maths.multiple(1, 2));
                        Log.i("appjson", "当前IHermesUtilityBean传递的值(/)：" + maths.division(1, 2));
                    }
                } catch (Exception e) {
                    Log.i("appjson", e.getMessage());
                }
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
