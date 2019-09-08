/**
 * Copyright 2016 Xiaofei
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.library.hermes;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Proxy;

import com.library.hermes.internal.Channel;
import com.library.hermes.internal.HermesInvocationHandler;
import com.library.hermes.internal.Reply;
import com.library.hermes.sender.Sender;
import com.library.hermes.sender.SenderDesignator;
import com.library.hermes.util.HermesException;
import com.library.hermes.util.HermesGc;
import com.library.hermes.util.TypeCenter;
import com.library.hermes.util.TypeUtils;
import com.library.hermes.wrapper.ObjectWrapper;

/**
 * Created by Xiaofei on March 31, 2016.
 */
public class Hermes {

    private static final String TAG = "HERMES";
    //typecenter对象专门用于缓存
    private static final TypeCenter TYPE_CENTER = TypeCenter.getInstance();
    //Channel对象用于绑定和解绑跨进程的service
    private static final Channel CHANNEL = Channel.getInstance();

    private static final HermesGc HERMES_GC = HermesGc.getInstance();

    private static Context sContext = null;

    public static void register(Object object) {
        register(object.getClass());
    }

    //检查是否传递进来了context对象
    private static void checkInit() {
        if (sContext == null) {
            throw new IllegalStateException("Hermes has not been initialized.");
        }
    }

    /**
     * 如果方法的返回类型与方法的接口返回类型一致，则不不需要在本地进程中注册类!
     * 但是，如果方法的返回类型与方法的返回类型不完全相同，则应该注册该方法。
     *
     * @param clazz
     */
    public static void register(Class<?> clazz) {
        //检查是否有context对象
        checkInit();
        //注册该class，跟踪到TypeCenter.register(Class)方法
        TYPE_CENTER.register(clazz);
    }

    public static Context getContext() {
        return sContext;
    }

    //在app初始化的时候初始化hermes，就是得到该app的applicationcontext对象
    public static void init(Context context) {
        if (sContext != null) {
            return;
        }
        sContext = context.getApplicationContext();
    }

