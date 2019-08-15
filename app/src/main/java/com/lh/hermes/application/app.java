package com.lh.hermes.application;

import android.app.Application;

import com.library.hermes.Hermes;

/**
 * user：LuHao
 * time：2019/8/15 17:06
 * describe：
 */
public class app extends Application {
    private Application instance;

    public Application getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Hermes.init(instance);
    }
}
