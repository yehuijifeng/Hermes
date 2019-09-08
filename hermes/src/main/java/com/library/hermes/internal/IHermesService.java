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

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

import java.util.List;

/**
 * 这里将aidl自动生成的代码实现，为了快进程使用
 */
public interface IHermesService extends IInterface {

    abstract class Stub extends Binder implements IHermesService {
        //这里指定包名绝对路径到iHermesservice
        private static final String DESCRIPTOR = "com.library.hermes.internal.IHermesService";

        //保存当前service
        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        //获得当前service
        public static IHermesService asInterface(IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            //查询本地接口
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            //查询到的IInterface不为null且属于IHermesService
            if (iin != null)
                if (iin instanceof IHermesService)
                    return ((IHermesService) iin);
            //如果本地接口没有查询到则新建一个， 继续走
            return new Proxy(obj);
        }

        @Override
        public IBinder asBinder() {
            return this;
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case INTERFACE_TRANSACTION:
                    reply.writeString(DESCRIPTOR);
                    return true;
                case TRANSACTION_send:
                    data.enforceInterface(DESCRIPTOR);
                    Mail _arg0;
                    if ((0 != data.readInt())) {
                        _arg0 = Mail.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    Reply _result = this.send(_arg0);
                    reply.writeNoException();
                    if ((_result != null)) {
                        reply.writeInt(1);
                        _result.writeToParcel(reply, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                case TRANSACTION_register:
                    data.enforceInterface(DESCRIPTOR);
                    IHermesServiceCallback _arg1;
                    IBinder iBinder = data.readStrongBinder();
                    _arg1 = IHermesServiceCallback.Stub.asInterface(iBinder);
                    int pid = data.readInt();
                    this.register(_arg1, pid);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_gc:
                    data.enforceInterface(DESCRIPTOR);
                    List list;
                    ClassLoader cl = this.getClass().getClassLoader();
                    list = data.readArrayList(cl);
                    this.gc(list);
                    reply.writeNoException();
                    return true;
            }
            return super.onTransact(code, data, reply, flags);
        }

        //新建一个Ihermesservice
        private static class Proxy implements IHermesService {

            private IBinder mRemote;

            Proxy(IBinder remote) {
                mRemote = remote;
            }

            @Override
            public IBinder asBinder() {
                return mRemote;
            }

            public String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override
            public Reply send(Mail mail) throws RemoteException {
                Parcel _data = Parcel.obtain();//创建Parcel
                Parcel _reply = Parcel.obtain();
                Reply _result;
                try {
                    //写入当前IHermesService
                    _data.writeInterfaceToken(DESCRIPTOR);
                    if ((mail != null)) {
                        _data.writeInt(1);
                        mail.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    mRemote.transact(Stub.TRANSACTION_send, _data, _reply, 0);
                    _reply.readException();
                    if ((0 != _reply.readInt())) {
                        _result = Reply.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public void register(IHermesServiceCallback callback, int pid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStrongBinder((((callback != null)) ? (callback.asBinder()) : (null)));
                    _data.writeInt(pid);
                    mRemote.transact(Stub.TRANSACTION_register, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void gc(List<Long> timeStamps) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeList(timeStamps);
                    mRemote.transact(Stub.TRANSACTION_gc, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        static final int TRANSACTION_send = IBinder.FIRST_CALL_TRANSACTION;

        static final int TRANSACTION_register = IBinder.FIRST_CALL_TRANSACTION + 1;

        static final int TRANSACTION_gc = IBinder.FIRST_CALL_TRANSACTION + 2;
    }

    Reply send(Mail mail) throws RemoteException;

    void register(IHermesServiceCallback callback, int pid) throws RemoteException;

    void gc(List<Long> timeStamps) throws RemoteException;
}
