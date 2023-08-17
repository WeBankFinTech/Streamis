package com.webank.wedatasphere.streamis.jobmanager.vo;

public class LinkisResponse {

    private String method;
    private int status;
    private String message;
    private LinkisResponseData data;
    public void setMethod(String method) {
        this.method = method;
    }
    public String getMethod() {
        return method;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    public int getStatus() {
        return status;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }

    public void setData(LinkisResponseData data) {
        this.data = data;
    }
    public LinkisResponseData getData() {
        return data;
    }

}


