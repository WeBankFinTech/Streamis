package com.webank.wedatasphere.streamis.jobmanager.manager.dao;

import com.webank.wedatasphere.streamis.jobmanager.manager.entity.JobTemplateFiles;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StreamJobTemplateMapper {
    JobTemplateFiles getJobTemplate(@Param("id")Long id, @Param("enable") Boolean enable);

    void insertJobTemplate(JobTemplateFiles jobTemplateFiles);

    JobTemplateFiles selectJobTemplate(@Param("name")String name, @Param("version")String version, @Param("projectName")String projectName);

    void updateJobTemplateById (JobTemplateFiles jobTemplateFiles);

    String getLatestJobTemplate (@Param("projectName")String projectName,@Param("enable") Boolean enable);

    JobTemplateFiles getLatestJobTemplateFile (@Param("projectName")String projectName,@Param("enable") Boolean enable);

    Long getJobTemplateByProject(@Param("projectName")String projectName);


    Integer setEnableByVersion(@Param("name")String name, @Param("version")String version,@Param("enable")Boolean enable);

    void deleteTemplateVersions(@Param("name")String name, @Param("projectName")String projectName);

    List<Long> selectTemplateId(@Param("name")String name, @Param("projectName")String projectName);

    void setEnable(@Param("templateIds")List<Long> templateIds,@Param("enable")Boolean enable);


}
