# 概述

1. Android进程间通信IPC框架
2. 像调用本地函数一样调用其他进程的函数
3. 在本地进程创建其他进程类的对象
4. 在本进程获取其他进程的单例
5. 在本进程使用其他进程的工具类
6. 支持进程间函数回调，调用其他进程函数的时候可以传入回调函数，让其他进程回调本进程的方法
7. 自带内存优化，内置两个垃圾回收器，地进程在远端进程创建的实例和本地进程传给远端进程的回调接口会被自动回收。

# 基本使用

```java
compile 'xiaofei.library:hermes:0.7.0'
```

## 两个进程共享一个对象单例

```java
//在进程A中，类使用注解标记该类的id
@ClassId(“Singleton”)
public class Singleton {
    //属性
    private volatile String mData;
    
	//实现单例，代码省略
    public static Singleton getInstance();
 
	//方法使用注解标记该方法的id
    @MethodId(“setData”)
    public void setData(String data) {
        mData = data;
    }

    @MethodId(“getData”)
    public String getData() {
        return mData;
    }
}
```

**进程B要访问在进程A中的Singleton对象，如下：**

```java
//在进程B中，自定义接口
//该接口指定与Singleton的注解的classid一致
@ClassId(“Singleton”)
public interface ISingleton {
	//方法id与Singleton的注解的methodid一致
    @MethodId(“setData”)
    void setData(String data);

    @MethodId(“getData”)
    String getData();
}
```

```java
//使用如下：
//获得Singleton对象
ISingleton singleton = Hermes.getInstance(ISingleton.class);
//调用方法
singleton.setData(“Hello, Hermes!”);
//调用方法
Log.v(TAG, singleton.getData());
```

## 在其他进程调用主进程的函数

**Hermes支持任意进程之间的函数调用**

### AndroidManifest.xml

```xml
<service android:name="xiaofei.library.hermes.HermesService$HermesService0"/>
```

### 主进程初始化Hermes

```java
//在给其他进程提供函数的进程中，使用Hermes.init(Context)初始化
Hermes.init(Context);
```

### 子进程连接Hermes

```java
//子进程链接Hermes后才可以使用Hermes的服务
//在Application.OnCreate()或Activity.OnCreate()
Hermes.connect(Context)；
```

### 查看通信的进程是否还活着

```java
Hermes.isConnected()
```

### 事先注册被调用的类

```java
//进程A中，被进程B调用的类需要事先注册
//如果类上面没有加上注解，那么注册就不是必须的，Hermes会通过类名进行反射查找相应的类
//有两种注册类的API：
Hermes.register(Class<?>)
Hermes.register(Object)
Hermes.register(object)等价于Hermes.register(object.getClass())
```

## 设置连接监听

```java
//在连接之前给Hermes设置监听器
Hermes.setHermesListener(new HermesListener() {
    @Override
    public void onHermesConnected(Class<? extends HermesService> service) {
        //连接成功，首先获取单例
       IUserInfo iUserinfo = Hermes.getInstance(IUserInfo.class);
        //通过单例获取UserInfo
       String name = iUserinfo.getUserName();
    }
});
//连接Hermes服务
Hermes.connect(context);
```

## 连接Hermes服务

```java
Hermes.connect(context);//需要连接服务才能得到其他进程的实例
```

## 断开Hermes服务

```java
Hermes.disconnect(context);
```



### 创建实例

```java
//进程B中，创建进程A中的实例有三种方法：
Hermes.newInstance(Class,Object…)
Hermes.getInstance(Class,Object…)
Hermes.getUtilityClass(Class)
```

#### Hermes.newInstance(Class,Object…)

1. 在进程A中创建指定类的实例，并将引用返回给进程B。

2. 函数的第二个参数将传给指定类的对应的构造器。
3. 在进程B中，调用Hermes.newInstance(ILoadingTask.class, “xxx”, true)得到LoadingTask实例。
4. 注意：该实例不是单例，该实例为当前线程所拥有的实例。

```java
@ClassId(“LoadingTask”)
public class LoadingTask {

    public LoadingTask(String path, boolean showImmediately) {
        //...
    }

    @MethodId(“start”)
    public void start() {
        //...
    }
}

@ClassId(“LoadingTask”)
public interface ILoadingTask {
    @MethodId(“start”)
    void start();
}
```

#### Hermes.getInstance(Class , Object...)

