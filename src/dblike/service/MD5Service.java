/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.service;

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This singleton class supplies service of MD5 hashcode generation.
 *
 * @author haijun
 */
public final class MD5Service {
    private static MD5Service md5Service = null;
    private static final int MD5_BLOCK_SIZE = 1024;
    
    private MD5Service() { 
    }
    
    /**
     * Get an instance object of the MD5 class.
     * @return 
     */
    public static MD5Service getInstance() {
        if (md5Service == null) {
            md5Service = new MD5Service(); 
        }
        return md5Service;
    }
    
    /**
     * This method calls the MD5 hash code generator and converts to a string. 
     * @param filename should be an valid filename
     * @return checksum of the file
     * @throws Exception "file no found exception"
     */
    public static String getMD5Checksum(final String filename) throws Exception {
        byte [] digest = getMD5HashByte(filename);

        //convert the checksum to String
        String checksum = "";
        for (int i = 0; i < digest.length; i++) {
            checksum += Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1);
        }
        return checksum;
    }
    
    /**
     * MD5 hash code generator for a file.
     * @param filename should ba an valid filename
     * @return byte array of the checksum
     * @throws Exception "file no found exception
     */
    private static byte [] getMD5HashByte(String filename) throws Exception {
        InputStream fis = new FileInputStream(filename);
        byte [] buffer = new byte[MD5_BLOCK_SIZE];
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
        return complete.digest();
    }
    
    public static String getMD5HashCode(final String password) {
        String md5 = "";
        if (password.isEmpty())
            return md5; 
        try {
            MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(password.getBytes(), 0, password.length());
            md5 = new BigInteger(1, mDigest.digest()).toString(16); 
        } catch (Exception ex) {
            ex.printStackTrace();
        }        
        return md5;
    }
}
