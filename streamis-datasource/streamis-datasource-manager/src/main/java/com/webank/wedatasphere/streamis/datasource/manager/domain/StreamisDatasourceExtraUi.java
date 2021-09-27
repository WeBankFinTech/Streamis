package com.webank.wedatasphere.streamis.datasource.manager.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author c01013
 * @since 2021-06-04
 */
@TableName("streamis_datasource_extra_ui")
public class StreamisDatasourceExtraUi extends Model<StreamisDatasourceExtraUi> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("datasource_type")
    private String datasourceType;

    @TableField(value="`key`")
    private String key;
    private String description;
    @TableField("description_en")
    private String descriptionEn;
    @TableField("lable_name")
    private String lableName;
    @TableField("lable_name_en")
    private String lableNameEn;
    @TableField("ui_type")
    private String uiType;
    private Integer required;
    private String value;
    @TableField("default_value")
    private String defaultValue;
    @TableField("is_hidden")
    private Integer isHidden;
    private String condition;
    @TableField("is_advanced")
    private Integer isAdvanced;
    private Integer order;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDatasourceType() {
        return datasourceType;
    }

    public void setDatasourceType(String datasourceType) {
        this.datasourceType = datasourceType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public String getLableName() {
        return lableName;
    }

    public void setLableName(String lableName) {
        this.lableName = lableName;
    }

    public String getLableNameEn() {
        return lableNameEn;
    }

    public void setLableNameEn(String lableNameEn) {
        this.lableNameEn = lableNameEn;
    }

    public String getUiType() {
        return uiType;
    }

    public void setUiType(String uiType) {
        this.uiType = uiType;
    }

    public Integer getRequired() {
        return required;
    }

    public void setRequired(Integer required) {
        this.required = required;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Integer getIsHidden() {
        return isHidden;
    }

    public void setIsHidden(Integer isHidden) {
        this.isHidden = isHidden;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Integer getIsAdvanced() {
        return isAdvanced;
    }

    public void setIsAdvanced(Integer isAdvanced) {
        this.isAdvanced = isAdvanced;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "StreamisDatasourceExtraUi{" +
        ", id=" + id +
        ", datasourceType=" + datasourceType +
        ", key=" + key +
        ", description=" + description +
        ", descriptionEn=" + descriptionEn +
        ", lableName=" + lableName +
        ", lableNameEn=" + lableNameEn +
        ", uiType=" + uiType +
        ", required=" + required +
        ", value=" + value +
        ", defaultValue=" + defaultValue +
        ", isHidden=" + isHidden +
        ", condition=" + condition +
        ", isAdvanced=" + isAdvanced +
        ", order=" + order +
        "}";
    }
}
