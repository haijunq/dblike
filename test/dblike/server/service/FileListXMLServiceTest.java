/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.server.service;

import dblike.service.FileInfo;
import java.util.Hashtable;
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
public class FileListXMLServiceTest {
    
    public FileListXMLServiceTest() {
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
     * Test of saveFileListToXML method, of class FileListXMLService.
     */
    @Test
    public void testSaveFileListToXML() {
        System.out.println("saveFileListToXML");
        FileListService filelist = new FileListService("haijun");
        Hashtable<String, String> filehashcode1 = new Hashtable<String, String>();
        filehashcode1.put("file1.part.0000", "1u48901u59431u5890");
        filehashcode1.put("file1.part.0001", "1usafsa6dfds6akljk");
        FileInfo fileinfo1 = new FileInfo(1, "phone", "file1", "12:00am", 3000, filehashcode1);
        
        Hashtable<String, String> filehashcode2 = new Hashtable<String, String>();
        filehashcode2.put("file2.part.0000", "joiqunhjksa6fhkeew");
        filehashcode2.put("file2.part.0001", "0728367gq9yeuwqoye");
        FileInfo fileinfo2 = new FileInfo(2, "pc", "file2", "12:01am", 4000, filehashcode2);
        
        filelist.getFileHashTable().put("file1", fileinfo1);
        filelist.getFileHashTable().put("file2", fileinfo2);
        System.out.println(filelist.getPathname());
        FileListXMLService.saveFileListToXML(filelist);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of loadFileListFromXML method, of class FileListXMLService.
     */
    @Test
    public void testLoadFileListFromXML() {
        System.out.println("loadFileListFromXML");
        String username = "haijun";
//        FileListService expResult = null;
        FileListService r = FileListXMLService.loadFileListFromXML(username);
        System.out.println(r.getPathname());
        System.out.println(r.getFileHashTable().get("file1"));
        System.out.println(r.getFileHashTable().get("file2"));
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }


}