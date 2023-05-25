package com.webank.wedatasphere.streamis.jobmanager.utils;

public class RegularUtil {
    public static final String LETTER_PATTERN="^.*[a-zA-Z]+.*$";//字母
    public static final String NUMBER_PATTERN="^.*[0-9]+.*$";//数字
    public static final String SPECIAL_CHAR_PATTERN="^.*[/^/$/.//,;:'!@#%&/*/|/?/+/(/)/[/]/{/}]+.*$";//特殊字符
    public static final String PW_LENGTH_PATTERN="^.{0,64}$";//字符长度
    public static final String PATTERN ="^[A-Za-z0-9_,.]{1,64}$" ; //大小写字母数字下划线逗号小数点


    public static boolean matches(String input) {
      return   input.matches(PATTERN);
    }

}
