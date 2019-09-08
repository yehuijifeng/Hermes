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

package com.library.hermes.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.library.hermes.HermesListener;
import com.library.hermes.HermesService;
import com.library.hermes.util.CallbackManager;
import com.library.hermes.util.CodeUtils;
import com.library.hermes.util.ErrorCodes;
import com.library.hermes.util.HermesException;
import com.library.hermes.util.TypeCenter;
import com.library.hermes.wrapper.ParameterWrapper;

/**
 * Created by Xiaofei on 16/4/11.
 */
public class Channel {

    private static final String TAG = "CHANNEL";

    private static volatile Channel sInstance = null;

    private final ConcurrentHashMap<Class<? extends HermesService>, IHermesService> mHermesServices = new ConcurrentHashMap<Class<? extends HermesService>, IHermesService>();

    private final ConcurrentHashMap<Class<? extends HermesService>, HermesServiceConnection> mHermesServiceConnections = new ConcurrentHashMap<Class<? extends HermesService>, HermesServiceConnection>();
    //绑定中状态缓存map
    private final ConcurrentHashMap<Class<? extends HermesService>, Boolean> mBindings = new ConcurrentHashMap<Class<? extends HermesService>, Boolean>();
    //绑定状态缓存map
    private final ConcurrentHashMap<Class<? extends HermesService>, Boolean> mBounds = new ConcurrentHashMap<Class<? extends HermesService>, Boolean>();

    private HermesListener mListener = null;

    private Handler mUiHandler = new Handler(Looper.getMainLooper());

    private static final CallbackManager CALLBACK_MANAGER = CallbackManager.getInstance();

    private static final TypeCenter TYPE_CENTER = TypeCenter.getInstance();

    private IHermesServiceCallback mHermesServiceCallback = new IHermesServiceCallback.Stub() {

        private Object[] getParameters(ParameterWrapper[] parameterWrappers) throws HermesException {
            if (parameterWrappers == null) {
                parameterWrappers = new ParameterWrapper[0];
            }
            int length = parameterWrappers.length;
            Object[] result = new Object[length];
            for (int i = 0; i < length; ++i) {
                ParameterWrapper parameterWrapper = parameterWrappers[i];
                if (parameterWrapper == null) {
                    result[i] = null;
                } else {
                    Class<?> clazz = TYPE_CENTER.getClassType(parameterWrapper);

                    String data = parameterWrapper.getData();
                    if (data == null) {
                        result[i] = null;
                    } else {
                        result[i] = CodeUtils.decode(data, clazz);
                    }
                }
            }
            return result;
        }

        public Reply callback(CallbackMail mail) {
            final Pair<Boolean, Object> pair = CALLBACK_MANAGER.getCallback(mail.getTimeStamp(), mail.getIndex());
            if (pair == null) {
                return null;
            }
            final Object callback = pair.second;
            if (callback == null) {
                return new Reply(ErrorCodes.CALLBACK_NOT_ALIVE, "");
            }
            boolean uiThread = pair.first;
            try {
                // TODO Currently, the callback should not be annotated!
                final Method method = TYPE_CENTER.getMethod(callback.getClass(), mail.getMethod());
                final Object[] parameters = getParameters(mail.getParameters());
                Object result = null;
                Exception exception = null;
                if (uiThread) {
                    boolean isMainThread = Looper.getMainLooper() == Looper.myLooper();
                    if (isMainThread) {
                        try {
                            result = method.invoke(callback, parameters);
                        } catch (IllegalAccessException e) {
                            exception = e;
                        } catch (InvocationTargetException e) {
                            exception = e;
                        }
                    } else {
                        mUiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    method.invoke(callback, parameters);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        return null;
                    }
                } else {
                    try {
                        result = method.invoke(callback, parameters);
                    } catch (IllegalAccessException e) {
                        exception = e;
                    } catch (InvocationTargetException e) {
                        exception = e;
                    }
                }
                if (exception != null) {
                    exception.printStackTrace();
                    throw new HermesException(ErrorCodes.METHOD_INVOCATION_EXCEPTION,
                            "Error occurs when invoking method " + method + " on " + callback, exception);
                }
                if (result == null) {
                    return null;
                }
                return new Reply(new ParameterWrapper(result));
            } catch (HermesException e) {
                e.printStackTrace();
                return new Reply(e.getErrorCode(), e.getErrorMessage());
            }
        }

        @Override
        public void gc(List<Long> timeStamps, List<Integer> indexes) throws RemoteException {
            int size = timeStamps.size();
            for (int i = 0; i < size; ++i) {
                CALLBACK_MANAGER.removeCallback(timeStamps.get(i), indexes.get(i));
            }
        }
    };

    private Channel() {

    }

    public static Channel getInstance() {
        if (sInstance == null) {
            synchronized (Channel.class) {
                if (sInstance == null) {
                    sInstance = new Channel();
                }
            }
        }
        return sInstance;
    }

