package com.webank.wedatasphere.streamis.jobmanager.log.server.storage.context;

import com.webank.wedatasphere.streamis.jobmanager.log.server.exception.StreamJobLogException;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.UUID;

/**
 * Storage context (represent the driver/disk)
 */
public class JobLogStorageContext{

    /**
     * Context id
     */
    private final String id;

    /**
     * Store path
     */
    private final Path storePath;

    /**
     * Store information
     */
    private final FileStore storeInfo;
    /**
     * Score of storage context
     */
    private final double score;

    /**
     * Storage weight
     */
    private double storeWeight;

    public JobLogStorageContext(String path, double score){
        this.id = UUID.randomUUID().toString();
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

    public String getId() {
        return id;
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

    public double getStoreWeight() {
        return storeWeight;
    }

    public void setStoreWeight(double storeWeight) {
        this.storeWeight = storeWeight;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof JobLogStorageContext){
            return this.id.equals(((JobLogStorageContext) o).id);
        }
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
}
