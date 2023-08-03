package com.webank.wedatasphere.streamis.jobmanager.launcher.entity;


import java.util.regex.Pattern;

public class StreamErrorCode {
    private Integer id;
    private String errorCode;
    private String errorDesc;
    private Pattern errorRegex;
    private String errorRegexStr;
    private Integer errorType;

    public StreamErrorCode() {
    }

    public StreamErrorCode(String errorCode, String errorDesc) {
        this.errorCode = errorCode;
        this.errorDesc = errorDesc;
    }

    public StreamErrorCode(Integer id, String errorCode, String errorDesc, String errorRegexStr, Integer errorType) {
        this.id = id;
        this.errorCode = errorCode;
        this.errorDesc = errorDesc;
        this.errorRegexStr = errorRegexStr;
        this.errorType = errorType;
        this.errorRegex = Pattern.compile(errorRegexStr);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDesc() {
        return errorDesc;
    }

    public void setErrorDesc(String errorDesc) {
        this.errorDesc = errorDesc;
    }

    public Pattern getErrorRegex() {
        return errorRegex;
    }

    public void setErrorRegex(Pattern errorRegex) {
        this.errorRegex = errorRegex;
    }

    public String getErrorRegexStr() {
        return errorRegexStr;
    }

    public void setErrorRegexStr(String errorRegexStr) {
        this.errorRegexStr = errorRegexStr;
        this.errorRegex = Pattern.compile(errorRegexStr);
    }

    public Integer getErrorType() {
        return errorType;
    }

    public void setErrorType(Integer errorType) {
        this.errorType = errorType;
    }
}