1. 在进程A中通过指定类的getInstance()方法创建实例，并将引用返回给进程B。
2. 第二个参数将传给对应的getInstance()方法。
3. 该函数适合获取单例，这样进程A和进程B就使用同一个单例。
4. 进程B中，调用Hermes.getInstance(IBitmapWrapper.class, “XXX”)将得到BitmapWrapper的实例。
5. 进程B中，调用Hermes.getInstance(IBitmapWrapper.class, 100)将得到BitmapWrapper的实例。

```java
@ClassId(“BitmapWrapper”)
public class BitmapWrapper {

    @GetInstance
    public static BitmapWrapper getInstance(String path) {
        //这里可以写成单例，两个进程可以获得当前这个实例
    }

    @GetInstance
    public static BitmapWrapper getInstance(int label) {
        //...
    }

    @MethodId(“show”)
    public void show() {
        //...
    }

}

@ClassId(“BitmapWrapper”)
public interface IBitmapWrapper {

    @MethodId(“show”)
    void show();

}
```

#### Hermes.getUtilityClass(Class )

1. 获取进程A的工具类。
2. 这种做法在插件开发中很有用。
3. 主app和插件app存在不同的进程中，为了维护方便，应该使用统一的工具类。
4. 插件app可以通过这个方法获取主app的工具类。

```java
@ClassId(“Maths”)
public class Maths {

    @MethodId(“plus”)
    public static int plus(int a, int b) {
       //模拟加法
    }

    @MethodId(“minus”)
    public static int minus(int a, int b) {
        //模拟减法
    }

}


@ClassId(“Maths”)
public interface IMaths {

    @MethodId(“plus”)
    int plus(int a, int b);

    @MethodId(“minus”)
    int minus(int a, int b);
}
```

**进程B中，使用下面代码使用进程A的工具类**

```java
IMaths maths = Hermes.getUtilityClass(IMaths.class);
int sum = maths.plus(3, 5);
int diff = maths.minus(3, 5);
```

### 注意事项

1. 如果两个进程属于两个不同的app（分别叫App A和App B），App A想访问App B的一个类，并且App A的接口和App B的对应类有相同的包名和类名，那么就没有必要在类和接口上加@ClassId注解。

   1. 但是要注意使用ProGuard后类名和包名仍要保持一致。

2. 如果接口和类里面对应的方法的名字相同，那么也没有必要在方法上加上@MethodId注解。

   1. 但是要注意使用ProGuard后接口内的方法名字必须仍然和类内的对应方法名字相同。

3. 如果进程A的一个类上面有一个@ClassId注解，这个类在进程B中对应的接口上有一个相同的@ClassId注解，那么进程A在进程B访问这个类之前必须注册这个类。

   1. 否则进程B使用Hermes.newInstance()、Hermes.getInstance()或Hermes.getUtilityClass()时，Hermes在进程A中找不到匹配的类。
   2. 类可以在构造器或者Application.OnCreate()中注册。
   3. 但是如果类和对应的接口上面没有@ClassId注解，但有相同的包名和类名，那么就不需要注册类。
   4. Hermes通过包名和类名匹配类和接口。
   5. 对于接口和类里面的函数，上面的说法仍然适用。

4. 如果不想让一个类或者函数被其他进程访问，可以在上面加上@WithinProcess注解。

5. 使用Hermes跨进程调用函数的时候，传入参数的类型可以是原参数类型的子类，但不可以是匿名类和局部类。

   1. 传递的参数不能是匿名类：maths.puls(new A(){});
   2. 传递的参数也不能是private的：private A a=new A(){};
   3. 但是回调函数例外，关于回调函数详见第7点

6. 如果被调用的函数的参数类型和返回值类型是int、double等基本类型或者String这样的Java通用类型，上面的说法可以很好地解决问题。但如果类型是自定义的类，比如第5点中的自定义类A，并且两个进程分别属于两个不同app，那么你必须在两个app中都定义这个类，且必须保证代码混淆后，两个类仍然有相同的包名和类名。

   1. 不过你可以适用@ClassId和@MethodId注解，这样包名和类名在混淆后不同也不要紧了。

