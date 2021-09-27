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
@TableName("streamis_datasource_extra_info")
public class StreamisDatasourceExtraInfo extends Model<StreamisDatasourceExtraInfo> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField(value="`key`")
    private String key;
    private String value;
    @TableField("streamis_table_meta_id")
    private Long streamisTableMetaId;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Long getStreamisTableMetaId() {
        return streamisTableMetaId;
    }

    public void setStreamisTableMetaId(Long streamisTableMetaId) {
        this.streamisTableMetaId = streamisTableMetaId;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }


    @Override
    public String toString() {
        return "StreamisDatasourceExtraInfo{" +
        ", id=" + id +
        ", key=" + key +
        ", value=" + value +
        ", streamisTableMetaId=" + streamisTableMetaId +
        "}";
    }
}
