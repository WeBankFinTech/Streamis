package com.webank.wedatasphere.streamis.jobmanager.launcher.entity.vo;

import java.util.List;

/**
 * @author limeng
 */
public class ConfigRelationVO {
    private Long configkeyId;
    private String key;
    private String name;
    private String value;
    private List<ValueList> valueLists;

    public static class ValueList{
        private String value;
        private Boolean selected;

        public ValueList() {
        }

        public ValueList(String value, Boolean selected) {
            this.value = value;
            this.selected = selected;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Boolean getSelected() {
            return selected;
        }

        public void setSelected(Boolean selected) {
            this.selected = selected;
        }
    }

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

    public List<ValueList> getValueLists() {
        return valueLists;
    }

    public void setValueLists(List<ValueList> valueLists) {
        this.valueLists = valueLists;
    }

    public Long getConfigkeyId() {
        return configkeyId;
    }

    public void setConfigkeyId(Long configkeyId) {
        this.configkeyId = configkeyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
