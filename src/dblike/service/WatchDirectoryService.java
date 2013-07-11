/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.service;

import dblike.server.ActiveServer;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

/**
 *
 * @author JingboYu
 */
public class WatchDirectoryService {
    
    private final WatchService watchService;
    private final Path directory;
    
    public WatchDirectoryService(String searchDir) throws IOException {
        
        FileSystem fs = FileSystems.getDefault();
        watchService = fs.newWatchService();
        directory = fs.getPath(searchDir);
        directory.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE, OVERFLOW);
        System.out.println("Registered watchService on " + directory);
        
    }
    
    public void watchFile() throws IOException {
        
        for (;;) {
            
            WatchKey key;
            try {
                key = watchService.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
            
            for (WatchEvent<?> e : key.pollEvents()) {
                
                @SuppressWarnings("unchecked")
                        WatchEvent<Path> event = (WatchEvent<Path>) e;
                
                Path fileName = event.context();
                Path child = directory.resolve(fileName);
                String contentType = Files.probeContentType(child);
                
                if (e.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                    System.out.println("file was created: " + fileName + " contentType: " + contentType);
                } else if (e.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                    System.out.println("file was modified: " + fileName + " contentType: " + contentType);
                } else if (e.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                    System.out.println("file was deleted: " + fileName);
                } else if (e.kind() == StandardWatchEventKinds.OVERFLOW) {
                    System.out.println("overflow occurred");
                    continue;
                }
                
                boolean valid = key.reset();
                if (!valid) {
                    System.out.println("object no longer registered");
                    break;
                }
                
            }
        }
        
    }

}