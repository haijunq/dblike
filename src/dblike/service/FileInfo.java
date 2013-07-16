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
 *
 * @author JingboYu
 */
public class FileInfo {

    private int version;
    private String deviceID;
    private String fileName;
    private String timestamp;
    private long fileSize;
    private Hashtable<String, String> fileHashCode;

    public FileInfo() {
        this.fileHashCode = new Hashtable<>();
    }

    public FileInfo(FileInfo fileInfo) {
        this.version = fileInfo.getVersion();
        this.timestamp = fileInfo.getTimestamp();
        this.deviceID = fileInfo.getDeviceID();
        this.fileName = fileInfo.getFileName();
        this.fileSize = fileInfo.getFileSize();
        this.fileHashCode = fileInfo.getFileHashCode();
    }

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

    public boolean isTimestampNewer(String timestamp) {
        return new DateTime(this.timestamp).isAfter(new DateTime(timestamp));
    }

    public boolean isVersionNewer(int version) {
        if (this.version < version) {
            return true;
        } else {
            return false;
        }
    }
    
    public boolean isSameDevice(String deviceID) {
        return this.deviceID.equals(deviceID);
    }

    @Override
    public String toString() {
        return "FileInfo{" + "version=" + version + ", deviceID=" + deviceID + ", fileName=" + fileName + ", timestamp=" + timestamp + ", fileSize=" + fileSize + ", fileHashCode=" + fileHashCode + '}';
    }

    /**
     *
     * @param anotherFileInfo
     * @return
     */
    public FileInfoDiff comparesToFileInfo(FileInfo anotherFileInfo) {
        FileInfoDiff diff = new FileInfoDiff();
        FileInfo fileInfoThis = new FileInfo(this);
        FileInfo fileInfoThat = new FileInfo(anotherFileInfo);
        
        for (String chunkName1 : fileInfoThis.getFileHashCode().keySet()) {
            for (String chunkName2 : fileInfoThat.getFileHashCode().keySet()) {
                if (chunkName1.equals(chunkName2) && fileInfoThis.getFileHashCode().get(chunkName1).equals(fileInfoThat.getFileHashCode().get(chunkName2))) {
                    fileInfoThis.getFileHashCode().remove(chunkName1);
                    fileInfoThat.getFileHashCode().remove(chunkName2);

                }
            }
        }

        for (String chunkName2 : fileInfoThat.getFileHashCode().keySet()) {
            for (String chunkName1 : fileInfoThis.getFileHashCode().keySet()) {
                if (chunkName1.equals(chunkName2) && fileInfoThis.getFileHashCode().get(chunkName1).equals(fileInfoThat.getFileHashCode().get(chunkName2))) {
                    fileInfoThis.getFileHashCode().remove(chunkName1);
                    fileInfoThat.getFileHashCode().remove(chunkName2);

                }
            }
        }
        return diff;
    }

}
