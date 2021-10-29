package com.webank.wedatasphere.streamis.jobmanager.launcher.dao;

import com.webank.wedatasphere.streamis.jobmanager.launcher.entity.JobUserRole;
import com.webank.wedatasphere.streamis.jobmanager.launcher.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author limeng
 */
public interface JobUserRoleMapper {

    void insertUser(User user);

    void insertJobUserRole(JobUserRole jobUserRole);

    void updateJobUserRole(JobUserRole jobUserRole);

    void deleteByJobUserRole(@Param("id") Long id,@Param("jobId") Long jobId);


    List<User> getUsersByUserName(@Param("username") String username);

    List<JobUserRole> getUserRoleById(@Param("jobId") Long jobId,@Param("username") String usernam);

}
