/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.client.service;

import dblike.client.ActiveServer;
import dblike.service.FileInfo;
import dblike.service.FileInfoDiff;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author haijun
 */
public class FileSyncClientServiceTest {
    
    public FileSyncClientServiceTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of uploadCreatedFileToServer method, of class FileSyncClientService.
     */
    @Test
    public void testUploadCreatedFileToServer() throws Exception {
        System.out.println("uploadCreatedFileToServer");
        String userName = "";
        String directory = "";
        String fileName = "";
        ActiveServer activeServer = null;
        FileSyncClientService instance = null;
        instance.uploadCreatedFileToServer(userName, directory, fileName, activeServer);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of uploadModifiedFileToServer method, of class FileSyncClientService.
     */
    @Test
    public void testUploadModifiedFileToServer() throws Exception {
        System.out.println("uploadModifiedFileToServer");
        String userName = "";
        String directory = "";
        String fileName = "";
        ActiveServer activeServer = null;
        FileInfoDiff diff = null;
        FileSyncClientService instance = null;
        instance.uploadModifiedFileToServer(userName, directory, fileName, activeServer, diff);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of uploadDeletedFileToServer method, of class FileSyncClientService.
     */
    @Test
    public void testUploadDeletedFileToServer() throws Exception {
        System.out.println("uploadDeletedFileToServer");
        String userName = "";
        String directory = "";
        String fileName = "";
        ActiveServer activeServer = null;
        FileSyncClientService instance = null;
        instance.uploadDeletedFileToServer(userName, directory, fileName, activeServer);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createFile method, of class FileSyncClientService.
     */
    @Test
    public void testCreateFile() throws Exception {
        System.out.println("createFile");
        String directory = "";
        String fileName = "";
        FileSyncClientService instance = null;
        instance.createFile(directory, fileName);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of modifieFile method, of class FileSyncClientService.
     */
    @Test
    public void testModifieFile() throws Exception {
        System.out.println("modifieFile");
        String directory = "";
        String fileName = "";
        FileInfoDiff diff = null;
        FileSyncClientService instance = null;
        instance.modifieFile(directory, fileName, diff);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of deleteFile method, of class FileSyncClientService.
     */
    @Test
    public void testDeleteFile() throws Exception {
        System.out.println("deleteFile");
        String directory = "";
        String fileName = "";
        FileSyncClientService instance = null;
        instance.deleteFile(directory, fileName);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateFileInfoToServer method, of class FileSyncClientService.
     */
    @Test
    public void testUpdateFileInfoToServer() throws Exception {
        System.out.println("updateFileInfoToServer");
        String userName = "";
        String directory = "";
        String fileName = "";
        ActiveServer activeServer = null;
        FileInfo fileInfo = null;
        FileSyncClientService instance = null;
        instance.updateFileInfoToServer(userName, directory, fileName, activeServer, fileInfo);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateFileInfo method, of class FileSyncClientService.
     */
    @Test
    public void testUpdateFileInfo() throws Exception {
        System.out.println("updateFileInfo");
        String userName = "";
        String directory = "";
        String fileName = "";
        FileInfo fileInfo = null;
        FileSyncClientService instance = null;
        instance.updateFileInfo(userName, directory, fileName, fileInfo);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of syncCreatedFileToServer method, of class FileSyncClientService.
     */
    @Test
    public void testSyncCreatedFileWithServer() throws Exception {
        System.out.println("syncCreatedFileWithServer");
        String userName = "";
        String directory = "";
        String fileName = "";
        ActiveServer activeServer = null;
        FileSyncClientService instance = null;
        instance.syncCreatedFileWithServer(userName, directory, fileName, activeServer);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of syncModifiedFileToServer method, of class FileSyncClientService.
     */
    @Test
    public void testSyncModifiedFileWithServer() throws Exception {
        System.out.println("syncModifiedFileWithServer");
        String userName = "";
        String directory = "";
        String fileName = "";
        ActiveServer activeServer = null;
        FileSyncClientService instance = null;
        instance.syncModifiedFileWithServer(userName, directory, fileName, activeServer);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of syncDeletedFileToServer method, of class FileSyncClientService.
     */
    @Test
    public void testSyncDeletedFileWithServer() throws Exception {
        System.out.println("syncDeletedFileWithServer");
        String userName = "";
        String directory = "";
        String fileName = "";
        ActiveServer activeServer = null;
        FileSyncClientService instance = null;
        instance.syncDeletedFileWithServer(userName, directory, fileName, activeServer);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of syncCreatedFileFromServer method, of class FileSyncClientService.
     */
    @Test
    public void testSyncCreatedFile() throws Exception {
        System.out.println("syncCreatedFile");
        String directoryName = "";
        String fileName = "";
        FileSyncClientService instance = null;
        instance.syncCreatedFileFromServer(directoryName, fileName);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of syncModifiedFileFromServer method, of class FileSyncClientService.
     */
    @Test
    public void testSyncModifiedFile() throws Exception {
        System.out.println("syncModifiedFile");
        String directory = "";
        String fileName = "";
        FileSyncClientService instance = null;
        instance.syncModifiedFileFromServer(directory, fileName);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of syncDeletedFileFromServer method, of class FileSyncClientService.
     */
    @Test
    public void testSyncDeletedFile() throws Exception {
        System.out.println("syncDeletedFile");
        String directory = "";
        String fileName = "";
        FileSyncClientService instance = null;
        instance.syncDeletedFileFromServer(directory, fileName);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of watchFile method, of class FileSyncClientService.
     */
    @Test
    public void testWatchFile() throws Exception {
        System.out.println("watchFile");
        FileSyncClientService instance = null;
        instance.watchFile();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of run method, of class FileSyncClientService.
     */
    @Test
    public void testRun() {
        System.out.println("run");
        FileSyncClientService instance = null;
        instance.run();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of clearTmpDirs method, of class FileSyncClientService.
     */
    @Test
    public void testClearTmpDir() throws IOException {
        System.out.println("clearTmpDir");
        FileSyncClientService f = new FileSyncClientService("./users/haijun");
        f.clearTmpDirs();
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }
}