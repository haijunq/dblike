/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.service;

import dblike.util.Utils;
import java.util.Date;
import java.util.Hashtable;

/**
 *
 * @author JingboYu
 */
public class FileInfo {
    
    private int version;
    private String deviceID;
    private String timestamp;
    private long fileSize;
    private Hashtable<String, String> fileHashCode;
    
    public FileInfo(int version, String timestamp, long fileSize, Hashtable<String, String> fileHashCode) {
        this.version = version;
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
        if (Utils.convertTimeFromString(timestamp).before(date))
            return true;
        else
            return false;
    }
    
    public boolean compareByVersion(int version) {
        if (this.version < version)
            return true;
        else
            return false;
    }
    
    @Override
    public String toString() {
        return "FileInfo{" + "version=" + version + ", timestamp=" + timestamp + ", fileSize=" + fileSize + ", fileHashCode=" + fileHashCode + '}';
    }
}
