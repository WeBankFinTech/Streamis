package com.webank.wedatasphere.streamis.jobmanager.manager.util;


import com.webank.wedatasphere.streamis.jobmanager.manager.conf.JobConf;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class IoUtils {
    private static Logger logger = LoggerFactory.getLogger(IoUtils.class);
    private static final String DATE_FORMAT="yyyyMMddHHmmssSSS";

    public static String generateIOPath(String userName,String projectName,String subDir){
        String baseUrl = JobConf.JOBMANAGER_EXPORT_URL().getValue();
        String dataStr = new SimpleDateFormat(DATE_FORMAT).format(new Date());
        return addFileSeparator(baseUrl,dataStr,userName,projectName,subDir);
    }

    public static String addFileSeparator(String... str){
        return Arrays.stream(str).reduce((a,b)-> a + File.separator + b).orElse("");
    }

    public static OutputStream generateExportOutputStream(String path) throws IOException {
        File file = new File(path);
        if(!file.getParentFile().exists()){
            FileUtils.forceMkdir(file.getParentFile());
        }

        if(file.exists()){
            logger.warn(String.format("%s is exist,delete it", path));
            file.delete();
        }
        file.createNewFile();
        return FileUtils.openOutputStream(file,true);
    }

    public static InputStream generateInputInputStream(String path) throws IOException {
        return new FileInputStream(path);
    }
}
