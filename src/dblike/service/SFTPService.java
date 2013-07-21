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

    public SFTPService() {
    }

    public SFTPService(String host) {
        this.sftpHost = host;
    }

    public SFTPService(String host, int port) {
        this.sftpHost = host;
        this.sftpPort = port;
    }

    public SFTPService(String host, String privateKey) {
        this.sftpHost = host;
        this.privateKey = privateKey;
    }

    /**
     * Constructor.
     *
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
     * Return a session.
     *
     * @return
     * @throws JSchException
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
     * Upload a file.
     *
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
        //        System.out.println("Channel connected!");

        ChannelSftp sftpChannel = (ChannelSftp) channel;
        sftpChannel.put(sourceFile, destinationFile);
        sftpChannel.exit();
        session.disconnect();
    }

    /**
     * Download a file.
     *
     * @param sourceFile
     * @param destinationFile
     * @throws JSchException
     * @throws SftpException
     */
    public void downloadFile(String sourceFile, String destinationFile) throws JSchException, SftpException {

        Session session = getSession();

        Channel channel = session.openChannel("sftp");
        channel.connect();
        //        System.out.println("Channel connected!");

        ChannelSftp sftpChannel = (ChannelSftp) channel;
        sftpChannel.get(sourceFile, destinationFile);
        sftpChannel.exit();
        session.disconnect();
    }

    /**
     * Delete a file.
     *
     * @param file
     * @throws JSchException
     * @throws SftpException
     */
    public void deleteFile(String file) throws JSchException, SftpException {
        Session session = getSession();

        Channel channel = session.openChannel("sftp");
        channel.connect();
        //        System.out.println("Channel connected!");

        ChannelSftp sftpChannel = (ChannelSftp) channel;
        sftpChannel.rm(file);
        sftpChannel.exit();
        session.disconnect();
    }

    /**
     * Upload a file.
     *
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
        //        System.out.println("Channel connected!");

        ChannelSftp sftpChannel = (ChannelSftp) channel;
        sftpChannel.cd(curRemoteDir);
        sftpChannel.lcd(curLocalDir);
        sftpChannel.put(sourceFile, destinationFile);
        sftpChannel.exit();
        session.disconnect();
    }

    /**
     * Download a file.
     *
     * @param sourceFile
     * @param destinationFile
     * @throws JSchException
     * @throws SftpException
     */
    public void downloadFile(String sourceFile, String destinationFile, String curLocalDir, String curRemoteDir) throws JSchException, SftpException {

        Session session = getSession();

        Channel channel = session.openChannel("sftp");
        channel.connect();
        //        System.out.println("Channel connected!");

        ChannelSftp sftpChannel = (ChannelSftp) channel;
        sftpChannel.cd(curRemoteDir);
        sftpChannel.lcd(curLocalDir);
        sftpChannel.get(sourceFile, destinationFile);
        sftpChannel.exit();
        session.disconnect();
    }

    /**
     * Delete a file.
     *
     * @param file
     * @param curRemoteDir
     * @throws JSchException
     * @throws SftpException
     */
    public void deleteFile(String file, String curRemoteDir) throws JSchException, SftpException {
        Session session = getSession();

        Channel channel = session.openChannel("sftp");
        channel.connect();
        //        System.out.println("Channel connected!");

        ChannelSftp sftpChannel = (ChannelSftp) channel;
        sftpChannel.cd(curRemoteDir);
        //        System.out.println("Del: " + file + " cur: " + curRemoteDir);

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
