/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.service;

import java.util.Hashtable;

/**
 * This singleton class maintains the hash table of the files.
 *
 * @author haijun
 */
public final class FileListService {

    private String pathname;
    private Hashtable<String, FileInfo> fileHashTable ; 
    //every user needs a FileListService object, so I removed "static"
    
   
//    public FileListService getInstance() {
//        if (fileHashTable == null) {
//            fileHashTable = new Hashtable<String, byte []>();
//        }
//        return fileHashTable;
//    }

    public FileListService(String pathname) {
        this.pathname = pathname;
        fileHashTable = new Hashtable<>();
    }

    public Hashtable<String, FileInfo> getFileHashTable() {
        return this.fileHashTable;
    }

    public void setFileHashTable(Hashtable<String, FileInfo> fileHashTable) {
        this.fileHashTable = fileHashTable;
    }

    public FileInfo getFileInfoByFileName(String fileName) {
        
        if (fileHashTable.containsKey(fileName))
            return fileHashTable.get(fileName);
        else
            return null;
    }
    
    @Override
    public String toString() {
        return "FileListService{" + '}';
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
