package com.gkmobile.plugin.unimqtt;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.HashMap;
import java.util.Map;

import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.common.UniModule;

public class UniMqtt extends UniModule {
    String TAG = "UniMqtt";
    private MqttCallbackFactory callbackFactory = new MqttCallbackFactory();
    private Map<String,IMqttToken> tokens = new HashMap<>();
    private MqttAndroidClient client;
    private MqttCallback mqttCallback = new MqttCallback() {

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            String msg = new String(message.getPayload());
            if(callbackFactory.getMessageArrived()==null) {
                return;
            }
            Log.d(TAG,"topic:" + topic);
            Log.d(TAG, "messageArrived1:" + msg);
            JSONObject data = new JSONObject();
            data.put("code", "success");
            data.put("topic", topic);
            data.put("message",msg);
            callbackFactory.getMessageArrived().invokeAndKeepAlive(data);
        }
        @Override
        public void deliveryComplete(IMqttDeliveryToken arg0) {
            int messageId = arg0.getMessageId();
            if(callbackFactory.getDeliveryComplete()==null) {
                return;
            }
            JSONObject data = new JSONObject();
            data.put("code", "success");
            data.put("messageId", messageId);
            callbackFactory.getDeliveryComplete().invokeAndKeepAlive(data);
        }

        @Override
        public void connectionLost(Throwable arg0) {
            if(callbackFactory.getConnectionLost()==null) {
                return;
            }
            JSONObject data = new JSONObject();
            data.put("code", "error");
            data.put("message", arg0.getMessage());
            callbackFactory.getConnectionLost().invokeAndKeepAlive(data);
        }
    };
    private IMqttActionListener iMqttActionListener =  new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
            if(callbackFactory.getOnSuccess()==null) {
                return;
            }
            int messageId = asyncActionToken.getMessageId();
            boolean isComplete= asyncActionToken.isComplete();
            JSONObject data = new JSONObject();
            data.put("code", "success");
            data.put("messageId", messageId);
            data.put("isComplete",isComplete);
            callbackFactory.getOnSuccess().invokeAndKeepAlive(data);
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            if(callbackFactory.getOnFailure()==null) {
                return;
            }
            int messageId = asyncActionToken.getMessageId();
            JSONObject data = new JSONObject();
            data.put("code", "error");
            data.put("message", exception.getMessage());
            data.put("messageId",messageId);
            callbackFactory.getOnFailure().invokeAndKeepAlive(data);
        }
    };
    @UniJSMethod(uiThread = true)
    public void create(JSONObject options, UniJSCallback messageArrived, UniJSCallback deliveryComplete,
                       UniJSCallback connectionLost,UniJSCallback onSuccess,UniJSCallback onFailure) {
        Context context = this.mWXSDKInstance.getContext();
        String host = options.getString("host");
        String clientId = options.getString("clientId");
        String userName = options.getString("userName");
        String password = options.getString("password");
        if(client==null) {
            client = new MqttAndroidClient(context, host, clientId);
        }
        // ??????MQTT????????????????????????
        client.setCallback(mqttCallback);
        callbackFactory.setMessageArrived(messageArrived);
        callbackFactory.setDeliveryComplete(deliveryComplete);
        callbackFactory.setConnectionLost(connectionLost);
        callbackFactory.setOnSuccess(onSuccess);
        callbackFactory.setOnFailure(onFailure);
        MqttConnectOptions conOpt = new MqttConnectOptions();
        // ????????????
        conOpt.setCleanSession(true);
        // ?????????????????????????????????
        conOpt.setConnectionTimeout(10);
        // ????????????????????????????????????
        conOpt.setKeepAliveInterval(30);
        // ?????????
        if(userName!=null) {
            conOpt.setUserName(userName);
        }
        // ??????
        if(password!=null) {
            conOpt.setPassword(password.toCharArray());
        }
        if (!client.isConnected()) {
            try {
                client.connect(conOpt, null, iMqttActionListener);
            } catch (MqttException e) {
                Log.e(TAG, "mqttConnect fail:" + e.getStackTrace());
            }
        }
    }
    @UniJSMethod(uiThread = true)
    public void mqttSubscribe(String topic,Integer qos) {
        try {
            if(qos==null) {
                qos =1;
            }
            // ??????myTopic??????
            IMqttToken mqttToken = tokens.get(topic);
            if(mqttToken==null){
                mqttToken = client.subscribe(topic, qos);
                tokens.put(topic,mqttToken);
            }
            Log.d(TAG, "MQTT????????????:" + topic);
        } catch (MqttException e) {
            Log.e(TAG, "MQTT????????????:" + e.getStackTrace());
        }
    }
    @UniJSMethod(uiThread = true)
    public void mqttUnSubscribe(String topic) {
        try {
            // ??????myTopic??????
            client.unsubscribe(topic);
            tokens.remove(topic);
            Log.d(TAG, "MQTT????????????:" + topic);
        } catch (MqttException e) {
            Log.e(TAG, "MQTT????????????:" + e.getStackTrace());
        }
    }
    @UniJSMethod(uiThread = false)
    public boolean isConnected() {
        if(client!=null && client.isConnected())  {
            return true;
        }
        return false;
    }
    @UniJSMethod(uiThread = false)
    public void mqttPublish(String topic,String msg) {
        Integer qos = 0;
        Boolean retained = false;
        try {
            if (client != null) {
                client.publish(topic,msg.getBytes(), qos.intValue(), retained.booleanValue());
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityDestroy() {
        super.onActivityDestroy();
        if(client!=null) {
            for(IMqttToken token:tokens.values()) {
                try {
                    client.unsubscribe(token.getTopics());
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
            tokens.clear();
            client.unregisterResources();
            client.close();
        }
    }
}