    /**
     * 子进程中去绑定
     *
     * @param context     当前进程的context对象
     * @param packageName 主进程的包名，如果是同一个进程，则不需要传递。如果是不同的app则需要传递
     * @param service     注册的service
     */
    public void bind(Context context, String packageName, Class<? extends HermesService> service) {
        //hermesservice连接对象
        HermesServiceConnection connection;
        //同步锁
        synchronized (this) {
            //获得绑定状态，判断当前service是否已经绑定
            if (getBound(service)) {
                //如果已经绑定了则不需要重复操作
                return;
            }
            //从缓存中拿到绑定状态
            Boolean binding = mBindings.get(service);
            if (binding != null && binding) {
                //如果已经在绑定中了，则也不需要重复操作
                return;
            }
            //没有缓存到绑定中，则需要进行缓存，这里表示该service正在进行绑定
            mBindings.put(service, true);
            //new一个hermesservice连接对象，继续走
            connection = new HermesServiceConnection(service);
            //添加缓存
            mHermesServiceConnections.put(service, connection);
        }
        //绑定服务，不同app，需要指定包名
        Intent intent;
        if (TextUtils.isEmpty(packageName)) {
            intent = new Intent(context, service);
        } else {
            intent = new Intent();
            intent.setClassName(packageName, service.getName());
        }
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    //取消绑定
    public void unbind(Context context, Class<? extends HermesService> service) {
        synchronized (this) {
            Boolean bound = mBounds.get(service);
            if (bound != null && bound) {
                HermesServiceConnection connection = mHermesServiceConnections.get(service);
                if (connection != null) {
                    //取消绑定
                    context.unbindService(connection);
                }
                mBounds.put(service, false);
            }
        }
    }

    public Reply send(Class<? extends HermesService> service, Mail mail) {
        IHermesService hermesService = mHermesServices.get(service);
        try {
            if (hermesService == null) {
                return new Reply(ErrorCodes.SERVICE_UNAVAILABLE,
                        "Service Unavailable: Check whether you have connected Hermes.");
            }
            return hermesService.send(mail);
        } catch (RemoteException e) {
            return new Reply(ErrorCodes.REMOTE_EXCEPTION, "Remote Exception: Check whether "
                    + "the process you are communicating with is still alive.");
        }
    }

    public void gc(Class<? extends HermesService> service, List<Long> timeStamps) {
        IHermesService hermesService = mHermesServices.get(service);
        if (hermesService == null) {
            Log.e(TAG, "Service Unavailable: Check whether you have disconnected the service before a process dies.");
        } else {
            try {
                hermesService.gc(timeStamps);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断当前service是否已经绑定了
     *
     * @param service
     * @return
     */
    public boolean getBound(Class<? extends HermesService> service) {
        Boolean bound = mBounds.get(service);
        return bound != null && bound;
    }

    public void setHermesListener(HermesListener listener) {
        mListener = listener;
    }

    public boolean isConnected(Class<? extends HermesService> service) {
        IHermesService hermesService = mHermesServices.get(service);
        return hermesService != null && hermesService.asBinder().pingBinder();
    }

    /**
     * hermesservice的连接服务，跨进程的连接和断开状态
     */
    private class HermesServiceConnection implements ServiceConnection {

        private Class<? extends HermesService> mClass;

        //传递进来继承hermerservice的class
        HermesServiceConnection(Class<? extends HermesService> service) {
            mClass = service;
        }

        /**
         * 已经连接
         *
         * @param className 组件名称
         * @param service   服务Binder
         */
        public void onServiceConnected(ComponentName className, IBinder service) {
            //同步锁
            synchronized (Channel.this) {
                //连接成功，则绑定状态缓存为true，表示已经绑定上了
                mBounds.put(mClass, true);
                //绑定中状态改成false，表示结束了绑定中状态
                mBindings.put(mClass, false);
                //这里得到IHermesService对象
                IHermesService hermesService = IHermesService.Stub.asInterface(service);
                //缓存起来
                mHermesServices.put(mClass, hermesService);
                try {
                    //注册当前的回调监听,在当前线程中注册
                    hermesService.register(mHermesServiceCallback, Process.myPid());
                } catch (RemoteException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Remote Exception: Check whether "
                            + "the process you are communicating with is still alive.");
                    return;
                }
            }
            //已经连接给予回调
            if (mListener != null) {
                mListener.onHermesConnected(mClass);
            }
        }

        /**
         * 已经断开
         *
         * @param className
         */
        public void onServiceDisconnected(ComponentName className) {
            synchronized (Channel.this) {
                //清除缓存
                mHermesServices.remove(mClass);
                //绑定状态biancheng false
                mBounds.put(mClass, false);
                //绑定中状态变成false
                mBindings.put(mClass, false);
            }
            if (mListener != null) {
                mListener.onHermesDisconnected(mClass);
            }
        }
    }
}
