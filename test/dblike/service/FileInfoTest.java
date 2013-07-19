/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.service;

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
public class FileInfoTest {
    
    public FileInfoTest() {
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
     * Test of getVersion method, of class FileInfo.
     */
    @Test
    public void testGetVersion() {
        System.out.println("getVersion");
        FileInfo instance = new FileInfo();
        int expResult = 0;
        int result = instance.getVersion();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setVersion method, of class FileInfo.
     */
    @Test
    public void testSetVersion() {
        System.out.println("setVersion");
        int version = 0;
        FileInfo instance = new FileInfo();
        instance.setVersion(version);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDeviceID method, of class FileInfo.
     */
    @Test
    public void testGetDeviceID() {
        System.out.println("getDeviceID");
        FileInfo instance = new FileInfo();
        String expResult = "";
        String result = instance.getDeviceID();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setDeviceID method, of class FileInfo.
     */
    @Test
    public void testSetDeviceID() {
        System.out.println("setDeviceID");
        String deviceID = "";
        FileInfo instance = new FileInfo();
        instance.setDeviceID(deviceID);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFileName method, of class FileInfo.
     */
    @Test
    public void testGetFileName() {
        System.out.println("getFileName");
        FileInfo instance = new FileInfo();
        String expResult = "";
        String result = instance.getFileName();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setFileName method, of class FileInfo.
     */
    @Test
    public void testSetFileName() {
        System.out.println("setFileName");
        String fileName = "";
        FileInfo instance = new FileInfo();
        instance.setFileName(fileName);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTimestamp method, of class FileInfo.
     */
    @Test
    public void testGetTimestamp() {
        System.out.println("getTimestamp");
        FileInfo instance = new FileInfo();
        String expResult = "";
        String result = instance.getTimestamp();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setTimestamp method, of class FileInfo.
     */
    @Test
    public void testSetTimestamp() {
        System.out.println("setTimestamp");
        String timestamp = "";
        FileInfo instance = new FileInfo();
        instance.setTimestamp(timestamp);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFileSize method, of class FileInfo.
     */
    @Test
    public void testGetFileSize() {
        System.out.println("getFileSize");
        FileInfo instance = new FileInfo();
        long expResult = 0L;
        long result = instance.getFileSize();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setFileSize method, of class FileInfo.
     */
    @Test
    public void testSetFileSize() {
        System.out.println("setFileSize");
        long fileSize = 0L;
        FileInfo instance = new FileInfo();
        instance.setFileSize(fileSize);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFileHashCode method, of class FileInfo.
     */
    @Test
    public void testGetFileHashCode() {
        System.out.println("getFileHashCode");
        FileInfo instance = new FileInfo();
        Hashtable expResult = null;
        Hashtable result = instance.getFileHashCode();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setFileHashCode method, of class FileInfo.
     */
    @Test
    public void testSetFileHashCode() {
        System.out.println("setFileHashCode");
        Hashtable<String, String> fileHashCode = null;
        FileInfo instance = new FileInfo();
        instance.setFileHashCode(fileHashCode);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isTimestampNewer method, of class FileInfo.
     */
    @Test
    public void testIsTimestampNewer() {
        System.out.println("isTimestampNewer");
        String timestamp = "";
        FileInfo instance = new FileInfo();
        boolean expResult = false;
        boolean result = instance.isTimestampNewer(timestamp);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isVersionNewer method, of class FileInfo.
     */
    @Test
    public void testIsVersionNewer() {
        System.out.println("isVersionNewer");
        int version = 0;
        FileInfo instance = new FileInfo();
        int expResult = 0;
        int result = instance.isVersionNewer(version);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isSameDevice method, of class FileInfo.
     */
    @Test
    public void testIsSameDevice() {
        System.out.println("isSameDevice");
        String deviceID = "";
        FileInfo instance = new FileInfo();
        boolean expResult = false;
        boolean result = instance.isSameDevice(deviceID);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class FileInfo.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        FileInfo instance = new FileInfo();
        String expResult = "";
        String result = instance.toString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of comparesToFileInfo method, of class FileInfo.
     */
    @Test
    public void testComparesToFileInfo() {
        System.out.println("comparesToFileInfo");
        String xml1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><fileInfo><version>3</version><deviceID>pc</deviceID><fileName>file2</fileName><timestamp>120</timestamp><fileSize>4000</fileSize><fileHashCode><fileChunkHashCode fileChunkName=\"file2.part.0001\">0728367gq9yeuwqoye</fileChunkHashCode><fileChunkHashCode fileChunkName=\"file2.part.0000\">joiqunhjksa6fhkeew</fileChunkHashCode></fileHashCode></fileInfo>\n";
        FileInfo fthis = FileInfoService.parseXMLStringToFileInfo(xml1);
        String xml2 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><fileInfo><version>4</version><deviceID>pc</deviceID><fileName>file2</fileName><timestamp>101</timestamp><fileSize>4000</fileSize><fileHashCode><fileChunkHashCode fileChunkName=\"file2.part.0001\">0728367gq9yeuwqoye</fileChunkHashCode><fileChunkHashCode fileChunkName=\"file2.part.0000\">joiqunhqerq6fhkeew</fileChunkHashCode></fileHashCode></fileInfo>\n";
        FileInfo fthat = FileInfoService.parseXMLStringToFileInfo(xml2);
        
        FileInfoDiff diff = fthis.comparesToFileInfo(fthat);
        System.out.println(diff);
    }
}