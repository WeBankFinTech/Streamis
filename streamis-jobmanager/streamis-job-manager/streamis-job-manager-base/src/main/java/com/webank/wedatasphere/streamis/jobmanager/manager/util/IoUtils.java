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

import org.apache.commons.io.FilenameUtils;
import org.apache.linkis.common.conf.CommonVars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class IoUtils {
    private static Logger logger = LoggerFactory.getLogger(IoUtils.class);
    private static final String dateFormatDay = "yyyyMMdd";
    private static final String dateFormatTime = "HHmmss";
    private static final String ioUrl = CommonVars.apply("wds.streamis.zip.dir", "/tmp").getValue();
    private static final String FILE_NAME_REGEX = "^[a-zA-Z0-9._-]+$";

    private IoUtils(){}

    public static String generateIOPath(String userName, String projectName, String subDir) {
        String baseIOUrl = ioUrl;
        String file;
        if (!subDir.contains(".")) {
            file =subDir;
        } else {
            file = subDir.substring(0,subDir.lastIndexOf("."));
        }
        String dayStr = new SimpleDateFormat(dateFormatDay).format(new Date());
        String timeStr = new SimpleDateFormat(dateFormatTime).format(new Date());
        return addFileSeparator(baseIOUrl, projectName, dayStr, userName, file + "_" + timeStr, subDir);
    }

    private static String addFileSeparator(String... str) {
        return Arrays.stream(str).reduce((a, b) -> a + File.separator + b).orElse("");
    }

    public static OutputStream generateExportOutputStream(String path) throws IOException {
        String fileName = FilenameUtils.getName(path);
        fileName = fileName.replace("..", "");
        File file = new File(path);
        Pattern pattern = Pattern.compile(FILE_NAME_REGEX);
        Matcher matcher = pattern.matcher(fileName);
        if (!matcher.matches()) {
            file.delete();
        }
        if (file.exists()) {
            logger.warn(String.format("%s is exist,delete it", path));
            boolean success = file.delete();
            if (!success) {
                throw new IOException("Failed to delete existing file: \"" + file.getAbsolutePath() + "\"");
            }
        }
        file.getParentFile().mkdirs();
        boolean success = file.createNewFile();
        if (!success) {
            throw new IOException("Failed to create file: \"" + file.getAbsolutePath() + "\"");
        }
        return FileUtils.openOutputStream(file, true);
    }

    public static InputStream generateInputInputStream(String path) throws IOException {
        return new FileInputStream(path);
    }

    public static void validateFileName(String fileName) throws IllegalArgumentException {
        // 检查文件名是否包含目录名（"../"）以及是否仅包含字母和数字
        if (fileName.contains("../")) {
            throw new IllegalArgumentException("File name cannot contain directory traversal (\"../\")");
        }
        Pattern pattern = Pattern.compile(FILE_NAME_REGEX);
        Matcher matcher = pattern.matcher(fileName);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid characters in file name: " + fileName);
        }
    }
}
