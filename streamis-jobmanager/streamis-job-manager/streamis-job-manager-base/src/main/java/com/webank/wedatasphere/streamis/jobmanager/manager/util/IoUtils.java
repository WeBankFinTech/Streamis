package com.webank.wedatasphere.streamis.jobmanager.manager.util;


import com.webank.wedatasphere.streamis.jobmanager.manager.conf.JobConf;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

public class IoUtils {
    private static Logger logger = LoggerFactory.getLogger(IoUtils.class);
    private static final String DATE_FORMAT="yyyyMMddHHmmssSSS";

    public static String generateIOPath(String userName,String projectName,String subDir){
        String baseUrl = JobConf.JOBJAR_EXPORT_URL().getValue();
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

    public static Properties getProperties(String path)  {
        Properties properties = new Properties();
        FileInputStream input =null;
        try {
            input = FileUtils.openInputStream(new File(path));
            properties.load(new FileInputStream(path));
            return properties;
        }catch (Exception ex){
            logger.error(String.format("path:%s msg:%s",path,ex.getMessage()));
        }finally {
            IOUtils.closeQuietly(input);
        }
        return null;
    }

}
