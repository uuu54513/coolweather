package com.coolweather.app.util;

public interface HttpCallbackListener{

    void onFinsh(String response);

    void onError(Exception e);
}
