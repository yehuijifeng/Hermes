package com.lh.hermes.service;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.lh.hermes.interfaces.IUserBean;
import com.lh.hermes.interfaces.IUserInstanceBean;
import com.library.hermes.Hermes;
import com.library.hermes.HermesListener;

/**
 * user：LuHao
 * time：2019/8/15 10:13
 * describe：跨进程通信的Herms，当前传递的对象没有使用@MethodId标签
 */
public class SupperService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        //在连接之前给Hermes设置监听器
        Hermes.setHermesListener(new HermesListener() {
            @Override
            public void onHermesConnected(Class<? extends com.library.hermes.HermesService> service) {

                //连接成功，首先获取单例
                try {
                    IUserBean maths = Hermes.getUtilityClass(IUserBean.class);
                    if (maths == null)
                        Log.i("appjson1", "当前IUserBean==null");
                    else
                        Log.i("appjson1", "当前IUserBean传递的值：" + maths.add(1, 2));
                    IUserInstanceBean iUserUpperBean = Hermes.getInstance(IUserInstanceBean.class, "aaa");
                    if (iUserUpperBean == null)
                        Log.i("appjson1", "当前iUserUpperBean==null");
                    else
                        Log.i("appjson1", "当前iUserUpperBean传递的值：" + iUserUpperBean.getName());
                } catch (Exception e) {

                    Log.i("appjson1", e.getMessage());
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
