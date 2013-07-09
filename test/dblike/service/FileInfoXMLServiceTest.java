/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.service;

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
public class FileInfoXMLServiceTest {
    
    public FileInfoXMLServiceTest() {
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
     * Test of fileInfoToXMLString method, of class FileInfoXMLService.
     */
    @Test
    public void testFileInfoToXMLString() {
        System.out.println("fileInfoToXMLString");
        String username = "haijun";
        FileListService r = FileListXMLService.loadFileListFromXML(username);
        FileInfo fileinfo = r.getFileInfoByFileName("file2");
        String result = FileInfoXMLService.fileInfoToXMLString(fileinfo);
        System.out.println(result);
        FileInfo newfileinfo = FileInfoXMLService.parseXMLStringToFileInfo(result);
        System.out.println(newfileinfo);
    }
}