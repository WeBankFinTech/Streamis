package com.webank.wedatasphere.streamis.jobmanager.utils;

import org.apache.commons.lang3.StringUtils;

public class RegularUtil {
    public static final String LETTER_PATTERN="^.*[a-zA-Z]+.*$";//字母
    public static final String NUMBER_PATTERN="^.*[0-9]+.*$";//数字
    public static final String SPECIAL_CHAR_PATTERN="^.*[/^/$/.//,;:'!@#%&/*/|/?/+/(/)/[/]/{/}]+.*$";//特殊字符
    public static final String PW_LENGTH_PATTERN="^.{0,64}$";//字符长度
    public static final String PATTERN ="^[A-Za-z0-9_,.]{1,64}$" ; //大小写字母数字下划线逗号小数点


    public static boolean matches(String input) {
        if (StringUtils.isNotBlank(input)){
            return input.matches(PATTERN);
        }
        return false;
    }


    /**
     * 特殊字符查询加转义符
     * @param string
     * @return
     */
    public static String escapeChar(String string) {
        if (StringUtils.isNotBlank(string)) {
            string = string.replaceAll("\\\\", "\\\\\\\\")
                    .replace("_", "\\_").replace("\'", "\\'")
                    .replace("%", "\\%").replace("*", "\\*");

        }
        return string;
    }

}
