package com.example.administrator.haweather.util;

/**
 * Created by ${zsf} on 2016/10/30.
 */
public interface HttpCallbackListener {
    void onFinish(String response);

    void onError(Exception e);
}
