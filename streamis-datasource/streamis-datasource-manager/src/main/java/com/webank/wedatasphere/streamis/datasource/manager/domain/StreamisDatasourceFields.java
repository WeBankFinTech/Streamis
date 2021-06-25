package com.webank.wedatasphere.streamis.datasource.manager.domain;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;
import java.util.Objects;

/**
 * <p>
 * 数据源字段表
 * </p>
 *
 * @author c01013
 * @since 2021-04-02
 */
@TableName("streamis_datasource_fields")
public class StreamisDatasourceFields extends Model<StreamisDatasourceFields> {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * streamis_table_meta 的id，外键
     */
    @TableField("streamis_table_meta_id")
    private Long streamisTableMetaId;
    /**
     * 数据源字段名字
     */
    @TableField("field_name")
    private String fieldName;
    /**
     * 字段类型，string、int等
     */
    @TableField("field_type")
    private String fieldType;
    /**
     * 是否主键
     */
    @TableField("field_is_primary")
    private Integer fieldIsPrimary;
    /**
     * 是否分区
     */
    @TableField("field_is_partition")
    private Integer fieldIsPartition;
    /**
     * 校验规则
     */
    @TableField("verify_rule")
    private String verifyRule;
    /**
     * 字段别名
     */
    @TableField("field_alias")
    private String fieldAlias;
    /**
     * 描述
     */
    @TableField("field_description")
    private String fieldDescription;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStreamisTableMetaId() {
        return streamisTableMetaId;
    }

    public void setStreamisTableMetaId(Long streamisTableMetaId) {
        this.streamisTableMetaId = streamisTableMetaId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public Integer getFieldIsPrimary() {
        return fieldIsPrimary;
    }

    public void setFieldIsPrimary(Integer fieldIsPrimary) {
        this.fieldIsPrimary = fieldIsPrimary;
    }

    public Integer getFieldIsPartition() {
        return fieldIsPartition;
    }

    public void setFieldIsPartition(Integer fieldIsPartition) {
        this.fieldIsPartition = fieldIsPartition;
    }

    public String getVerifyRule() {
        return verifyRule;
    }

    public void setVerifyRule(String verifyRule) {
        this.verifyRule = verifyRule;
    }

    public String getFieldAlias() {
        return fieldAlias;
    }

    public void setFieldAlias(String fieldAlias) {
        this.fieldAlias = fieldAlias;
    }

    public String getFieldDescription() {
        return fieldDescription;
    }

    public void setFieldDescription(String fieldDescription) {
        this.fieldDescription = fieldDescription;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "StreamisDatasourceFields{" +
        ", id=" + id +
        ", streamisTableMetaId=" + streamisTableMetaId +
        ", fieldName=" + fieldName +
        ", fieldType=" + fieldType +
        ", fieldIsPrimary=" + fieldIsPrimary +
        ", fieldIsPartition=" + fieldIsPartition +
        ", verifyRule=" + verifyRule +
        ", fieldAlias=" + fieldAlias +
        ", fieldDescription=" + fieldDescription +
        "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StreamisDatasourceFields that = (StreamisDatasourceFields) o;
        return id.equals(that.id) &&
                streamisTableMetaId.equals(that.streamisTableMetaId) &&
                fieldName.equals(that.fieldName) &&
                fieldType.equals(that.fieldType) &&
                fieldIsPrimary.equals(that.fieldIsPrimary) &&
                fieldIsPartition.equals(that.fieldIsPartition) &&
                verifyRule.equals(that.verifyRule) &&
                fieldAlias.equals(that.fieldAlias) &&
                fieldDescription.equals(that.fieldDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, streamisTableMetaId, fieldName, fieldType, fieldIsPrimary, fieldIsPartition, verifyRule, fieldAlias, fieldDescription);
    }
}
