/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.service.test;

import dblike.service.WatchDirectoryService;
import java.io.IOException;

/**
 *
 * @author JingboYu
 */
public class WatchDirectoryServiceTest {
    
    public static void main(String[] args) throws IOException {
        new WatchDirectoryService("./").watchFile();
    }
}
