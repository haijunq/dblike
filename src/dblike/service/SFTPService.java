/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.service;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import java.io.File;
import java.util.Vector;

/**
 * SFTP service class.
 *
 * @author JingboYu
 */
public class SFTPService {

    private String sftpHost = ServiceUtils.SFTP_HOST;
    private int sftpPort = ServiceUtils.SFTP_PORT;
    private String userName = ServiceUtils.SFTP_USERNAME;
    private String pwd = ServiceUtils.SFTP_PASSWORD;
    private String privateKey = ServiceUtils.SFTP_PRIVATE_KEY;

    /**
     * Constructor.
     */
    public SFTPService() {
    }

    /**
     * Constructor.
     * @param host 
     */
    public SFTPService(String host) {
        this.sftpHost = host;
    }

    /**
     * Constructor.
     * @param host
     * @param port 
     */
    public SFTPService(String host, int port) {
        this.sftpHost = host;
        this.sftpPort = port;
    }

    /**
     * Constructor.
     * @param host
     * @param privateKey 
     */
    public SFTPService(String host, String privateKey) {
        this.sftpHost = host;
        this.privateKey = privateKey;
    }

    /**
     * Constructor. 
     * @param sftpHost
     * @param sftpPort
     * @param userName
     * @param pwd
     * @param privateKey
     */
    public SFTPService(String sftpHost, int sftpPort, String userName, String pwd, String privateKey) {

        this.sftpHost = sftpHost;
        this.sftpPort = sftpPort;
        this.userName = userName;
        this.pwd = pwd;
        this.privateKey = privateKey;

    }

    /**
     *
     * @return @throws JSchException
     */
    private Session getSession() throws JSchException {

        JSch jsch = new JSch();
        jsch.addIdentity(privateKey);
        Session session = null;
        session = jsch.getSession(userName, sftpHost, sftpPort);
        session.setConfig("StrictHostKeyChecking", "no");
        session.setPassword(pwd);
        session.connect();
        return session;
    }

    /**
     * Upload file service. 
     * @param sourceFile
     * @param destinationFile
     * @return
     * @throws JSchException
     * @throws SftpException
     */
    public void uploadFile(String sourceFile, String destinationFile) throws JSchException, SftpException {

        Session session = getSession();

        Channel channel = session.openChannel("sftp");
        channel.connect();
        // System.out.println("Channel connected!");

        ChannelSftp sftpChannel = (ChannelSftp) channel;
        sftpChannel.put(sourceFile, destinationFile);
        sftpChannel.exit();
        session.disconnect();
    }

    /**
     * Download file service.
     * @param sourceFile
     * @param destinationFile
     * @throws JSchException
     * @throws SftpException
     */
    public void downloadFile(String sourceFile, String destinationFile) throws JSchException, SftpException {

        Session session = getSession();

        Channel channel = session.openChannel("sftp");
        channel.connect();
        // System.out.println("Channel connected!");

        ChannelSftp sftpChannel = (ChannelSftp) channel;
        sftpChannel.get(sourceFile, destinationFile);
        sftpChannel.exit();
        session.disconnect();
    }

    /**
     * Delete file service.
     * @param file
     * @throws JSchException
     * @throws SftpException 
     */
    public void deleteFile(String file) throws JSchException, SftpException {
        Session session = getSession();

        Channel channel = session.openChannel("sftp");
        channel.connect();
        // System.out.println("Channel connected!");

        ChannelSftp sftpChannel = (ChannelSftp) channel;
        sftpChannel.rm(file);
        sftpChannel.exit();
        session.disconnect();
    }

    /**
     * Upload file service.
     * @param sourceFile
     * @param destinationFile
     * @return
     * @throws JSchException
     * @throws SftpException
     */
    public void uploadFile(String sourceFile, String destinationFile, String curLocalDir, String curRemoteDir) throws JSchException, SftpException {

        Session session = getSession();

        Channel channel = session.openChannel("sftp");
        channel.connect();
        // System.out.println("Channel connected!");

        ChannelSftp sftpChannel = (ChannelSftp) channel;
        sftpChannel.cd(curRemoteDir);
        sftpChannel.lcd(curLocalDir);
        sftpChannel.put(sourceFile, destinationFile);
        sftpChannel.exit();
        session.disconnect();
    }

