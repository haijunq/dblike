/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.client.service;

import dblike.service.FileInfo;
import dblike.service.FileInfoDiff;
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
        FileSyncClientService instance = null;
        instance.uploadCreatedFileToServer(userName, directory, fileName);
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
        FileInfoDiff diff = null;
        FileSyncClientService instance = null;
        instance.uploadModifiedFileToServer(userName, directory, fileName, diff);
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
        FileSyncClientService instance = null;
        instance.uploadDeletedFileToServer(userName, directory, fileName);
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
        FileInfo fileInfo = null;
        FileSyncClientService.createFile(directory, fileName, fileInfo);
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
        FileSyncClientService.modifieFile(directory, fileName, diff);
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
        FileSyncClientService.deleteFile(directory, fileName);
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
        FileInfo fileInfo = null;
        FileSyncClientService instance = null;
        instance.updateFileInfoToServer(userName, directory, fileName, fileInfo);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateFileInfoFromServer method, of class FileSyncClientService.
     */
    @Test
    public void testUpdateFileInfoFromServer() throws Exception {
        System.out.println("updateFileInfoFromServer");
        String userName = "";
        String directory = "";
        String fileName = "";
        FileSyncClientService.updateFileInfoFromServer(userName, directory, fileName);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateLocalFileInfo method, of class FileSyncClientService.
     */
    @Test
    public void testUpdateLocalFileInfo() throws Exception {
        System.out.println("updateLocalFileInfo");
        String userName = "";
        String directory = "";
        String fileName = "";
        FileSyncClientService.updateLocalFileInfo(userName, directory, fileName);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateAllLocalFileInfo method, of class FileSyncClientService.
     */
    @Test
    public void testUpdateAllLocalFileInfo() throws Exception {
        System.out.println("updateAllLocalFileInfo");
        String directory = "./users/nicole";
        FileSyncClientService.updateAllLocalFileInfo(directory);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of getLocalFileInfoByFileName method, of class FileSyncClientService.
     */
    @Test
    public void testGetLocalFileInfoByFileName() throws Exception {
        System.out.println("getLocalFileInfoByFileName");
        String fileName = "";
        FileInfo expResult = null;
        FileInfo result = FileSyncClientService.getLocalFileInfoByFileName(fileName);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isFolderChangeFromServer method, of class FileSyncClientService.
     */
    @Test
    public void testIsFolderChangeFromServer() throws Exception {
        System.out.println("isFolderChangeFromServer");
        String directory = "";
        String fileName = "";
        FileSyncClientService instance = null;
        boolean expResult = false;
        boolean result = instance.isFolderChangeFromServer(directory, fileName);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of syncCreatedFileToServer method, of class FileSyncClientService.
     */
    @Test
    public void testSyncCreatedFileToServer() throws Exception {
        System.out.println("syncCreatedFileToServer");
        String userName = "";
        String directory = "";
        String fileName = "";
        FileSyncClientService instance = null;
        instance.syncCreatedFileToServer(userName, directory, fileName);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of syncModifiedFileToServer method, of class FileSyncClientService.
     */
    @Test
    public void testSyncModifiedFileToServer() throws Exception {
        System.out.println("syncModifiedFileToServer");
        String userName = "";
        String directory = "";
        String fileName = "";
        FileSyncClientService instance = null;
        instance.syncModifiedFileToServer(userName, directory, fileName);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of syncDeletedFileToServer method, of class FileSyncClientService.
     */
    @Test
    public void testSyncDeletedFileToServer() throws Exception {
        System.out.println("syncDeletedFileToServer");
        String userName = "";
        String directory = "";
        String fileName = "";
        FileSyncClientService instance = null;
        instance.syncDeletedFileToServer(userName, directory, fileName);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of syncCreatedFileFromServer method, of class FileSyncClientService.
     */
    @Test
    public void testSyncCreatedFileFromServer() throws Exception {
        System.out.println("syncCreatedFileFromServer");
        String userName = "";
        String directory = "";
        String fileName = "";
        FileSyncClientService.syncCreatedFileFromServer(userName, directory, fileName);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of syncModifiedFileFromServer method, of class FileSyncClientService.
     */
    @Test
    public void testSyncModifiedFileFromServer() throws Exception {
        System.out.println("syncModifiedFileFromServer");
        String userName = "";
        String directory = "";
        String fileName = "";
        FileSyncClientService.syncModifiedFileFromServer(userName, directory, fileName);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of syncDeletedFileFromServer method, of class FileSyncClientService.
     */
    @Test
    public void testSyncDeletedFileFromServer() throws Exception {
        System.out.println("syncDeletedFileFromServer");
        String userName = "";
        String directory = "";
        String fileName = "";
        FileSyncClientService.syncDeletedFileFromServer(userName, directory, fileName);
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
    public void testClearTmpDirs() {
        System.out.println("clearTmpDirs");
        FileSyncClientService instance = null;
        instance.clearTmpDirs();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}