7. 如果被调用的函数有回调参数，那么函数定义中这个参数必须是一个接口，不能是抽象类。请特别注意回调函数运行的线程。

   1. 如果进程A调用进程B的函数，并且传入一个回调函数供进程B在进程A进行回调操作，那么默认这个回调函数将运行在进程A的主线程（UI线程）。如果你不想让回调函数运行在主线程，那么在接口声明的函数的对应的回调参数之前加上@Background注解。

   2. 如果回调函数有返回值，那么你应该让它运行在后台线程。如果运行在主线程，那么返回值始终为null。

   3. 默认情况下，Hermes持有回调函数的强引用，这个可能会导致内存泄漏。你可以在接口声明的对应回调参数前加上@WeakRef注解，这样Hermes持有的就是回调函数的弱引用。

   4. 如果进程的回调函数被回收了，而对方进程还在调用这个函数（对方进程并不会知道回调函数被回收），这个不会有任何影响，也不会造成崩溃。

   5. 如果回调函数有返回值，那么就返回null。

   6. 如果你使用了@Background和@WeakRef注解，你必须在接口中对应的函数参数前进行添加。

   7. 如果加在其他地方，并不会有任何作用。

   8. ```java
      @ClassId(“Foo”)
      public class Foo {
          public static void f(int i, Callback callback) {
          }
      }
      
      @ClassId(“callback”)
      public interface Callback {
          void callback();
      }
      
      @ClassId(“Foo”)
      public interface IFoo {
          void f(int i, @WeakRef @Background Callback callback);
      }
      ```

8. 调用函数的时候，任何Context在另一个进程中都会变成对方进程的applicationContext。

9. 数据传输是基于Json的。

10. 使用Hermes的时候，有任何的错误都会使用android.util.Log.e()打出错误日志。

## 调用非主进程的函数

# aidl跨进程通信

## 编写adil文件

1. 包名下右键
2. new
3. AIDL
4. AIDL File
5. 命名

```java
// IHermesService.aidl
package com.xxx.hermes.bean;

// Declare any non-default types here with import statements
//这里需要自己手动添加，并且有与之对应的java文件和aidl文件
import com.xxx.hermes.bean.HermesBean;

interface IHermesService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
     HermesBean getHermesBean();

     void setHermesBean(int id,String name,double money);
}
```

```java
//对应的HermesBean的aidl文件
// HermesBean.aidl
package com.xxx.hermes.bean;

// Declare any non-default types here with import statements

parcelable HermesBean;
```

```java
package com.xxx.hermes.bean;

import android.os.Parcel;
import android.os.Parcelable;
//HermesBean于之对应的HermesBean.aidl必须在同一个包名路径下
public class HermesBean implements Parcelable {
    private int id;
    private String name;
    private double money;
 //省略构造方法、序列化、反序列化、get/set方法、toString
}

```

## 实现adil的接口

```java
package com.xxx.hermes.aidl;

import android.util.Log;

import com.xxx.hermes.bean.HermesBean;
import com.xxx.hermes.bean.IHermesService;

//实现adil的接口，暴露给客户端使用
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
```

## aidl服务端

```java
package com.xxx.hermes.service;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.xxx.hermes.aidl.HermesAidl;

//跨进程通信的服务端
public class HermesService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return new HermesAidl();
    }
}
```

**AndroidManifest.xml**

```xml
<application>
	<service
		android:name="com.xxx.hermes.service.HermesService"
		android:enabled="true"
		android:process=":hermes" />
</application>
```



## aidl客户端

```java
public class MainActivity extends AppCompatActivity {
    private IHermesService iHermesService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //这里开启aidl的服务端
        Intent i = new Intent(this, HermesService.class);
        startService(i);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //绑定这个客户端
		Intent intent = new Intent(this, HermesService.class);
        bindService(intent, hermesConnection, BIND_ABOVE_CLIENT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解绑这个客户端
        if (hermesConnection != null)
            unbindService(hermesConnection);
    }

    //服务链接
    private ServiceConnection hermesConnection = new ServiceConnection() {
        //服务链接
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            iHermesService = IHermesService.Stub.asInterface(iBinder);
            Toast.makeText(MainActivity.this, "服务连接", Toast.LENGTH_SHORT).show();
        }

        //服务断开
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            iHermesService = null;
            Toast.makeText(MainActivity.this, "服务断开连接", Toast.LENGTH_SHORT).show();
        }
    };

	//测试通信
    public void onHermes(View view) throws RemoteException {
        if (iHermesService != null) {
            Toast.makeText(this, "服务已连接，获得HermesBean实例：" + iHermesService.getHermesBean().toString(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "服务断开连接", Toast.LENGTH_SHORT).show();
        }
    } 
}
```

