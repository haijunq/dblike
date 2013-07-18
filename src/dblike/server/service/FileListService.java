/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.server.service;

import dblike.service.FileInfo;
import java.util.Hashtable;

/**
 * This singleton class maintains the hash table of the files.
 *
 * @author haijun
 */
public final class FileListService {
    
    private String pathname; //username
    private Hashtable<String, FileInfo> fileHashTable ;
    
    
    //    public FileListService getInstance() {
    //        if (fileHashTable == null) {
    //            fileHashTable = new Hashtable<String, byte []>();
    //        }
    //        return fileHashTable;
    //    }
    
    
    //every user needs a FileListService object, so I removed "static"
    //        if (fileHashTable == null) {
    //            fileHashTable = new Hashtable<String, byte []>();
    //        }
    //        return fileHashTable;
    //    }
    
    
    public String getPathname() {
        return pathname;
    }
    
    public void setPathname(String pathname) {
        this.pathname = pathname;
    }
    
    public FileListService() {
        this.pathname = "";
        this.fileHashTable = new Hashtable<>();
    }
    
    
    
    public FileListService(String username) {
        this.pathname = "./users/" + username + "/";
        fileHashTable = new Hashtable<>();
    }
    
    public Hashtable<String, FileInfo> getFileHashTable() {
        return this.fileHashTable;
    }
    
    public void setFileHashTable(Hashtable<String, FileInfo> fileHashTable) {
        this.fileHashTable = fileHashTable;
    }
    
    public boolean containFileInfo(String fileName) {
        
        return fileHashTable.containsKey(fileName);
    }
    
    public FileInfo getFileInfo(String fileName) {
        
        return fileHashTable.get(fileName);
    }
    
    public FileInfo getFileInfoByFileName(String fileName) {
        
        if (fileHashTable.containsKey(fileName))
            return fileHashTable.get(fileName);
        else
            return null;
    }

    @Override
    public String toString() {
        return "FileListService{" + "pathname=" + pathname + ", fileHashTable=" + fileHashTable + '}';
    }
    
    
    public void addNewFileInfo(FileInfo newFileInfo) {
        this.fileHashTable.put(newFileInfo.getFileName(), newFileInfo);
    }
    
    public void updateFileInfo(FileInfo newFileInfo) {
        this.fileHashTable.put(newFileInfo.getFileName(), newFileInfo);
    }
    
    public void removeFileInfo(String fileName) {
        this.fileHashTable.remove(fileName);
    }
    
    
}
