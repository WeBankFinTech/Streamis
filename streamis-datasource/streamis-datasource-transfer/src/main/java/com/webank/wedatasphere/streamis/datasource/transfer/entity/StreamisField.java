package com.webank.wedatasphere.streamis.datasource.transfer.entity;

public class StreamisField {
    private Long id;
    private String fieldName;
    private Long streamisTableMetaId;
    private String fieldType;
    private Boolean fieldIsPrimary;
    private Boolean fieldIsPartition;
    private String description;

    public StreamisField(Long id, String fieldName, Long streamisTableMetaId, String fieldType, Boolean fieldIsPrimary, Boolean fieldIsPartition, String description) {
        this.id = id;
        this.fieldName = fieldName;
        this.streamisTableMetaId = streamisTableMetaId;
        this.fieldType = fieldType;
        this.fieldIsPrimary = fieldIsPrimary;
        this.fieldIsPartition = fieldIsPartition;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Long getStreamisTableMetaId() {
        return streamisTableMetaId;
    }

    public void setStreamisTableMetaId(Long streamisTableMetaId) {
        this.streamisTableMetaId = streamisTableMetaId;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public Boolean getFieldIsPrimary() {
        return fieldIsPrimary;
    }

    public void setFieldIsPrimary(Boolean fieldIsPrimary) {
        this.fieldIsPrimary = fieldIsPrimary;
    }

    public Boolean getFieldIsPartition() {
        return fieldIsPartition;
    }

    public void setFieldIsPartition(Boolean fieldIsPartition) {
        this.fieldIsPartition = fieldIsPartition;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
