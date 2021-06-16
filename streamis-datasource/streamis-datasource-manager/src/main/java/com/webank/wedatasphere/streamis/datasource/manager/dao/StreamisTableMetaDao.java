package com.webank.wedatasphere.streamis.datasource.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.wedatasphere.streamis.datasource.manager.domain.StreamisTableMeta;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * StreamTableMeta表 Mapper 接口
 * </p>
 *
 * @author c01013
 * @since 2021-04-02
 */
@Mapper
public interface StreamisTableMetaDao extends BaseMapper<StreamisTableMeta> {

}
