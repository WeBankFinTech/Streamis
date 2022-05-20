package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.state;

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.state.JobState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.state.JobStateConf.CHECKPOINT_PATH_PATTERN;

public class CheckpointJobStateFetcher extends AbstractLinkisJobStateFetcher<JobState> {

    private static final Logger logger = LoggerFactory.getLogger(CheckpointJobStateFetcher.class);

    @Override
    public JobState getState(JobStateFileInfo pathInfo) {
        Checkpoint checkpoint = new Checkpoint(pathInfo.getPath());
        checkpoint.setMetadataInfo(pathInfo);
        checkpoint.setTimestamp(pathInfo.getModifytime());
        logger.info("checkpoint info is {}",checkpoint);
        return checkpoint;
    }

    @Override
    protected boolean isMatch(String path) {
        Pattern pattern = Pattern.compile(CHECKPOINT_PATH_PATTERN);
        Matcher matcher = pattern.matcher(path);
        return matcher.matches();
    }

}
