/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 *
 * @author haijun
 */
public class FileSegmentService {

    private static final int CHUNK_SIZE = 4096;
    private static final String CURRENT_DIR = ".";

    private static void splitFileToSegments(String filename) throws Exception {
        File file = new File(filename);
        long fileSize = file.length();
        FileInputStream fis = new FileInputStream(file);

        int chunkCount = (int) fileSize / CHUNK_SIZE;
        int lastChunkSize = (int) fileSize % CHUNK_SIZE;

        FileChannel fc = fis.getChannel();
        ByteBuffer bb = ByteBuffer.allocate(CHUNK_SIZE);
        ByteBuffer lastbb = ByteBuffer.allocate(lastChunkSize);

        byte[] bytes;

        for (int i = 0; i < chunkCount; i++) {
            fc.read(bb);
            bb.flip();
            // save the part of the file into a chunk
            bytes = bb.array();
            storeByteArrayToFile(bytes, filename + ".part." + String.format("%04d", i));
            bb.clear();
        }
        fc.read(lastbb);
        lastbb.flip();
        bytes = lastbb.array();
        storeByteArrayToFile(bytes, filename + ".part" + String.format("%04d", chunkCount));
        lastbb.clear();

        fis.close();
    }

    private static void storeByteArrayToFile(byte[] bytesToSave, String path)
            throws Exception {
        FileOutputStream fout = new FileOutputStream(path);
        try {
            fout.write(bytesToSave);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            fout.close();
        }
    }

    private static void mergeByteArrayToSingleFile(final String filename)
            throws Exception {
        File dir = new File(CURRENT_DIR);

        File[] matches = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith(filename + ".part.");
            }
        });

        byte[] bb = new byte[CHUNK_SIZE];
        byte[] lastbb = new byte[(int) matches[matches.length - 1].length()];
        System.out.println(matches[2]);
        System.out.print(lastbb.length);
        byte[] tb = new byte[(int) (CHUNK_SIZE * (matches.length - 1) + matches[matches.length - 1].length())];

        try {
            for (int i = 0; i < matches.length - 1; i++) {
                FileInputStream fin = new FileInputStream(matches[i]);
                fin.read(bb);
                System.arraycopy(bb, 0, tb, i * CHUNK_SIZE, CHUNK_SIZE);
                fin.close();
            }
            FileInputStream fin = new FileInputStream(matches[matches.length - 1]);
            fin.read(lastbb);
            System.arraycopy(lastbb, 0, tb, (matches.length - 1) * CHUNK_SIZE, lastbb.length);
            fin.close();

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        storeByteArrayToFile(tb, filename + ".merge");

    }
}
