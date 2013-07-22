/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.client.service;

import dblike.server.service.*;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import dblike.api.ClientAPI;
import dblike.api.ServerAPI;
import dblike.client.ActiveServer;
import dblike.service.FileInfo;
import dblike.service.FileInfoDiff;
import dblike.service.FileInfoService;
import dblike.service.FileSegmentService;
import dblike.service.MD5Service;
import dblike.service.SFTPService;
import dblike.service.ServiceUtils;
import dblike.service.WatchDirectoryService;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;

/**
 * The main class for file and fileInfo synchronization.
 *
 * @author JingboYu
 */
public class FileSyncClientService implements Runnable {

    private final WatchService watchService;
    private final Path directory;
    private static SFTPService sftpService = null;

    /**
     * Constructor.
     *
     * @param directory
     * @throws IOException
     * @throws RemoteException
     * @throws Exception
     */
    public FileSyncClientService(String directory) throws IOException, RemoteException, Exception {

        // set sync directory and register directory watcher
        FileSystem fs = FileSystems.getDefault();
        this.directory = fs.getPath(directory);
        this.watchService = fs.newWatchService();
        this.directory.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE, OVERFLOW);
        System.out.println("Registered watchService on " + directory);

//        updateAllLocalFileInfo(directory);
        FileListXMLService.saveFileListToXML(ClientConfig.getMyFileList());   // for debug
        initSftpService();
    }

    /**
     * Initialize the SFTP connection.
     */
    private static void initSftpService() {
        sftpService = new SFTPService(ClientConfig.getServerList().get(ClientConfig.getCurrentServerIndex()).getServerIP());
    }

    /**
     * Upload the created file to server.
     *
     * @param userName
     * @param directory
     * @param fileName
     * @param activeServer
     * @throws JSchException
     * @throws SftpException
     */
    public synchronized static void uploadCreatedFileToServer(String userName, String directory, String fileName) throws JSchException, SftpException, Exception {
        initSftpService();
        String srcFilePath = ClientConfig.getCurrentClient().getFolderPath() + fileName;
        String dstFilePath = FileInfoService.getSERVER_USERS_FOLDER() + userName + "/" + fileName;
        System.out.println(srcFilePath);
        System.out.println(dstFilePath);
        sftpService.uploadFile(srcFilePath, dstFilePath);
    }

    /**
     * Upload a modified file to server.
     *
     * @param userName
     * @param directory
     * @param fileName
     * @param diff
     * @throws JSchException
     * @throws SftpException
     * @throws Exception
     */
    public synchronized void uploadModifiedFileToServer(String userName, String directory, String fileName, FileInfoDiff diff) throws JSchException, SftpException, Exception {
        uploadCreatedFileToServer(userName, directory, fileName);
    }

    /**
     * Upload a deleted file to server.
     *
     * @param userName
     * @param directory
     * @param fileName
     * @throws JSchException
     * @throws SftpException
     */
    public synchronized void uploadDeletedFileToServer(String userName, String directory, String fileName) throws JSchException, SftpException {
        this.initSftpService();
        String filePath = directory + fileName;
        try {
            sftpService.deleteFile(filePath);
        } catch (Exception e) {
//            System.out.println(e);
        }
    }

    // the following three are for clientAPI methods
    /**
     * Create a local file.
     *
     * @param directory
     * @param fileName
     * @param fileInfo
     * @throws JSchException
     * @throws SftpException
     */
    public synchronized static void createFile(String directory, String fileName, FileInfo fileInfo) throws JSchException, SftpException {
        //download all file segments to ./tmp/download/ and merge to single file
        // for now download the file from server directly
        initSftpService();
        String dstFilePath = ClientConfig.getCurrentClient().getFolderPath() + "/" + fileName;
        String srcFilePath = "./users/" + ClientConfig.getCurrentClient().getClientID() + "/" + fileName;
        sftpService.downloadFile(srcFilePath, dstFilePath);
    }

    /**
     * Modify a local file.
     *
     * @param directory
     * @param fileName
     * @param diff
     * @throws JSchException
     * @throws SftpException
     */
    public synchronized static void modifieFile(String directory, String fileName, FileInfoDiff diff) throws JSchException, SftpException {
        createFile(directory, fileName, diff);
    }

    /**
     * Delete a local file.
     *
     * @param directory
     * @param fileName
     * @throws JSchException
     * @throws SftpException
     */
    public synchronized static void deleteFile(String directory, String fileName) throws JSchException, SftpException {
        //just delete using File.delete()
        File file = new File(ClientConfig.getCurrentClient().getFolderPath() + "/" + fileName);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * Update fileInfo to Server.
     *
     * @param userName
     * @param directory
     * @param fileName
     * @param activeServer
     * @throws RemoteException
     */
    public synchronized static void updateFileInfoToServer(String userName, String directory, String fileName, FileInfo fileInfo) throws RemoteException {
        // get fileinfo from server
        ServerAPI server = ClientConfig.getServerList().get(ClientConfig.getCurrentServerIndex()).getServerAPI();
        server.setFileInfoToServer(ClientConfig.getServerList().get(ClientConfig.getCurrentServerIndex()).getServerIP(),
                ClientConfig.getServerList().get(ClientConfig.getCurrentServerIndex()).getPort(), userName, directory, fileName,
                FileInfoService.fileInfoToXMLString(fileInfo));
    }

    /**
     * Update local fileInfo from server.
     *
     * @param userName
     * @param directory
     * @param fileName
     * @throws RemoteException
     */
    public synchronized static void updateFileInfoFromServer(String userName, String directory, String fileName) throws RemoteException {
        // get fileinfo from server
        System.out.println("Func: updateFileInfoFromServer");
        ServerAPI server = ClientConfig.getServerList().get(ClientConfig.getCurrentServerIndex()).getServerAPI();
        FileInfo newFileInfo = FileInfoService.parseXMLStringToFileInfo(
                server.getFileInfoFromServer(ClientConfig.getServerList().get(ClientConfig.getCurrentServerIndex()).getServerIP(),
                ClientConfig.getServerList().get(ClientConfig.getCurrentServerIndex()).getPort(), userName, directory, fileName));
        ClientConfig.getMyFileList().updateFileInfo(newFileInfo);
        System.out.println(newFileInfo);
    }

    /**
     * Update local fileInfo with a local file.
     *
     * @param userName
     * @param directory
     * @param fileName
     * @throws RemoteException
     * @throws Exception
     */
    public synchronized static void updateLocalFileInfo(String userName, String directory, String fileName) throws RemoteException, Exception {
        ClientConfig.getMyFileList().updateFileInfo(getLocalFileInfoByFileName(fileName));
    }

    /**
     * Sync the local folder and the local FileListService table. This function
     * only runs once right after the client login.
     *
     * @param directory
     */
    public synchronized static void updateAllLocalFileInfo(String directory) throws RemoteException, Exception {
        System.out.println("initial FileList: " + ClientConfig.getMyFileList());
        File dir = new File(directory);
        HashSet<String> curfiles = new HashSet<String>(Arrays.asList(dir.list()));

        // for deleted files
        if (!ClientConfig.getMyFileList().getFileHashTable().isEmpty()) {
            for (String oldfile : ClientConfig.getMyFileList().getFileHashTable().keySet()) {
                if (!curfiles.contains(oldfile)) { // means this file is deleted.
                    ClientConfig.getMyFileList().getFileHashTable().get(oldfile).setFileSize(0);
                    ClientConfig.getMyFileList().getFileHashTable().get(oldfile).setVersion(ClientConfig.getMyFileList().getFileHashTable().get(oldfile).getVersion() + 1);
                    ClientConfig.getMyFileList().getFileHashTable().get(oldfile).setTimestamp(new DateTime().toString());
                    ClientConfig.getMyFileList().getFileHashTable().get(oldfile).getFileHashCode().clear();
                }
            }
        }

        for (String file : curfiles) {
            updateLocalFileInfo(ClientConfig.getCurrentClient().getClientID(), directory, file);
        }

        FileListXMLService.saveFileListToXML(ClientConfig.getMyFileList());
        System.out.println("after scan: " + ClientConfig.getMyFileList());
    }

    /**
     * Get a fileInfo from a local file.
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    public synchronized static FileInfo getLocalFileInfoByFileName(String fileName) throws Exception {
        FileInfo newFileInfo = FileInfoService.getFileInfoByFileName(ClientConfig.getCurrentClient().getFolderPath(), fileName);
        if (ClientConfig.getMyFileList().getFileHashTable().containsKey(fileName)) {
            if (ClientConfig.getMyFileList().getFileHashTable().get(fileName).getFileHashCode().equals(newFileInfo.getFileHashCode())) {
                // the fileinfo and file are already same, do nothing
                newFileInfo = ClientConfig.getMyFileList().getFileHashTable().get(fileName);
            } else {
                // for now, set the version increment by one
                newFileInfo.setVersion(ClientConfig.getMyFileList().getFileHashTable().get(fileName).getVersion() + 1);
                newFileInfo.setDeviceID(ClientConfig.getCurrentClient().getDeviceID());
            }
        } else {
            // this is a newly created file by client
            newFileInfo.setVersion(1);
            newFileInfo.setDeviceID(ClientConfig.getCurrentClient().getDeviceID());
        }
        return newFileInfo;
    }

    /**
     * Check whether the folder change is from server.
     *
     * @param directory
     * @param fileName
     * @return
     * @throws Exception
     */
    public synchronized boolean isFolderChangeFromServer(String directory, String fileName) throws Exception {
        FileInfo newFileInfo = FileInfoService.getFileInfoByFileName(ClientConfig.getCurrentClient().getFolderPath(), fileName);
        if (ClientConfig.getMyFileList().getFileHashTable().containsKey(fileName)
                && ClientConfig.getMyFileList().getFileHashTable().get(fileName).getFileHashCode().equals(newFileInfo.getFileHashCode())) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * Compare to fileInfo from the server.
     *
     * @param userName
     * @param directory
     * @param fileName
     * @return
     * @throws Exception
     */
    public static FileInfoDiff compareToServerFileInfo(String userName, String directory, String fileName) throws Exception {
        ServerAPI server = ClientConfig.getServerList().get(ClientConfig.getCurrentServerIndex()).getServerAPI();
        FileInfo serverFileInfo;

        if (server.containFileInfoFromServer(ClientConfig.getServerList().get(ClientConfig.getCurrentServerIndex()).getServerIP(),
                ClientConfig.getServerList().get(ClientConfig.getCurrentServerIndex()).getPort(), userName, directory, fileName)) {
            serverFileInfo = FileInfoService.parseXMLStringToFileInfo(
                    server.getFileInfoFromServer(ClientConfig.getServerList().get(ClientConfig.getCurrentServerIndex()).getServerIP(),
                    ClientConfig.getServerList().get(ClientConfig.getCurrentServerIndex()).getPort(), userName, directory, fileName));
        } else {
            serverFileInfo = new FileInfo();
            serverFileInfo.setVersion(0);
        }

        FileInfo clientFileInfo = ClientConfig.getMyFileList().getFileInfoByFileName(fileName); // clientFileInfo must exist

        FileInfoDiff diff = clientFileInfo.comparesToFileInfo(serverFileInfo);
        return diff;
    }

    /**
     * Synchronize a created file to server.
     *
     * @param userName
     * @param directory
     * @param fileName
     * @param activeServer
     * @throws RemoteException
     * @throws JSchException
     * @throws SftpException
     */
    public synchronized static void syncCreatedFileToServer(String userName, String directory, String fileName) throws RemoteException, JSchException, SftpException, Exception {
        System.out.println("Func: syncCreatedFileToServer");
        updateLocalFileInfo(userName, directory, fileName);
        System.out.println(ClientConfig.getMyFileList().toString());

        FileInfoDiff diff = compareToServerFileInfo(userName, directory, fileName);
        if (diff.getFlag() == 1) {
            System.out.println("Func: syncCreatedFileToServer flag = 1");
            uploadCreatedFileToServer(userName, directory, fileName);
            updateFileInfoToServer(userName, directory, fileName, ClientConfig.getMyFileList().getFileInfo(fileName));
        }

        if (diff.getFlag() == 3) {
            System.out.println("Func: syncCreatedFileToServer flag = 3");
            Path conflictFile = new File(ClientConfig.getCurrentClient().getFolderPath() + "/" + fileName).toPath();
            Path conflictCopy = new File(ClientConfig.getCurrentClient().getFolderPath() + "/conflicted_copy_from_" + ClientConfig.getCurrentClient().getDeviceID() + "_" + fileName).toPath();
            Files.copy(conflictFile, conflictCopy);
            syncCreatedFileFromServer(userName, directory, fileName);
            updateFileInfoFromServer(userName, directory, fileName);
        }
    }

    /**
     * Synchronize a modified file to server.
     *
     * @param userName
     * @param directory
     * @param fileName
     * @param activeServer
     * @throws RemoteException
     * @throws JSchException
     * @throws SftpException
     */
    public synchronized void syncModifiedFileToServer(String userName, String directory, String fileName) throws RemoteException, JSchException, SftpException, Exception {
        this.syncCreatedFileToServer(userName, directory, fileName);
    }

    /**
     * Synchronize deleted file to server.
     *
     * @param userName
     * @param directory
     * @param fileName
     * @throws RemoteException
     * @throws JSchException
     * @throws SftpException
     * @throws Exception
     */
    public synchronized void syncDeletedFileToServer(String userName, String directory, String fileName) throws RemoteException, JSchException, SftpException, Exception {
        System.out.println("Func: syncDeletedFileToServer");
        this.updateLocalFileInfo(userName, directory, fileName);
        FileInfoDiff diff = compareToServerFileInfo(userName, directory, fileName);
        System.out.println("diff.flag = " + diff.getFlag());
        System.out.println("Local fileInfo -> " + ClientConfig.getMyFileList().getFileInfo(fileName));
        if (diff.getFlag() == 1) {
            this.uploadDeletedFileToServer(userName, directory, fileName);
            this.updateFileInfoToServer(userName, directory, fileName, ClientConfig.getMyFileList().getFileInfo(fileName));
        }

        if (diff.getFlag() == 5) {
            this.syncCreatedFileFromServer(userName, directory, fileName);
            this.updateFileInfoFromServer(userName, directory, fileName);
        }
//        System.out.println(ClientConfig.getMyFileList());
    }

    /**
     * Synchronize a created file from the server.
     *
     * @param userName
     * @param directory
     * @param fileName
     * @throws JSchException
     * @throws RemoteException
     * @throws SftpException
     */
    public synchronized static void syncCreatedFileFromServer(String userName, String directory, String fileName) throws JSchException, RemoteException, SftpException, Exception {
        System.out.println("Func: syncCreatedFileFromServer body1");
        updateFileInfoFromServer(userName, directory, fileName);
        System.out.println("Func: syncCreatedFileFromServer body2");
        createFile(directory, fileName, null);
    }

    /**
     * Synchronize a modified file from server.
     *
     * @param userName
     * @param directory
     * @param fileName
     * @throws JSchException
     * @throws RemoteException
     * @throws SftpException
     */
    public synchronized static void syncModifiedFileFromServer(String userName, String directory, String fileName) throws JSchException, RemoteException, SftpException, Exception {
        System.out.println("Func: syncModifiedFileFromServer");
        syncCreatedFileFromServer(userName, directory, fileName);
    }

    /**
     * Synchronize a deleted file from server.
     *
     * @param userName
     * @param directory
     * @param fileName
     * @throws JSchException
     * @throws RemoteException
     * @throws SftpException
     */
    public synchronized static void syncDeletedFileFromServer(String userName, String directory, String fileName) throws JSchException, RemoteException, SftpException, Exception {
        System.out.println("Func: syncDeletedFileFromServer body1");
        updateFileInfoFromServer(userName, directory, fileName);
        System.out.println("Func: syncDeletedFileFromServer body2");
        deleteFile(directory, fileName);
    }

    /**
     * Watch file service.
     *
     * @throws IOException
     * @throws JSchException
     * @throws RemoteException
     * @throws SftpException
     */
    public void watchFile() throws IOException, JSchException, RemoteException, SftpException, Exception {

        while (true) {

            WatchKey key;
            try {
                key = watchService.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }

            for (WatchEvent<?> e : key.pollEvents()) {

                @SuppressWarnings("unchecked")
                WatchEvent<Path> event = (WatchEvent<Path>) e;

                Path name = event.context();
                Path child = directory.resolve(name);

                String userName = ClientConfig.getCurrentClient().getClientID();
                String directoryName = "./users/" + userName + "/";
                String fileName = child.getName(child.getNameCount() - 1).toString();

                Thread.sleep(1000);

                if (e.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
//                    if (!this.isFolderChangeFromServer(directoryName, fileName)) {
                    this.syncCreatedFileToServer(ClientConfig.getCurrentClient().getClientID(), directoryName, fileName);
//                    }
                    System.out.println("directory: " + directory.getParent() + " file was created: " + fileName);
                } else if (e.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
//                    if (!this.isFolderChangeFromServer(directoryName, fileName)) {
                    this.syncModifiedFileToServer(ClientConfig.getCurrentClient().getClientID(), directoryName, fileName);
//                    }
                    System.out.println("file was modified: " + fileName);
                } else if (e.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
//                    if (!this.isFolderChangeFromServer(directoryName, fileName)) {
                    System.out.println("Deletion comes from local.....");
                    this.syncDeletedFileToServer(ClientConfig.getCurrentClient().getClientID(), directoryName, fileName);
//                    }
                    System.out.println("file was deleted: " + fileName);
                } else if (e.kind() == StandardWatchEventKinds.OVERFLOW) {
                    System.out.println("overflow occurred");
                    continue;
                }

                FileListXMLService.saveFileListToXML(ClientConfig.getMyFileList());

                boolean valid = key.reset();
                if (!valid) {
                    System.out.println("object no longer registered");
                    break;
                }
            }
        }
    }

    /**
     * Run() method.
     */
    @Override
    public void run() {
        try {
            watchFile();
        } catch (IOException ex) {
            Logger.getLogger(FileSyncServerService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSchException ex) {
            Logger.getLogger(FileSyncServerService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SftpException ex) {
            Logger.getLogger(FileSyncServerService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(FileSyncServerService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Clear the tmp directories.
     */
    public void clearTmpDirs() {
        File utmp = new File(FileSegmentService.getTEMP_UPDIR());
        String[] ufiles = utmp.list();
        for (int i = 0; i < ufiles.length; i++) {
            File cur = new File(utmp, ufiles[i]);
            cur.delete();
        }
        File dtmp = new File(FileSegmentService.getTEMP_DOWNDIR());
        String[] dfiles = dtmp.list();
        for (int i = 0; i < dfiles.length; i++) {
            File cur = new File(dtmp, dfiles[i]);
            cur.delete();
        }
    }
}
