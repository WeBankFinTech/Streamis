package com.webank.wedatasphere.streamis.datasource.manager.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;

/**
 * <p>
 * 数据源授权表
 * </p>
 *
 * @author c01013
 * @since 2021-04-02
 */
@TableName("streamis_datasource_authority")
public class StreamisDatasourceAuthority extends Model<StreamisDatasourceAuthority> {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * streamis_table_meta 的id
     */
    @TableField("streamis_table_meta_id")
    private Long streamisTableMetaId;
    /**
     * 授权范围,1:workspace，2:project,3:task
     */
    @TableField("authority_scope")
    private String authorityScope;
    /**
     * 授权范围的id,如果是workspace级别授权，则是workspaceid，如果是project 则是projectId
     */
    @TableField("authority_id")
    private String authorityId;
    /**
     * 授权用户名
     */
    @TableField("grant_user")
    private String grantUser;


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

    public String getAuthorityScope() {
        return authorityScope;
    }

    public void setAuthorityScope(String authorityScope) {
        this.authorityScope = authorityScope;
    }

    public String getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(String authorityId) {
        this.authorityId = authorityId;
    }

    public String getGrantUser() {
        return grantUser;
    }

    public void setGrantUser(String grantUser) {
        this.grantUser = grantUser;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "StreamisDatasourceAuthority{" +
        ", id=" + id +
        ", streamisTableMetaId=" + streamisTableMetaId +
        ", authorityScope=" + authorityScope +
        ", authorityId=" + authorityId +
        ", grantUser=" + grantUser +
        "}";
    }
}
