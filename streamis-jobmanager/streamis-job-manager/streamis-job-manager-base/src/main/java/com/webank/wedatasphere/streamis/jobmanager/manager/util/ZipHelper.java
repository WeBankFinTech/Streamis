/*
 * Copyright 2021 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.streamis.jobmanager.manager.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class ZipHelper {
    private static final Logger logger = LoggerFactory.getLogger(ZipHelper.class);
    private static final String UN_ZIP_CMD = "unzip";
    private static final String ZIP_TYPE = ".zip";

    private ZipHelper(){}

    public static String unzip(String dirPath)throws Exception {
        File file = new File(dirPath);
        if(!file.exists()){
            logger.error("{} does not exist, can not unzip", dirPath);
            throw new Exception(dirPath + " does not exist, can not unzip");
        }
        //First use simple approach, call new process to zip(先用简单的方法，调用新进程进行压缩)
        String[] strArr = dirPath.split(File.separator);
        String shortPath = strArr[strArr.length - 1];
        String workPath = dirPath.substring(0, dirPath.length() - shortPath.length() - 1);
        List<String> list = new ArrayList<>();
        list.add(UN_ZIP_CMD);
        String longZipFilePath = dirPath.replace(ZIP_TYPE,"");
        list.add(shortPath);
        ProcessBuilder processBuilder = new ProcessBuilder(list);
        processBuilder.redirectErrorStream(true);
        processBuilder.directory(new File(workPath));
        BufferedReader infoReader = null;
        BufferedReader errorReader = null;
        try{
            Process process = processBuilder.start();
            infoReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String infoLine = null;
            while((infoLine = infoReader.readLine()) != null){
                logger.info("process output: {} ", infoLine);
            }
            String errorLine = null;
            StringBuilder errMsg = new StringBuilder();
            while((errorLine = errorReader.readLine()) != null){
                if (StringUtils.isNotEmpty(errorLine)){
                    errMsg.append(errorLine).append("\n");
                }
                logger.error("process error: {} ", errorLine);
            }
            int exitCode = process.waitFor();
            if (exitCode != 0){
                throw new Exception(errMsg.toString());
            }
        }catch(final Exception e){
            logger.error("Fail to unzip file(解压缩 zip 文件失败), reason: ", e);
            Exception exception = new Exception(dirPath + " to zip file failed");
            exception.initCause(e);
            throw exception;
        } finally {
            logger.info("generate unzip catalogue{}", workPath);
            IOUtils.closeQuietly(infoReader);
            IOUtils.closeQuietly(errorReader);
        }
        return longZipFilePath;
    }

    public static boolean isZip(String fileName){
        return fileName.substring(fileName.lastIndexOf('.')).equals(ZIP_TYPE);
    }
}