    /**
     * Download file service.
     * @param sourceFile
     * @param destinationFile
     * @throws JSchException
     * @throws SftpException
     */
    public void downloadFile(String sourceFile, String destinationFile, String curLocalDir, String curRemoteDir) throws JSchException, SftpException {

        Session session = getSession();

        Channel channel = session.openChannel("sftp");
        channel.connect();
        // System.out.println("Channel connected!");

        ChannelSftp sftpChannel = (ChannelSftp) channel;
        sftpChannel.cd(curRemoteDir);
        sftpChannel.lcd(curLocalDir);
        sftpChannel.get(sourceFile, destinationFile);
        sftpChannel.exit();
        session.disconnect();
    }

    /**
     * Download a whole directory service. 
     * @param sourceDir
     * @param destinationDir
     * @param curLocalDir
     * @param curRemoteDir
     * @throws JSchException
     * @throws SftpException 
     */
    public void downloadDirectory(String sourceDir, String destinationDir, String curLocalDir, String curRemoteDir) throws JSchException, SftpException {

        Session session = getSession();

        Channel channel = session.openChannel("sftp");
        channel.connect();
        // System.out.println("Channel connected!");

        ChannelSftp sftpChannel = (ChannelSftp) channel;

        System.out.println("mkdir: " + destinationDir);
        (new File(destinationDir)).mkdirs();

        sftpChannel.cd(curRemoteDir);
        sftpChannel.lcd(curLocalDir);


        Vector<ChannelSftp.LsEntry> list = sftpChannel.ls(sourceDir);
        for (ChannelSftp.LsEntry entry : list) {
            String fileName = entry.getFilename();
            SftpATTRS attr = entry.getAttrs();
            // System.out.println("file name: " + fileName + " is dir: " + attr.isDir());
            if (attr.isDir()) {
                if (fileName.equals(".") || fileName.equals("..")) {
                    continue;
                }
                // System.out.println("mkdir: " + sourceDir + "/" + fileName);
                (new File(destinationDir + "/" + fileName)).mkdirs();
                Vector<ChannelSftp.LsEntry> subList = sftpChannel.ls(sourceDir + "/" + fileName);
                for (ChannelSftp.LsEntry subEntry : subList) {
                    String subFileName = subEntry.getFilename();
                    SftpATTRS subAttr = subEntry.getAttrs();
                    // System.out.println("file name: " + subFileName + " is dir: " + subAttr.isDir());
                    if (subAttr.isDir()) {
                        continue;
                    }
                    String srcFile = sourceDir + "/" + fileName + "/" + subFileName;
                    String dstFile = destinationDir + "/" + fileName + "/" + subFileName;
                    sftpChannel.get(srcFile, dstFile);
                }
            } else {
                sftpChannel.get(sourceDir + "/" + fileName, destinationDir + "/" + fileName);
            }
        }

        sftpChannel.exit();
        session.disconnect();
    }

    /**
     * Delete a remote file service. 
     * @param file
     * @param curRemoteDir
     * @throws JSchException
     * @throws SftpException 
     */
    public void deleteFile(String file, String curRemoteDir) throws JSchException, SftpException {
        Session session = getSession();

        Channel channel = session.openChannel("sftp");
        channel.connect();
        // System.out.println("Channel connected!");

        ChannelSftp sftpChannel = (ChannelSftp) channel;
        sftpChannel.cd(curRemoteDir);
        // System.out.println("Del: " + file + " cur: " + curRemoteDir);

        Boolean fileExists = true;
        SftpATTRS sftpATTRS;
        try {
            sftpATTRS = sftpChannel.lstat(file);
        } catch (Exception ex) {
            fileExists = false;
        }

        if (fileExists) {
            sftpChannel.rm(file);
        }
        sftpChannel.exit();
        session.disconnect();
    }
}