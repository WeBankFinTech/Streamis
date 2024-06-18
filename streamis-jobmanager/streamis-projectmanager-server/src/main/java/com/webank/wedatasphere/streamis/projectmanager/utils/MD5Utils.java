package com.webank.wedatasphere.streamis.projectmanager.utils;

import com.webank.wedatasphere.streamis.jobmanager.manager.util.IoUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MD5Utils {
    private MD5Utils(){}

    private static final Logger logger = LoggerFactory.getLogger(MD5Utils.class);

    public static String getMD5(String filePath) {
        byte[] key = getBytes(filePath);
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key);
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(Arrays.hashCode(key));
        }
        return cacheKey;
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    private static byte[] getBytes(String filePath){
        byte[] buffer = null;
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        try {
             IoUtils.validateFileName(FilenameUtils.getName(filePath));
             File file = new File(filePath);
             fis = new FileInputStream(file);
             bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            buffer = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try{
                if(fis != null){
                    fis.close();
                }
            }catch(Exception e){
                logger.error("关闭输入流错误！", e);
            }

            try{
                if(bos != null){
                    bos.close();
                }
            }catch(Exception e){
                logger.error("关闭输出流错误！", e);
            }
        }
        return buffer;
    }


}
