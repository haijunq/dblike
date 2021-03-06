/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.service;

import dblike.client.service.FileSyncClientService;
import dblike.server.service.FileListService;
import dblike.server.service.FileListXMLService;
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
public class FileInfoServiceTest {
    
    public FileInfoServiceTest() {
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
     * Test of fileInfoToXMLString method, of class FileInfoService.
     */
    @Test
    public void testFileInfoToXMLString() throws Exception {
        System.out.println("fileInfoToXMLString");
//        String username = "haijun";
//        FileListService r = FileListXMLService.loadFileListFromXML(username);
//        FileInfo fileinfo = r.getFileInfoByFileName("file2");
        FileInfo fileinfo = FileSyncClientService.getLocalFileInfoByFileName("yes");
        fileinfo.setDeviceID("one");
        System.out.println(fileinfo);
        String result = FileInfoService.fileInfoToXMLString(fileinfo);
        System.out.println(result);
        FileInfo newfileinfo = FileInfoService.parseXMLStringToFileInfo(result);
        System.out.println(newfileinfo);
    }

    /**
     * Test of parseXMLStringToFileInfo method, of class FileInfoService.
     */
    @Test
    public void testParseXMLStringToFileInfo() {
        System.out.println("parseXMLStringToFileInfo");
        String xmlstring = "";
        FileInfo expResult = null;
        FileInfo result = FileInfoService.parseXMLStringToFileInfo(xmlstring);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFileInfoByFileName method, of class FileInfoService.
     */
    @Test
    public void testGetFileInfoByFileName() throws Exception {
        System.out.println("getFileInfoByFileName");
        String directory = "./users/haijun";
        String filename = "filelist.xmsl";
//        FileInfo expResult = null;
        FileInfo result = FileInfoService.getFileInfoByFileName(directory, filename);
        System.out.println(result);
        //        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of getAbsolutePathName method, of class FileInfoService.
     */
    @Test
    public void testGetAbsolutePathName() throws Exception {
        System.out.println("getAbsolutePathName");
        String pathName = "./users/haijun";
//        String expResult = "";
        pathName = FileInfoService.getAbsolutePathName(pathName);
        System.out.println(pathName);
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }
}