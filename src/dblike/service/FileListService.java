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

    private static Hashtable<String, byte[]> fileHashTable = null;

    private FileListService() {
    }

//    public FileListService getInstance() {
//        if (fileHashTable == null) {
//            fileHashTable = new Hashtable<String, byte []>();
//        }
//        return fileHashTable;
//    }
}
