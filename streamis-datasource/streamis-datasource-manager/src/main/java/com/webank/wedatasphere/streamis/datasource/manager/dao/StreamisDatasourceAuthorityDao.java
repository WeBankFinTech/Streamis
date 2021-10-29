package com.webank.wedatasphere.streamis.datasource.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.wedatasphere.streamis.datasource.manager.domain.StreamisDatasourceAuthority;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 数据源授权表 Mapper 接口
 * </p>
 *
 * @author c01013
 * @since 2021-04-02
 */
@Mapper
public interface StreamisDatasourceAuthorityDao extends BaseMapper<StreamisDatasourceAuthority> {

}
