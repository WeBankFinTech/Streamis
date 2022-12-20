package com.webank.wedatasphere.streamis.jobmanager.log.utils;

/**
 * Tool to operate str
 */
public class StringUtils {

    /**
     * Convert string to array
     * @param input string
     * @param delimiter delimiter
     * @return array
     */
    public static String[] convertStrToArray(String input, String delimiter){
        if (null != input && !input.trim().equals("") &&
                !input.equals(delimiter.trim())){
            return input.split(",");
        }
        return null;
    }

}
