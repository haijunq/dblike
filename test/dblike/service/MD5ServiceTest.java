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
public class MD5ServiceTest {
    
    public MD5ServiceTest() {
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
     * Test of getInstance method, of class MD5Service.
     */
//    @Test
//    public void testGetInstance() {
//        System.out.println("getInstance");
//        MD5Service expResult = null;
//        MD5Service result = MD5Service.getInstance();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of getMD5Checksum method, of class MD5Service.
     */
    @Test
    public void testGetMD5Checksum() throws Exception {
        System.out.println("getMD5Checksum");
        String filename = "doraemon.jpg";
//        String expResult = "";
        String result = MD5Service.getInstance().getMD5StringFromFile(filename);
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
        System.out.println(result);
    }

    /**
     * Test of getInstance method, of class MD5Service.
     */
//    @Test
//    public void testGetInstance() {
//        System.out.println("getInstance");
//        MD5Service expResult = null;
//        MD5Service result = MD5Service.getInstance();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of getMD5HashCode method, of class MD5Service.
     */
    @Test
    public void testGetMD5HashCode() {
        System.out.println("getMD5HashCode");
        String password = "password";
//        String expResult = "";
        String result = MD5Service.getInstance().getMD5FromString(password);
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
        System.out.println(result);
    }

    /**
     * Test of getInstance method, of class MD5Service.
     */
    @Test
    public void testGetInstance() {
        System.out.println("getInstance");
        MD5Service expResult = null;
        MD5Service result = MD5Service.getInstance();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMD5StringFromFile method, of class MD5Service.
     */
    @Test
    public void testGetMD5StringFromFile() throws Exception {
        System.out.println("getMD5StringFromFile");
        String filename = "./users/haijun/doraemon.jpg.part.0000";
//        String expResult = "";
        String result = MD5Service.getMD5StringFromFile(filename);
        System.out.println(result);
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of getMD5StringTableFromSingleFile method, of class MD5Service.
     */
    @Test
    public void testGetMD5StringTableFromSingleFile() throws Exception {
        System.out.println("getMD5StringTableFromSingleFile");
        String directory = "users/haijun";
        String filename = "doraemon.jpg";
//        Hashtable expResult = null;
        Hashtable result = MD5Service.getMD5StringTableFromSingleFile(directory, filename);
        System.out.println(result);
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of getMD5FromString method, of class MD5Service.
     */
    @Test
    public void testGetMD5FromString() {
        System.out.println("getMD5FromString");
        String password = "";
        String expResult = "";
        String result = MD5Service.getMD5FromString(password);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}