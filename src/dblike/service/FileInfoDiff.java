/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.service;

import java.util.Hashtable;

/**
 * This class extends FileInfo and set the different bit. 
 * @author haijun
 */
public class FileInfoDiff extends FileInfo{
    int flag;

    public FileInfoDiff() {
    }
    
    public FileInfoDiff(int flag) {
        this.flag = flag;
    }

    public FileInfoDiff(int flag, FileInfo fileInfo) {
        super(fileInfo);
        this.flag = flag;
    }

    public FileInfoDiff(int flag, int version, String deviceID, String fileName, String timestamp, long fileSize, Hashtable<String, String> fileHashCode) {
        super(version, deviceID, fileName, timestamp, fileSize, fileHashCode);
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    @Override
    public String toString() {
        return "FileInfoDiff{" + "flag=" + flag + "," + super.toString()+ '}';
    }
    
    
}
