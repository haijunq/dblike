/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.service;

import java.util.ArrayList;
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
public class FileSegmentServiceTest {
    
    public FileSegmentServiceTest() {
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
     * Test of splitFileToSegments method, of class FileSegmentService.
     */
    @Test
    public void testSplitFileToSegments() throws Exception {
        System.out.println("splitFileToSegments");
        String dir = "./users/haijun";
        String filename = "doraemon.jpg";
        FileSegmentService.splitFileToSegments(dir, filename);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of mergeByteArrayToSingleFile method, of class FileSegmentService.
     */
    @Test
    public void testMergeByteArrayToSingleFile() throws Exception {
        System.out.println("mergeByteArrayToSingleFile");
        String directory = "./users/haijun";
        String filename = "doraemon.jpg";
//        FileSegmentService.mergeByteArrayToSingleFile(directory, filename);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of insertSegmentsToFile method, of class FileSegmentService.
     */
    @Test
    public void testInsertSegmentsToFile() throws Exception {
        System.out.println("insertSegmentsToFile");
        String directory = "./users/haijun";
        String filename = "doraemon.jpg";
        ArrayList<String> fileChunks = new ArrayList<>();
        fileChunks.add("doraemon.jpg.part.0008");
//        FileSegmentService.insertSegmentsToFile(directory, filename, fileChunks);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of getChunkFromSingleFile method, of class FileSegmentService.
     */
    @Test
    public void testGetChunkFromSingleFile() throws Exception {
        System.out.println("getChunkFromSingleFile");
        String directory = "./users/haijun";
        String filename = "doraemon.jpg";
        String fileChunkName = "doraemon.jpg.part.0009";
        FileSegmentService.getChunkFromSingleFile(directory, filename, fileChunkName);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }
}