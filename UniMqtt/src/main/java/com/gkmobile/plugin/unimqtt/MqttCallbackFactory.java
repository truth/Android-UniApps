package com.gkmobile.plugin.unimqtt;

import io.dcloud.feature.uniapp.bridge.UniJSCallback;

public class MqttCallbackFactory {
    private UniJSCallback messageArrived;
    private UniJSCallback deliveryComplete;
    private UniJSCallback connectionLost;
    private UniJSCallback onSuccess;
    private UniJSCallback onFailure;

    public UniJSCallback getMessageArrived() {
        return messageArrived;
    }

    public void setMessageArrived(UniJSCallback messageArrived) {
        this.messageArrived = messageArrived;
    }

    public UniJSCallback getDeliveryComplete() {
        return deliveryComplete;
    }

    public void setDeliveryComplete(UniJSCallback deliveryComplete) {
        this.deliveryComplete = deliveryComplete;
    }

    public UniJSCallback getConnectionLost() {
        return connectionLost;
    }

    public void setConnectionLost(UniJSCallback connectionLost) {
        this.connectionLost = connectionLost;
    }

    public UniJSCallback getOnSuccess() {
        return onSuccess;
    }

    public void setOnSuccess(UniJSCallback onSuccess) {
        this.onSuccess = onSuccess;
    }

    public UniJSCallback getOnFailure() {
        return onFailure;
    }

    public void setOnFailure(UniJSCallback onFailure) {
        this.onFailure = onFailure;
    }
}
