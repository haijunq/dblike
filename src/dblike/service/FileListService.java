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
    private static Hashtable<String, FileInfo> fileHashTable ;
    
   
//    public FileListService getInstance() {
//        if (fileHashTable == null) {
//            fileHashTable = new Hashtable<String, byte []>();
//        }
//        return fileHashTable;
//    }

    public FileListService(String pathname) {
        this.pathname = pathname;
    }

    public static Hashtable<String, FileInfo> getFileHashTable() {
        return fileHashTable;
    }

    public static void setFileHashTable(Hashtable<String, FileInfo> fileHashTable) {
        FileListService.fileHashTable = fileHashTable;
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
}
