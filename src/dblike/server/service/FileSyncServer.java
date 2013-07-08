/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.server.service;

import dblike.server.ActiveClient;
import dblike.server.ActiveServer;
import dblike.service.FileInfo;
import dblike.service.WatchDirectoryService;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author JingboYu
 */
public class FileSyncServer implements Runnable {
    
    public static Hashtable<String, FileInfo> fileInfoHashtable;
    public static String directory;

    public FileSyncServer(String directory) {
        FileSyncServer.directory = directory;
    }
    
    @Override
    public void run() {
        try {
            new WatchDirectoryService(directory).watchFile();
        } catch (IOException ex) {
            Logger.getLogger(FileSyncServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
