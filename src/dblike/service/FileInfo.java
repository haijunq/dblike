/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.service;

import dblike.util.Utils;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

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
    }

    public FileInfo(FileInfo fileInfo) {
        this.version = fileInfo.getVersion();
        this.timestamp = fileInfo.getTimestamp();
        this.deviceID = fileInfo.getDeviceID();
        this.fileName = fileInfo.getFileName();
        this.fileSize = fileInfo.getFileSize();
        this.fileHashCode = fileInfo.getFileHashCode();
    }

    public FileInfo(int version, String deviceID, String fileName,  String timestamp, long fileSize, Hashtable<String, String> fileHashCode) {
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

    public boolean compareByTime(Date date) {
        if (Utils.convertTimeFromString(timestamp).before(date)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean compareByVersion(int version) {
        if (this.version < version) {
            return true;
        } else {
            return false;
        }
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
    public ArrayList<FileInfo> compareByHashCode(FileInfo anotherFileInfo) {
        ArrayList<FileInfo> diff = new ArrayList<>();
        diff.add(new FileInfo(this));
        diff.add(new FileInfo(anotherFileInfo));

        for (String chunkName1 : diff.get(0).getFileHashCode().keySet()) {
            for (String chunkName2 : diff.get(1).getFileHashCode().keySet()) {
                if (chunkName1.equals(chunkName2) && diff.get(0).getFileHashCode().get(chunkName1).equals(diff.get(1).getFileHashCode().get(chunkName2))) {
                    diff.get(0).getFileHashCode().remove(chunkName1);
                    diff.get(1).getFileHashCode().remove(chunkName2);

                }
            }
        }

        for (String chunkName2 : diff.get(1).getFileHashCode().keySet()) {
            for (String chunkName1 : diff.get(0).getFileHashCode().keySet()) {
                if (chunkName1.equals(chunkName2) && diff.get(0).getFileHashCode().get(chunkName1).equals(diff.get(1).getFileHashCode().get(chunkName2))) {
                    diff.get(0).getFileHashCode().remove(chunkName1);
                    diff.get(1).getFileHashCode().remove(chunkName2);

                }
            }
        }
        return diff;
    }
}
