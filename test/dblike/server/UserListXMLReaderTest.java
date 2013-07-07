/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.server;

import dblike.service.MD5Service;
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
public class UserListXMLReaderTest {
    
    public UserListXMLReaderTest() {
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
     * Test of loadUserListFromXML method, of class UserListXMLReader.
     */
    @Test
    public void testLoadUserListFromXML() {
        System.out.println("loadUserListFromXML");
//        boolean expResult = false;
        UserListXMLReader.loadUserListFromXML();
        System.out.println(UserListXMLReader.getValidUserList());
        System.out.println(UserListXMLReader.isValidUser("haijun", MD5Service.getInstance().getMD5FromString("password")));
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of isValidUser method, of class UserListXMLReader.
     */
//    @Test
//    public void testIsValidUser() {
//        System.out.println("isValidUser");
//        String username = "";
//        String password = "";
//        boolean expResult = false;
//        boolean result = UserListXMLReader.isValidUser(username, password);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
}