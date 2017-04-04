package com.iemqra.bme.lostnfound.event;

/**
 * Created by Balazs on 2017. 03. 24..
 */

public class BaseEvent {
    private int code;
    private String message;
    private String data;
    private Throwable throwable;

    public BaseEvent() {
    }

    public BaseEvent(int code, String message, String data, Throwable throwable) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.throwable = throwable;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
}
