/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;

/**
 * This singleton class supplies service of MD5 hashcode generation.
 *
 * @author haijun
 */
public final class MD5Service {

    private static MD5Service md5Service = null;
    private static final int MD5_BLOCK_SIZE = 1024;
    private static final int CHUNK_SIZE = 4096;

    private MD5Service() {
    }

    /**
     * Get an instance object of the MD5 class.
     *
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
     *
     * @param filename should be an valid filename
     * @return checksum of the file
     * @throws Exception "file no found exception"
     */
    public static String getMD5StringFromFile(final String filename) throws Exception {
        byte[] digest = getMD5ByteArrayFromFile(filename);

        //convert the checksum to String
        return convertByteArrayToString(digest);
    }

    private static String convertByteArrayToString(byte[] digest) {
        String checksum = "";
        for (int i = 0; i < digest.length; i++) {
            checksum += Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1);
        }
        return checksum;
    }

    public static Hashtable<String, String> getMD5StringTableFromSingleFile(final String directory, final String filename) throws Exception {
        Hashtable<String, String> fileHashTable = new Hashtable<>();
        File file = new File(directory + "/" + filename);
        long fileSize = file.length();
        FileInputStream fis = new FileInputStream(file);

        int chunkCount = (int) fileSize / CHUNK_SIZE + 1;

        FileChannel fc = fis.getChannel();
        ByteBuffer bb = ByteBuffer.allocate(CHUNK_SIZE);

        MessageDigest complete = MessageDigest.getInstance("MD5");

        String fname = "";
        String hcode = "";

        for (int i = 0; i < chunkCount; i++) {
            fc.read(bb);
            bb.flip();
            complete.update(bb);
            fname = filename + ".part." + String.format("%04d", i);
            hcode = convertByteArrayToString(complete.digest());
            fileHashTable.put(fname, hcode);
            bb.clear();
        }
        fis.close();

        return fileHashTable;
    }

    /**
     * MD5 hash code generator for a file.
     *
     * @param filename should ba an valid filename
     * @return byte array of the checksum
     * @throws Exception "file no found exception
     */
    private static byte[] getMD5ByteArrayFromFile(String filename) throws Exception {
        InputStream fis = new FileInputStream(filename);
        byte[] buffer = new byte[MD5_BLOCK_SIZE];
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

    /**
     *
     * @param password
     * @return
     */
    public static String getMD5FromString(final String password) {
        String md5 = "";
        if (password.isEmpty()) {
            return md5;
        }
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
