package com.lh.hermes.view;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lh.hermes.R;
import com.lh.hermes.bean.IHermesService;
import com.lh.hermes.bean.UserBean;
import com.lh.hermes.bean.UserInstanceBean;
import com.lh.hermes.service.HermesService;
import com.lh.hermes.service.SupperService;
import com.lh.hermes.service.ThreadService;
import com.library.hermes.Hermes;

public class MainActivity extends AppCompatActivity {
    private IHermesService iHermesService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, HermesService.class);
        startService(intent);
        Hermes.register(UserBean.class);
        Hermes.register(UserInstanceBean.class);
        Intent intent1 = new Intent(this, ThreadService.class);
        startService(intent1);
        Intent intent2 = new Intent(this, SupperService.class);
        startService(intent2);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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


    public void onHermes(View view) throws RemoteException {
        if (iHermesService != null) {
            Toast.makeText(this, "服务已连接，获得HermesBean实例：" + iHermesService.getHermesBean().toString(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "服务断开连接", Toast.LENGTH_SHORT).show();
        }
    }

    public void onBind(View view) {
        Intent intent = new Intent(this, HermesService.class);
        //绑定这个客户端
        bindService(intent, hermesConnection, BIND_ABOVE_CLIENT);
    }
}
