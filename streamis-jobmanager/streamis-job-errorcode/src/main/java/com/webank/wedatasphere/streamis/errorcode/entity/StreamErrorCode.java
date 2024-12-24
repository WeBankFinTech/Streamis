package com.webank.wedatasphere.streamis.errorcode.entity;



public class StreamErrorCode {
    private Integer id;
    private String errorCode;
    private String errorDesc;
    private String errorRegex;
    private Integer errorType;
    private String solution;

    public StreamErrorCode() {
    }

    public StreamErrorCode(String errorCode, String errorDesc,String solution) {
        this.errorCode = errorCode;
        this.errorDesc = errorDesc;
        this.solution = solution;
    }

    public StreamErrorCode(Integer id, String errorCode, String errorDesc, String errorRegex, Integer errorType,String solution) {
        this.id = id;
        this.errorCode = errorCode;
        this.errorDesc = errorDesc;
        this.errorRegex = errorRegex;
        this.errorType = errorType;
        this.solution = solution;
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

    public String getErrorRegex() {
        return errorRegex;
    }

    public void setErrorRegex(String errorRegex) {
        this.errorRegex = errorRegex;
    }

    public Integer getErrorType() {
        return errorType;
    }

    public void setErrorType(Integer errorType) {
        this.errorType = errorType;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }
}
