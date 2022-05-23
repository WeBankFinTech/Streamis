package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.state;

public class JobStateFileInfo {
    private String name;
    private String path;
    private String parentPath;
    private long size;
    private long modifytime;

    public JobStateFileInfo(String name, String path, String parentPath, long size, long modifytime) {
        this.name = name;
        this.path = path;
        this.parentPath = parentPath;
        this.size = size;
        this.modifytime = modifytime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getModifytime() {
        return modifytime;
    }

    public void setModifytime(long modifytime) {
        this.modifytime = modifytime;
    }
}
