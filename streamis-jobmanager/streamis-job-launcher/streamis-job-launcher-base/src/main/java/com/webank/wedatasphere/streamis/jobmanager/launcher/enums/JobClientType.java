package com.webank.wedatasphere.streamis.jobmanager.launcher.enums;

public enum JobClientType {

    ATTACH("attach"),
    DETACH("detach"),
    DETACH_STANDALONE("detachStandalone");

    private String name;

    JobClientType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
