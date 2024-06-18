package com.webank.wedatasphere.streamis.jobmanager.utils;

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.conf.JobConf;
import org.apache.commons.lang3.StringUtils;

public class RegularUtil {

    private RegularUtil(){}

    public static final String LETTER_PATTERN="^.*[a-zA-Z]+.*$";//字母
    public static final String NUMBER_PATTERN="^.*[0-9]+.*$";//数字
    public static final String SPECIAL_CHAR_PATTERN="^.*[/^/$/.//,;:'!@#%&/*/|/?/+/(/)/[/]/{/}]+.*$";//特殊字符
    public static final String PW_LENGTH_PATTERN="^.{0,64}$";//字符长度
    public static final String PATTERN ="^[A-Za-z0-9_,.]{1,64}$" ; //大小写字母数字下划线逗号小数点、

    public static final String JOB_NAME_PATTERN ="^[A-Za-z0-9_,.]+" ; //大小写字母数字下划线逗号小数点

    public static final String PRODUCT_NAME_PATTERN = "^[A-Za-z0-9_]+" ; //大小写字母数字下划线

    public static boolean matches(String input) {
        if (StringUtils.isNotBlank(input)){
            return input.matches(PATTERN);
        }
        return false;
    }

    public static boolean matchesJobName(String jobName) {
        int length = Integer.parseInt(JobConf.JOB_NAME_LENGTH_MAX().getHotValue().toString());
        if (StringUtils.isNotBlank(jobName) && jobName.length() < length){
            return jobName.matches(JOB_NAME_PATTERN);
        }
        return false;
    }

    public static boolean matchesProductName(String productName) {
        return productName.matches(PRODUCT_NAME_PATTERN);
    }




}