    private static void checkBound(Class<? extends HermesService> service) {
        if (!CHANNEL.getBound(service)) {
            throw new IllegalStateException("Service Unavailable: You have not connected the service "
                    + "or the connection is not completed. You can set HermesListener to receive a callback "
                    + "when the connection is completed.");
        }
    }
    //使用java的动态代理获得对象
    private static <T> T getProxy(Class<? extends HermesService> service, ObjectWrapper object) {
        Class<?> clazz = object.getObjectClass();
        T proxy = (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz},
                new HermesInvocationHandler(service, object));
        //在回收器中注册，以便于回收
        HERMES_GC.register(service, proxy, object.getTimeStamp());
        return proxy;
    }

    public static <T> T newInstance(Class<T> clazz, Object... parameters) {
        return newInstanceInService(HermesService.HermesService0.class, clazz, parameters);
    }

    /**
     * new一个对象的实例
     *
     * @param service
     * @param clazz
     * @param parameters
     * @param <T>
     * @return
     */
    public static <T> T newInstanceInService(Class<? extends HermesService> service, Class<T> clazz, Object... parameters) {
        //检查service是否符合要求，不为null，必须是接口
        TypeUtils.validateServiceInterface(clazz);
        //检查当前service是否绑定
        checkBound(service);
        //new一个ObjectWrapper对象，类型为TYPE_OBJECT_TO_NEW
        ObjectWrapper object = new ObjectWrapper(clazz, ObjectWrapper.TYPE_OBJECT_TO_NEW);
        //获得sender对象
        Sender sender = SenderDesignator.getPostOffice(service, SenderDesignator.TYPE_NEW_INSTANCE, object);
        try {
            Reply reply = sender.send(null, parameters);
            if (reply != null && !reply.success()) {
                Log.e(TAG, "Error occurs during creating instance. Error code: " + reply.getErrorCode());
                Log.e(TAG, "Error message: " + reply.getMessage());
                return null;
            }
        } catch (HermesException e) {
            e.printStackTrace();
            return null;
        }
        object.setType(ObjectWrapper.TYPE_OBJECT);
        return getProxy(service, object);
    }

    public static <T> T getInstanceInService(Class<? extends HermesService> service, Class<T> clazz, Object... parameters) {
        return getInstanceWithMethodNameInService(service, clazz, "", parameters);
    }

    public static <T> T getInstance(Class<T> clazz, Object... parameters) {
        return getInstanceInService(HermesService.HermesService0.class, clazz, parameters);
    }

    public static <T> T getInstanceWithMethodName(Class<T> clazz, String methodName, Object... parameters) {
        return getInstanceWithMethodNameInService(HermesService.HermesService0.class, clazz, methodName, parameters);
    }

    public static <T> T getInstanceWithMethodNameInService(Class<? extends HermesService> service, Class<T> clazz, String methodName, Object... parameters) {
        TypeUtils.validateServiceInterface(clazz);
        checkBound(service);
        ObjectWrapper object = new ObjectWrapper(clazz, ObjectWrapper.TYPE_OBJECT_TO_GET);
        Sender sender = SenderDesignator.getPostOffice(service, SenderDesignator.TYPE_GET_INSTANCE, object);
        if (parameters == null) {
            parameters = new Object[0];
        }
        int length = parameters.length;
        Object[] tmp = new Object[length + 1];
        tmp[0] = methodName;
        for (int i = 0; i < length; ++i) {
            tmp[i + 1] = parameters[i];
        }
        try {
            Reply reply = sender.send(null, tmp);
            if (reply != null && !reply.success()) {
                Log.e(TAG, "Error occurs during getting instance. Error code: " + reply.getErrorCode());
                Log.e(TAG, "Error message: " + reply.getMessage());
                return null;
            }
        } catch (HermesException e) {
            e.printStackTrace();
            return null;
        }
        object.setType(ObjectWrapper.TYPE_OBJECT);
        return getProxy(service, object);
    }

    public static <T> T getUtilityClass(Class<T> clazz) {
        return getUtilityClassInService(HermesService.HermesService0.class, clazz);
    }

    public static <T> T getUtilityClassInService(Class<? extends HermesService> service, Class<T> clazz) {
        TypeUtils.validateServiceInterface(clazz);
        checkBound(service);
        ObjectWrapper object = new ObjectWrapper(clazz, ObjectWrapper.TYPE_CLASS_TO_GET);
        Sender sender = SenderDesignator.getPostOffice(service, SenderDesignator.TYPE_GET_UTILITY_CLASS, object);
        try {
            Reply reply = sender.send(null, null);
            if (reply != null && !reply.success()) {
                Log.e(TAG, "Error occurs during getting utility class. Error code: " + reply.getErrorCode());
                Log.e(TAG, "Error message: " + reply.getMessage());
                return null;
            }
        } catch (HermesException e) {
            e.printStackTrace();
            return null;
        }
        object.setType(ObjectWrapper.TYPE_CLASS);
        return getProxy(service, object);
    }

    public static void connect(Context context) {
        connectApp(context, null, HermesService.HermesService0.class);
    }

    public static void connect(Context context, Class<? extends HermesService> service) {
        // TODO callbacks should be handled as an exception.
        // It seems that callbacks may not be registered.
        connectApp(context, null, service);
    }

    public static void connectApp(Context context, String packageName) {
        connectApp(context, packageName, HermesService.HermesService0.class);
    }


    /**
     * 其他进程需要调用该方法进行连接操作
     *
     * @param context     当前进程的context
     * @param packageName 主进程的包名
     * @param service     主进程中注册的service，这个service继承HermesService，需要在清单文件中注册
     */
    public static void connectApp(Context context, String packageName, Class<? extends HermesService> service) {
        //如果两个进程在同一个app中看，则这里不会再初始化；
        //如果两个进程再不同的app中，则这里的connectApp（）方法依然会去调用init(context)方法
        init(context);
        //进行绑定操作，进入绑定操作
        CHANNEL.bind(context.getApplicationContext(), packageName, service);
    }

    public static void disconnect(Context context) {
        disconnect(context, HermesService.HermesService0.class);
    }

    public static void disconnect(Context context, Class<? extends HermesService> service) {
        CHANNEL.unbind(context.getApplicationContext(), service);
    }

    public static boolean isConnected() {
        return isConnected(HermesService.HermesService0.class);
    }

    public static boolean isConnected(Class<? extends HermesService> service) {
        return CHANNEL.isConnected(service);
    }

    public static void setHermesListener(HermesListener listener) {
        CHANNEL.setHermesListener(listener);
    }

}
