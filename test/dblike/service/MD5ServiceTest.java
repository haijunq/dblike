/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.service;

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
        String result = MD5Service.getInstance().getMD5Checksum(filename);
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
        String password = "helloworld";
//        String expResult = "";
        String result = MD5Service.getInstance().getMD5HashCode(password);
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
        System.out.println(result);
    }
}