package com.lh.hermes.service;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.lh.hermes.bean.HermesMethodBean;
import com.lh.hermes.interfaces.IHermesInstanceBean;
import com.lh.hermes.interfaces.IHermesMethodBean;
import com.lh.hermes.interfaces.IHermesOtherBean;
import com.lh.hermes.interfaces.IHermesOtherListener;
import com.library.hermes.Hermes;
import com.library.hermes.HermesListener;
import com.library.hermes.HermesService;

/**
 * user：LuHao
 * time：2019/8/15 10:13
 * describe：跨进程通信的Herms，测试其他Hermes的注解功能和注意事项
 */
public class HermesOtherService extends Service {
    public HermesMethodBean hermesMethodBean = new HermesMethodBean(1, "a", 1D);

    @Override
    public void onCreate() {
        super.onCreate();
        //在连接之前给Hermes设置监听器
        Hermes.setHermesListener(new HermesListener() {
            @Override
            public void onHermesConnected(Class<? extends HermesService> service) {
                //连接成功，首先获取单例
                try {
                    //静态方法没有限制,这里不清楚原因需要先调用getUtilityClass在调用newInstance才能正常使用
                    Hermes.getUtilityClass(IHermesOtherBean.class);
                    //如果是new的有限制
                    IHermesOtherBean iHermesOtherBeanNew = Hermes.newInstance(IHermesOtherBean.class);
                    if (iHermesOtherBeanNew == null)
                        Log.i("appjson", "当前IHermesOtherBean==null");
                    else {
                        //这种匿名类是错误的写法
//                        Log.i("appjson", iHermesOtherBean.customMethod(new HermesMethodBean(1, "aaaa", 1D)));
                        //这种private的对象也是错误的
//                        HermesMethodBean hermesMethodBean = new HermesMethodBean(1, "a", 1D);
//                        iHermesOtherBean.customMethod(hermesMethodBean);
                        //必须是非private且不是匿名类的对象
//                        hermesMethodBean = new HermesMethodBean(1, "a", 1D);
                        iHermesOtherBeanNew.setString1("bbbbbb");
                        Log.i("appjson", iHermesOtherBeanNew.customMethod(hermesMethodBean) + "aaa");
                        iHermesOtherBeanNew.customBackMethod(hermesMethodBean, new IHermesOtherListener() {
                            @Override
                            public void getVoid() {
                                Log.i("appjson", "getVoid()");
                            }

                            @Override
                            public int getBackInteger() {
                                Log.i("appjson", "getBackInteger()");
                                return 100;
                            }

                            @Override
                            public void setInteger(int i) {
                                Log.i("appjson", "setInteger(" + i + ")");

                            }

                            @Override
                            public String getBackString() {
                                Log.i("appjson", "getBackString()");
                                return "getBackString-admin";
                            }

                            @Override
                            public void setString(String str) {
                                Log.i("appjson", "setString(" + str + ")");
                            }

                            @Override
                            public HermesMethodBean getHermesMethod() {
                                Log.i("appjson", "getHermesMethod()");
                                return new HermesMethodBean(50, "test", 99D);
                            }

                            @Override
                            public void setHermesMethod(HermesMethodBean hermesMethod) {
                                Log.i("appjson", "setHermesMethod():" + hermesMethod.toString());
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.i("appjson", "error:" + e.getMessage());
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
