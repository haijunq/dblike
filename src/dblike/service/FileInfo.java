/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.service;

import dblike.util.Utils;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import org.joda.time.DateTime;

/**
 * Information and metadata of a file, also contains a logic clock. 
 * @author JingboYu
 */
public class FileInfo {

    private int version;
    private String deviceID;
    private String fileName;
    private String timestamp;
    private long fileSize;
    private Hashtable<String, String> fileHashCode;

    /**
     * Constructor. 
     */
    public FileInfo() {
        this.fileHashCode = new Hashtable<>();
    }

    /**
     * Constructor. 
     * @param fileInfo 
     */
    public FileInfo(FileInfo fileInfo) {
        this.version = fileInfo.getVersion();
        this.timestamp = fileInfo.getTimestamp();
        this.deviceID = fileInfo.getDeviceID();
        this.fileName = fileInfo.getFileName();
        this.fileSize = fileInfo.getFileSize();
        this.fileHashCode = fileInfo.getFileHashCode();
    }

    /**
     * Constructor.
     * @param version
     * @param deviceID
     * @param fileName
     * @param timestamp
     * @param fileSize
     * @param fileHashCode 
     */
    public FileInfo(int version, String deviceID, String fileName, String timestamp, long fileSize, Hashtable<String, String> fileHashCode) {
        this.version = version;
        this.deviceID = deviceID;
        this.fileName = fileName;
        this.timestamp = timestamp;
        this.fileSize = fileSize;
        this.fileHashCode = fileHashCode;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public Hashtable<String, String> getFileHashCode() {
        return fileHashCode;
    }

    public void setFileHashCode(Hashtable<String, String> fileHashCode) {
        this.fileHashCode = fileHashCode;
    }

    /**
     * Test whether this fileInfo object is newer than another fileInfo.
     * @param timestamp
     * @return 
     */
    public int isTimestampNewer(String timestamp) {
        DateTime thisTimestamp = new DateTime(this.timestamp);
        DateTime thatTimestamp = new DateTime(timestamp);
        if (thisTimestamp.isAfter(thatTimestamp)) {
            return 1;
        }
        if (thisTimestamp.isBefore(thatTimestamp)) {
            return -1;
        }
        return 0;
    }

    /**
     * Check version number.
     *
     * @param version
     * @return 0 if equal, 1 if newer, 2 if older
     */
    public int isVersionNewer(int version) {
        if (this.version == version) {
            return 0;
        } else if (this.version > version) {
            return 1;
        } else {
            return 2;
        }
    }

    /**
     * Check whether fileInfo from the same device. 
     * @param deviceID
     * @return 
     */
    public boolean isSameDevice(String deviceID) {
        return this.deviceID.equals(deviceID);
    }

    @Override
    public String toString() {
        return "FileInfo{" + "version=" + version + ", deviceID=" + deviceID + ", fileName=" + fileName + ", timestamp=" + timestamp + ", fileSize=" + fileSize + ", fileHashCode=" + fileHashCode + '}';
    }

    /**
     * Compare to the other fileInfo and returns the result which is indicated by the flag bit. 
     * @param anotherFileInfo
     * @return FileInfoDiff object in which the flag and different fileInfo is set. 
     */
    public FileInfoDiff comparesToFileInfo(FileInfo anotherFileInfo) {
        FileInfoDiff diff = new FileInfoDiff();
        FileInfo fileInfoThis = new FileInfo(this);
        FileInfo fileInfoThat = new FileInfo(anotherFileInfo);

        // if FileInfoThat 's version is 0, meaning there is no such file on the other side, then 
        // return FileInfoThis because it's a newer version.
        if (fileInfoThat.getVersion() == 0) {
            diff = new FileInfoDiff(1, fileInfoThis);
            return diff;
        }

        // if this and that have the same hashtable, return 0.
        if (fileInfoThis.getFileHashCode().equals(fileInfoThat.getFileHashCode())) {
            if (fileInfoThis.getVersion() >= fileInfoThat.getVersion()) {
                diff = new FileInfoDiff(0, fileInfoThis);
            } else {
                diff = new FileInfoDiff(0, fileInfoThat);
            }

            return diff;
        }
        // if fileInfoThat's version is not 0, and its hashcode table is empty, meaning the file on the other side was deleted      

        // if DeviceID are the same, then just check the version number to determain newer or older
        if (fileInfoThis.isSameDevice(fileInfoThat.getDeviceID())) {
            diff.setDeviceID(fileInfoThis.getDeviceID());
            diff.setFlag(fileInfoThis.isVersionNewer(fileInfoThat.getVersion()));
            if (diff.getFlag() == 0) {
                return diff;
            }
            if (diff.getFlag() == 2) {
                diff = new FileInfoDiff(2, fileInfoThat);
                return diff;
            }

            // else diff.flag = 1, meaning local version is newer.
            diff.setFileName(fileInfoThis.getFileName());
            diff.setTimestamp(fileInfoThis.getTimestamp());
            diff.setFileSize(fileInfoThis.getFileSize());
            diff.setVersion(fileInfoThis.getVersion());


            // empty meaning the local file is deleted.
            if (fileInfoThis.getFileHashCode().isEmpty()) {
                return diff;
            }

            // empty meaning the remote file is deleted.
            if (fileInfoThat.getFileHashCode().isEmpty()) {
                diff.setFileHashCode(fileInfoThis.getFileHashCode());
                return diff;
            }

            // else, remove the identical chunks with fileInfoThat from fileInfoThis
//            for (String chunkName1 : fileInfoThis.getFileHashCode().keySet()) {
//                for (String chunkName2 : fileInfoThat.getFileHashCode().keySet()) {
//                    if (chunkName1.equals(chunkName2) && fileInfoThis.getFileHashCode().get(chunkName1).equals(fileInfoThat.getFileHashCode().get(chunkName2))) {
//                        fileInfoThis.getFileHashCode().remove(chunkName1);
//                        continue;
//                    }
//                    diff.getFileHashCode().put(chunkName1, fileInfoThis.getFileHashCode().get(chunkName1));
//                }
//            }
//            diff.setFileHashCode(fileInfoThis.getFileHashCode());
            return diff;
        } // if not the same device, then compare the version number and timestamp to determine which copy is made.
        else {
            // local version newer, return data in fileInfoThis
            if (fileInfoThis.getVersion() > fileInfoThat.getVersion()) {
                diff.setFlag(1);
                // use fileInfoThis
                diff.setVersion(fileInfoThis.getVersion());
                diff.setFileName(fileInfoThis.getFileName());
                diff.setDeviceID(fileInfoThis.getDeviceID());
                diff.setFileSize(fileInfoThis.getFileSize());
                diff.setTimestamp(fileInfoThis.getTimestamp());

                // empty meaning the local file is deleted.
                if (fileInfoThis.getFileHashCode().isEmpty()) {
                    return diff;
                }

                // empty meaning the remote file is deleted.
                if (fileInfoThat.getFileHashCode().isEmpty()) {
                    diff.setFileHashCode(fileInfoThis.getFileHashCode());
                    return diff;
                }

                // else return the difference
//                for (String chunkName1 : fileInfoThis.getFileHashCode().keySet()) {
//                    for (String chunkName2 : fileInfoThat.getFileHashCode().keySet()) {
//                        if (chunkName1.equals(chunkName2) && fileInfoThis.getFileHashCode().get(chunkName1).equals(fileInfoThat.getFileHashCode().get(chunkName2))) {
//                            fileInfoThis.getFileHashCode().remove(chunkName1);
//                            continue;
//                        }
//                        diff.getFileHashCode().put(chunkName1, fileInfoThis.getFileHashCode().get(chunkName1));
//                    }
//                }

//                diff.setFileHashCode(fileInfoThis.getFileHashCode());
                return diff;
            } // if not newer, then make a seperate copy
            else {
                // if this is empty, meaning local file is deleted, then set local version as 0
                // set the flag to be 5, this will force the local client to download from the server
                if (fileInfoThis.getFileHashCode().isEmpty()) {
                    fileInfoThis.setVersion(0);
                    diff = new FileInfoDiff(5, fileInfoThis);
                    return diff;
                }
                // if that is empty, meaning remote file is deleted, then 
                if (fileInfoThat.getFileHashCode().isEmpty()) {
                    diff.setFlag(1);
                    diff.setVersion(fileInfoThat.getVersion()); // get the larger version number
                    diff.setFileName(fileInfoThis.getFileName());
                    diff.setTimestamp(fileInfoThis.getTimestamp());
                    diff.setDeviceID(fileInfoThis.getDeviceID());
                    diff.setFileSize(fileInfoThis.getFileSize());
                    diff.setFileHashCode(fileInfoThis.getFileHashCode());
                    return diff;
                }

                // if both not empty, then make a conflict copy by changing the newer 
                // if this is newer 
                if (fileInfoThis.isTimestampNewer(fileInfoThat.getTimestamp()) == 1) {
                    String conflict = "conflicted_copy_from_" + fileInfoThis.getDeviceID();
                    diff.setFlag(3);
                    diff.setVersion(fileInfoThis.getVersion());
                    diff.setTimestamp(fileInfoThis.getTimestamp());
                    diff.setDeviceID(fileInfoThis.getDeviceID());
                    diff.setFileSize(fileInfoThis.getFileSize());
                    diff.setFileName(conflict + fileInfoThis.getFileName());
                    diff.getFileHashCode().clear();
                    for (String key : fileInfoThis.getFileHashCode().keySet()) {
                        diff.getFileHashCode().put(conflict + key, fileInfoThis.getFileHashCode().get(key));
                    }
                    return diff;
                } // if that is newer
                else if (fileInfoThis.isTimestampNewer(fileInfoThat.getTimestamp()) == -1) {
                    String conflict = "conflicted_copy_from_" + fileInfoThat.getDeviceID();
                    diff.setFlag(4);
                    diff.setVersion(fileInfoThat.getVersion());
                    diff.setTimestamp(fileInfoThat.getTimestamp());
                    diff.setDeviceID(fileInfoThat.getDeviceID());
                    diff.setFileSize(fileInfoThat.getFileSize());
                    diff.setFileName(conflict + fileInfoThat.getFileName());
                    diff.getFileHashCode().clear();
                    for (String key : fileInfoThat.getFileHashCode().keySet()) {
                        diff.getFileHashCode().put(conflict + key, fileInfoThat.getFileHashCode().get(key));
                    }
                    return diff;
                } // if this and that timestamp is the same, break the tie and set the conflict bit
                else {
                    if (fileInfoThis.getDeviceID().compareTo(fileInfoThat.getDeviceID()) == -1) {
                        String conflict = "conflicted_copy_from_" + fileInfoThis.getDeviceID();
                        diff.setFlag(3);
                        diff.setVersion(fileInfoThis.getVersion());
                        diff.setTimestamp(fileInfoThis.getTimestamp());
                        diff.setDeviceID(fileInfoThis.getDeviceID());
                        diff.setFileSize(fileInfoThis.getFileSize());
                        diff.setFileName(conflict + fileInfoThis.getFileName());
                        diff.getFileHashCode().clear();
                        for (String key : fileInfoThis.getFileHashCode().keySet()) {
                            diff.getFileHashCode().put(conflict + key, fileInfoThis.getFileHashCode().get(key));
                        }
                        return diff;
                    } else {
                        String conflict = "conflicted_copy_from_" + fileInfoThat.getDeviceID();
                        diff.setFlag(4);
                        diff.setVersion(fileInfoThat.getVersion());
                        diff.setTimestamp(fileInfoThat.getTimestamp());
                        diff.setDeviceID(fileInfoThat.getDeviceID());
                        diff.setFileSize(fileInfoThat.getFileSize());
                        diff.setFileName(conflict + fileInfoThat.getFileName());
                        diff.getFileHashCode().clear();
                        for (String key : fileInfoThat.getFileHashCode().keySet()) {
                            diff.getFileHashCode().put(conflict + key, fileInfoThat.getFileHashCode().get(key));
                        }
                        return diff;
                    }
                }
                // use fileInfoThat, rename all the entries in the FileInfo
            }
//            return diff;
        }


//        for (String chunkName1 : fileInfoThis.getFileHashCode().keySet()) {
//            for (String chunkName2 : fileInfoThat.getFileHashCode().keySet()) {
//                if (chunkName1.equals(chunkName2) && fileInfoThis.getFileHashCode().get(chunkName1).equals(fileInfoThat.getFileHashCode().get(chunkName2))) {
//                    fileInfoThis.getFileHashCode().remove(chunkName1);
//                    fileInfoThat.getFileHashCode().remove(chunkName2);
//
//                }
//            }
//        }
//
//        for (String chunkName2 : fileInfoThat.getFileHashCode().keySet()) {
//            for (String chunkName1 : fileInfoThis.getFileHashCode().keySet()) {
//                if (chunkName1.equals(chunkName2) && fileInfoThis.getFileHashCode().get(chunkName1).equals(fileInfoThat.getFileHashCode().get(chunkName2))) {
//                    fileInfoThis.getFileHashCode().remove(chunkName1);
//                    fileInfoThat.getFileHashCode().remove(chunkName2);
//
//                }
//            }
//        }
//        return diff;
    }
}
