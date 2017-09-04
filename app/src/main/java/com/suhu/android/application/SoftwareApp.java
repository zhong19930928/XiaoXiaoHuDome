package com.suhu.android.application;

import android.app.Application;

import com.mob.MobSDK;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;

/**
 * Created by Administrator on 2017/9/2 0002.
 */

public class SoftwareApp extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        MobSDK.init(this, "20a8ee4f731de", "ab236fd23e610fe078dcc1fb136c0adf");

        PushAgent mPushAgent = PushAgent.getInstance(this);//注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token
            }

            @Override
            public void onFailure(String s, String s1) {

            }
        });

    }
}