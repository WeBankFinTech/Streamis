package com.webank.wedatasphere.streamis.jobmanager.vo;

public class LinkisResponseData {

    private String solution;
    private boolean historyAdmin;
    private String errorMsgTip;
    private boolean admin;
    public void setSolution(String solution) {
        this.solution = solution;
    }
    public String getSolution() {
        return solution;
    }

    public void setHistoryAdmin(boolean historyAdmin) {
        this.historyAdmin = historyAdmin;
    }
    public boolean getHistoryAdmin() {
        return historyAdmin;
    }

    public void setErrorMsgTip(String errorMsgTip) {
        this.errorMsgTip = errorMsgTip;
    }
    public String getErrorMsgTip() {
        return errorMsgTip;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
    public boolean getAdmin() {
        return admin;
    }
}
