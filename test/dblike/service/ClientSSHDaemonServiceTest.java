/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.service;

import org.apache.sshd.SshServer;
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
public class ClientSSHDaemonServiceTest {
    
    public ClientSSHDaemonServiceTest() {
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
     * Test of startSSHServer method, of class ClientSSHDaemonService.
     */
    @Test
    public void testStartSSHServer() {
        System.out.println("startSSHServer");
        ClientSSHDaemonService.startSSHServer();
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }
}