/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.service;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 * This singleton class supplies service of MD5 hashcode generation.
 *
 * @author haijun
 */
public final class MD5Service {
    private static MD5Service md5Service = null;
    
    private MD5Service() { 
    }
    
    /**
     * Get an instance object of the MD5 class.
     * @return 
     */
    public MD5Service getInstance() {
        if (md5Service == null) {
            md5Service = new MD5Service(); 
        }
        return md5Service;
    }
    
    /**
     * This method calculate the MD5 hash code from a file. 
     * @param filename should be an valid filename
     * @return checksum of the file
     * @throws Exception "file no found exception"
     */
    private String getMD5Checksum(String filename) throws Exception {
        InputStream fis = new FileInputStream(filename);
        byte [] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance("MD5");
        int numRead;

        //perform the hash calculation for every 1024-byte blocks
        do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
                complete.update(buffer, 0, numRead);
            }
        } while (numRead != -1);
        fis.close();

        //convert the checksum to String
        String checksum = "";
        byte [] digest = complete.digest();        
        for (int i = 0; i < digest.length; i++) {
            checksum += Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1);
        }
        return checksum;
    }
}
