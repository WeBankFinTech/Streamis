package com.webank.wedatasphere.streamis.jobmanager.log.server.storage;

import com.webank.wedatasphere.streamis.jobmanager.log.server.exception.StreamJobLogException;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.utils.MemUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermissions;

/**
 * Storage context (represent the driver/disk)
 */
public class JobLogStorageContext{

    /**
     * Store path
     */
    private Path storePath;

    /**
     * Store information
     */
    private FileStore storeInfo;
    /**
     * Score of storage context
     */
    public double score;

    public JobLogStorageContext(String path, double score){
        this.storePath = Paths.get(path);
        this.storeInfo = initStorePath(this.storePath);
        this.score = score;
    }


    private FileStore initStorePath(Path path){
        if (Files.notExists(path)){
            try {
                Files.createDirectories(this.storePath,
                        PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxrwxr--")));
            } catch (IOException e) {
                throw new StreamJobLogException.Runtime(-1,
                        "Cannot make the storage path directory: [" + path + "], message: " + e.getMessage());
            }
            // Allow dir link
        } else if (!Files.isDirectory(path)){
            throw new StreamJobLogException.Runtime(-1,
                    "the storage path: [" + path + "] is not directory" );
        }
        try {
            return Files.getFileStore(path);
        } catch (IOException e) {
            throw new StreamJobLogException.Runtime(-1,
                    "Fail to get the storage information in path: [" + path + "], message: " + e.getMessage());
        }
    }

    public Path getStorePath() {
        return storePath;
    }

    /**
     * Score
     * @return score value
     */
    public double getScore() {
        return score;
    }

    /**
     * Total space
     * @return bytes return
     * @throws IOException
     */
    public long getTotalSpace() throws IOException {
        long result = storeInfo.getTotalSpace();
        if (result < 0){
            result = Long.MAX_VALUE;
        }
        return result;
    }

    /**
     * Usable space
     * @return bytes return
     * @throws IOException
     */
    public long getUsableSpace() throws IOException {
        long result = storeInfo.getUsableSpace();
        if (result < 0){
            result = Long.MAX_VALUE;
        }
        return result;
    }

    /**
     * The percent of usable disk
     * @return bytes return
     * @throws IOException
     */
    public double getUsablePercent() throws IOException{
        long result = storeInfo.getUnallocatedSpace();
        if (result < 0){
            result = Long.MAX_VALUE;
        }
        return result;
    }
    /**
     * Unallocated space
     * @return bytes return
     * @throws IOException
     */
    public long getUnallocatedSpace() throws IOException{
        long result = storeInfo.getUnallocatedSpace();
        if (result < 0){
            result = Long.MAX_VALUE;
        }
        return result;
    }
}
