package com.webank.wedatasphere.streamis.jobmanager.manager.util;

import java.io.File;

public class FileUtils {
    public static boolean checkDirExists(String dir){
        File file = new File(dir);
        return file.exists() && file.isDirectory();
    }
}
