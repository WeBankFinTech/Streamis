package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.state;

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.state.JobState;
import org.apache.linkis.common.exception.WarnException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

public class CheckpointJobStateFetcher extends AbstractLinkisJobStateFetcher<JobState> {

    private static final Logger logger = LoggerFactory.getLogger(CheckpointJobStateFetcher.class);

    @Override
    public JobState getState(JobStateResult jobStateResult) {
        DirFileTree dirFileTrees = jobStateResult.getDirFileTrees();
        if(dirFileTrees == null){
            logger.warn("dirFileTrees in the results is null");
            throw new WarnException(-1, "dirFileTrees in the results is null");
        }
        String path = dirFileTrees.getPath();
        String name = dirFileTrees.getName();
        List<DirFileTree> childrenList = dirFileTrees.getChildren();
        DirFileTree lastchildren = new DirFileTree();
        String initModifytime = "0";
        lastchildren.getProperties().put("modifytime", initModifytime);
        for (DirFileTree children:childrenList) {
            String childrenPath = children.getPath();
            String childrenName = children.getName();
            HashMap<String, String> properties = children.getProperties();
            String childrenParentPath = children.getParentPath();
            Boolean isLeaf = children.getIsLeaf();
            if(isLeaf){
                long size = Long.parseLong(properties.get("size"));
                long modifytime = Long.parseLong(properties.get("modifytime"));
                if(modifytime > Long.parseLong(lastchildren.getProperties().get("modifytime"))){
                    lastchildren = children;
                }
            }
        }
        if(initModifytime.equals(lastchildren.getProperties().get("modifytime"))){
            logger.warn("childrenDirFileTrees have no leaf nodes, the obtained checkpoint is null");
            throw new WarnException(-1, "childrenDirFileTrees have no leaf nodes, the obtained checkpoint is null");
        }
        Checkpoint checkpoint = new Checkpoint(lastchildren.getPath());
        checkpoint.setMetadataInfo(lastchildren);
        checkpoint.setTimestamp(Long.parseLong(lastchildren.getProperties().get("modifytime")));
        logger.info("checkpoint info is {}",checkpoint);
        return checkpoint;
    }
}
