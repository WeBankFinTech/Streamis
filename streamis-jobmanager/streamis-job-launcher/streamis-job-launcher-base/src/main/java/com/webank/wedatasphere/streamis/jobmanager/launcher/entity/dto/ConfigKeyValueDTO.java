package com.webank.wedatasphere.streamis.jobmanager.launcher.entity.dto;

/**
 * @author limeng
 */
public class ConfigKeyValueDTO {
    private Long configkeyId;
    private String name;
    private String key;
    private String defaultValue;
    private String value;
    private Integer type;
    private Integer status;
    private Integer sort;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Long getConfigkeyId() {
        return configkeyId;
    }

    public void setConfigkeyId(Long configkeyId) {
        this.configkeyId = configkeyId;
    }
}
