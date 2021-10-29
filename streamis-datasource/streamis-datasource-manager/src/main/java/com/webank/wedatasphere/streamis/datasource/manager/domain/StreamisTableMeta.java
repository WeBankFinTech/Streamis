package com.webank.wedatasphere.streamis.datasource.manager.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

/**
 * <p>
 * StreamTableMeta表
 * </p>
 *
 * @author c01013
 * @since 2021-04-03
 */
@TableName("streamis_table_meta")
public class StreamisTableMeta extends Model<StreamisTableMeta> {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 名字，Streamis节点的名字
     */
    private String name;
    /**
     * 标签，逗号分隔
     */
    private String tags;
    /**
     * 基本描述信息
     */
    private String description;
    /**
     * 表示是DWD,DWS或者维表等
     */
    private String layer;
    /**
     * 作用域，表示是工程级别还是任务级别等
     */
    private String scope;
    /**
     * json信息保存额外信息
     */
    private String params;
    /**
     * 工作空间名
     */
    @TableField("workspace_name")
    private String workspaceName;
    /**
     * 绑定的linkis数据源的名字
     */
    @TableField("linkis_datasource_name")
    private String linkisDatasourceName;

    @TableField("table_name")
    private String tableName;

    @TableField("node_name")
    private String nodeName;

    private String alias;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @TableField("create_by")
    private String createBy;
    @TableField("create_time")
    private Date createTime;
    @TableField("update_by")
    private String updateBy;
    @TableField("update_time")
    private Date updateTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLayer() {
        return layer;
    }

    public void setLayer(String layer) {
        this.layer = layer;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    public String getLinkisDatasourceName() {
        return linkisDatasourceName;
    }

    public void setLinkisDatasourceName(String linkisDatasourceName) {
        this.linkisDatasourceName = linkisDatasourceName;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    @Override
    public String toString() {
        return "StreamisTableMeta{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", tags='" + tags + '\'' +
                ", description='" + description + '\'' +
                ", layer='" + layer + '\'' +
                ", scope='" + scope + '\'' +
                ", params='" + params + '\'' +
                ", workspaceName='" + workspaceName + '\'' +
                ", linkisDatasourceName='" + linkisDatasourceName + '\'' +
                ", tableName='" + tableName + '\'' +
                ", nodeName='" + nodeName + '\'' +
                ", alias='" + alias + '\'' +
                ", createBy='" + createBy + '\'' +
                ", createTime=" + createTime +
                ", updateBy='" + updateBy + '\'' +
                ", updateTime=" + updateTime +
                '}';
    }
}
