package com.webank.wedatasphere.streamis.jobmanager.model;

import org.apache.http.Header;

public class ResponseWithHeaders {

    private String responseStr;

    private Header[] headers;

    public ResponseWithHeaders(String responseStr, Header[] headers) {
        this.responseStr = responseStr;
        this.headers = headers;
    }

    public String getResponseStr() {
        return responseStr;
    }

    public void setResponseStr(String responseStr) {
        this.responseStr = responseStr;
    }

    public Header[] getHeaders() {
        return headers;
    }

    public void setHeaders(Header[] headers) {
        this.headers = headers;
    }
}
