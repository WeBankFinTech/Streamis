package com.webank.wedatasphere.streamis.jobmanager.launcher.enums;

public enum JobClientType {

    ATTACH("attach"),
    DETACH("detach"),
    DETACH_STANDALONE("detachStandalone"),

    OTHER("other");

    private String name;

    JobClientType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public JobClientType toJobClientType(String s) {
        if ("attach".equalsIgnoreCase(s)) {
            return ATTACH;
        } else if ("detach".equalsIgnoreCase(s)) {
            return DETACH;
        } else if ("detachStandalone".equalsIgnoreCase(s)) {
            return DETACH_STANDALONE;
        } else {
            // default
            return ATTACH;
        }
    }
}
