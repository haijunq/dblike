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
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author JingboYu
 */
public class FileSyncClientService implements Runnable {

    private final WatchService watchService;
    private final Path directory;
    private static SFTPService sftpService = null;

    public FileSyncClientService(String directory) throws IOException, RemoteException, Exception {

        // set sync directory and register directory watcher
        FileSystem fs = FileSystems.getDefault();
        this.directory = fs.getPath(directory);
        this.watchService = fs.newWatchService();
        this.directory.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE, OVERFLOW);
        System.out.println("Registered watchService on " + directory);

//        updateAllLocalFileInfo(directory);
        initSftpService();
    }

    // how to guarantee the connection in case of failure
    private static void initSftpService() {
        while (sftpService == null) {
            sftpService = new SFTPService(ClientConfig.getServerList().get(ClientConfig.getCurrentServerIndex()).getServerIP());
        }
    }

    /**
     *
     * @param userName
     * @param directory
     * @param fileName
     * @param activeServer
     * @throws JSchException
     * @throws SftpException
     */
    public synchronized void uploadCreatedFileToServer(String userName, String directory, String fileName) throws JSchException, SftpException, Exception {
        this.initSftpService();
        String srcFilePath = ClientConfig.getCurrentClient().getFolderPath() + fileName;
        String dstFilePath = FileInfoService.getSERVER_USERS_FOLDER() + userName + "/" + fileName;
        sftpService.uploadFile(srcFilePath, dstFilePath);
    }

    public synchronized void uploadModifiedFileToServer(String userName, String directory, String fileName, FileInfoDiff diff) throws JSchException, SftpException, Exception {
//        SFTPService sftpService = new SFTPService(ClientConfig.getServerList().get(ClientConfig.getCurrentServerIndex()).getServerIP(), ClientConfig.getServerList().get(ClientConfig.getCurrentServerIndex()).getPort());
        // upload only changed slices. 
        // to do should also include the deleted slices.
//        if (!diff.getFileHashCode().isEmpty()) {
//            for (String fileChunkName : diff.getFileHashCode().keySet()) {
//                FileSegmentService.getChunkFromSingleFile(ClientConfig.getCurrentClient().getFolderPath(), fileName, fileChunkName);
//                String srcFilePath = FileSegmentService.getTEMP_UPDIR() + "/" + fileChunkName;
//                String dstFilePath = "./users/" + userName + "/" + fileChunkName;
//                sftpService.uploadFile(srcFilePath, dstFilePath);
//            }
//            clearTmpDirs(); // to be tested
//        }
        uploadCreatedFileToServer(userName, directory, fileName);
    }

    public synchronized void uploadDeletedFileToServer(String userName, String directory, String fileName) throws JSchException, SftpException {
//        SFTPService sftpService = new SFTPService(activeServer.getServerIP(), activeServer.getPort());
//        if (serverToDeleteFileInfo.getFileHashCode().isEmpty()) {
//            return;
//        }
        this.initSftpService();
        String filePath = directory + fileName;
        sftpService.deleteFile(filePath);

//        for (String fileChunkName : serverToDeleteFileInfo.getFileHashCode().keySet()) {
//            String filePath = "./users/" + userName + "/" + fileName;
//            sftpService.deleteFile(filePath);
//        }
    }

    // the following three are for clientAPI methods
    public synchronized static void createFile(String directory, String fileName, FileInfo fileInfo) throws JSchException, SftpException {
        //download all file segments to ./tmp/download/ and merge to single file
        // for now download the file from server directly
        initSftpService();
        String dstFilePath = ClientConfig.getCurrentClient().getFolderPath() + "/" + fileName;
        String srcFilePath = "./users/" + ClientConfig.getCurrentClient().getClientID() + "/" + fileName;
        sftpService.downloadFile(srcFilePath, dstFilePath);
    }

    // to do
    public synchronized static void modifieFile(String directory, String fileName, FileInfoDiff diff) throws JSchException, SftpException {
        createFile(directory, fileName, diff);
        //download updated file segments to ./tmp/download/ and insert to single file
    }

    // to do
    public synchronized static void deleteFile(String directory, String fileName) throws JSchException, SftpException {
        //just delete using File.delete()
        File file = new File(ClientConfig.getCurrentClient().getFolderPath() + "/" + fileName);
        if (file.exists()) {
            file.delete();
        }
//        ClientAPI client = activeClient.getClientAPI()
//        // client upload and download file from server
//        String srcFilePath = "~/" + userName + "/" + fileName;
//        String dstFilePath = "~/" + userName + "/" + fileName;
    }

    /**
     *
     * @param userName
     * @param directory
     * @param fileName
     * @param activeServer
     * @throws RemoteException
     */
    public synchronized void updateFileInfoToServer(String userName, String directory, String fileName, FileInfo fileInfo) throws RemoteException {
        // get fileinfo from server
        ServerAPI server = ClientConfig.getServerList().get(ClientConfig.getCurrentServerIndex()).getServerAPI();
        server.printMsg();
        System.out.println(FileInfoService.fileInfoToXMLString(fileInfo));
//        server.setFileInfoToServer(ClientConfig.getServerList().get(ClientConfig.getCurrentServerIndex()).getServerIP(),
//                ClientConfig.getServerList().get(ClientConfig.getCurrentServerIndex()).getPort(), userName, directory, fileName,
//                FileInfoService.fileInfoToXMLString(fileInfo));
    }

    /**
     *
     * @param userName
     * @param directory
     * @param fileName
     * @throws RemoteException
     */
    public synchronized static void updateFileInfoFromServer(String userName, String directory, String fileName) throws RemoteException {
        // get fileinfo from server
        ServerAPI server = ClientConfig.getServerList().get(ClientConfig.getCurrentServerIndex()).getServerAPI();
        FileInfo newFileInfo = FileInfoService.parseXMLStringToFileInfo(
                server.getFileInfoFromServer(ClientConfig.getServerList().get(ClientConfig.getCurrentServerIndex()).getServerIP(),
                ClientConfig.getServerList().get(ClientConfig.getCurrentServerIndex()).getPort(), userName, directory, fileName));
        ClientConfig.getMyFileList().updateFileInfo(newFileInfo);
    }

    /**
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
     * Sync the local folder and the local FileListService table. This function only runs once right after the client login.
     * @param directory 
     */
    public synchronized static void updateAllLocalFileInfo(String directory) throws RemoteException, Exception {
        File dir = new File(directory);
        String [] files = dir.list();
        for (String file : files) {
//            System.out.println(file);
//            System.out.println(getLocalFileInfoByFileName(file));
            updateLocalFileInfo(ClientConfig.getCurrentClient().getClientID(), directory, file);
        }
        System.out.println(ClientConfig.getMyFileList());
    }
    /**
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
                System.out.println("here");
                newFileInfo = ClientConfig.getMyFileList().getFileHashTable().get(fileName);
            } else {
                // for now, set the version increment by one
                newFileInfo.setVersion(ClientConfig.getMyFileList().getFileHashTable().get(fileName).getVersion() + 1);
                newFileInfo.setDeviceID(ClientConfig.getCurrentClient().getDeviceID());
            }
        } else {
            // this is a newly created file by client
            newFileInfo.setVersion(0);
            newFileInfo.setDeviceID(ClientConfig.getCurrentClient().getDeviceID());
        }
        return newFileInfo;
    }
    
    /**
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
     *
     * @param userName
     * @param directory
     * @param fileName
     * @param activeServer
     * @throws RemoteException
     * @throws JSchException
     * @throws SftpException
     */
    public synchronized void syncCreatedFileToServer(String userName, String directory, String fileName) throws RemoteException, JSchException, SftpException, Exception {
        this.updateLocalFileInfo(userName, directory, fileName);
        this.uploadCreatedFileToServer(userName, directory, fileName);
        this.updateFileInfoToServer(userName, directory, fileName, ClientConfig.getMyFileList().getFileInfo(fileName));
        System.out.println(ClientConfig.getMyFileList());

        //        FileInfo fileInfo = fileListHashtable.get(userName).getFileInfoByFileName(fileName);
//        
//        // get fileinfo from server
//        ServerAPI server = activeServer.getServerAPI();
//        String fileInfoStr = server.getFileInfoFromServer(activeServer.getServerIP(), activeServer.getPort(),
//                userName, directory, fileName);
//        if (fileInfoStr.isEmpty())
//        {
//            uploadCreatedFileToServer(userName, directory, fileName, activeServer);
//            updateFileInfoToServer(userName, directory, fileName, activeServer, fileInfo);
//        }
    }

    /**
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

        // get fileinfo from current server
//        FileInfo fileInfo = fileListHashtable.get(userName).getFileInfoByFileName(fileName);
//        
//        // get fileinfo from server
//        ServerAPI server = activeServer.getServerAPI();
//        String fileInfoStr = server.getFileInfoFromServer(activeServer.getServerIP(), activeServer.getPort(),
//                userName, directory, fileName);
//        FileInfoDiff diff = fileInfo.comparesToFileInfo(FileInfoService.parseXMLStringToFileInfo(fileInfoStr));
//        
//        if (diff.getFlag() == 1)
//        {
//            uploadModifiedFileToServer(userName, directory, fileName, activeServer, diff);
//            updateFileInfoToServer(userName, directory, fileName, activeServer, fileInfo);
//        }
    }

    public synchronized void syncDeletedFileToServer(String userName, String directory, String fileName) throws RemoteException, JSchException, SftpException, Exception {
        this.updateLocalFileInfo(userName, directory, fileName);
        System.out.println(ClientConfig.getMyFileList().getFileInfo(fileName));
        this.uploadDeletedFileToServer(userName, directory, fileName);
        this.updateFileInfoToServer(userName, directory, fileName, ClientConfig.getMyFileList().getFileInfo(fileName));
        System.out.println(ClientConfig.getMyFileList());

//        // get fileinfo from current server
//        FileInfo fileInfo = fileListHashtable.get(userName).getFileInfoByFileName(fileName);
//        
//        // get fileinfo from server
//        ServerAPI server = activeServer.getServerAPI();
//        String fileInfoStr = server.getFileInfoFromServer(activeServer.getServerIP(), activeServer.getPort(),
//                userName, directory, fileName);
//        
//        if (!fileInfoStr.isEmpty())
//        {
//            uploadDeletedFileToServer(userName, directory, fileName, activeServer);
//            updateFileInfoToServer(userName, directory, fileName, activeServer, fileInfo);
//        }
    }

    /**
     *
     * @param userName
     * @param directory
     * @param fileName
     * @throws JSchException
     * @throws RemoteException
     * @throws SftpException
     */
    public synchronized static void syncCreatedFileFromServer(String userName, String directory, String fileName) throws JSchException, RemoteException, SftpException, Exception {
        updateFileInfoFromServer(userName, directory, fileName);
        createFile(directory, fileName, null);

        // get md5 table
//        String filePath = directory.toString() + fileName;
//        Hashtable<String, String> md5Hashtable = MD5Service.getMD5StringTableFromSingleFile(directory.toString(), filePath);
//
//        // compare md5 with fileinfo
//        if (ClientConfig.getMyFileList().getFileInfoByFileName(fileName) == null) // created from own client
//        {
//            // update local
//            // update server
////            syncCreatedFileToServer(directoryName, fileName, ClientConfig.getServerList().elementAt(ClientConfig.getCurrentServerIndex()));
//        }
    }

    /**
     *
     * @param userName
     * @param directory
     * @param fileName
     * @throws JSchException
     * @throws RemoteException
     * @throws SftpException
     */
    public synchronized static void syncModifiedFileFromServer(String userName, String directory, String fileName) throws JSchException, RemoteException, SftpException, Exception {
        syncCreatedFileFromServer(userName, directory, fileName);
    }

    /**
     *
     * @param userName
     * @param directory
     * @param fileName
     * @throws JSchException
     * @throws RemoteException
     * @throws SftpException
     */
    public synchronized static void syncDeletedFileFromServer(String userName, String directory, String fileName) throws JSchException, RemoteException, SftpException, Exception {
        updateFileInfoFromServer(userName, directory, fileName);
        deleteFile(directory, fileName);
    }

    /**
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

//                // get userName, directoryName, and fileName
//                String directoryName = directory.getName(directory.getNameCount() - 2).toString();
//                String fileName = directory.getName(directory.getNameCount() - 1).toString();
//                System.out.println(directory.getFileName());

                Path name = event.context();
                Path child = directory.resolve(name);

                String directoryName = FileInfoService.getSERVER_USERS_FOLDER() + ClientConfig.getCurrentClient().getClientID() + "/";
                String fileName = child.getName(child.getNameCount() - 1).toString();

                if (e.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                    if (!this.isFolderChangeFromServer(directoryName, fileName)) {
                        this.syncCreatedFileToServer(ClientConfig.getCurrentClient().getClientID(), directoryName, fileName);
                    }
                    System.out.println("directory: " + directory.getParent() + " file was created: " + fileName);
                } else if (e.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                    if (!this.isFolderChangeFromServer(directoryName, fileName)) {
                        this.syncModifiedFileToServer(ClientConfig.getCurrentClient().getClientID(), directoryName, fileName);
                    }
                    System.out.println("file was modified: " + fileName);
                } else if (e.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                    if (!this.isFolderChangeFromServer(directoryName, fileName)) {
                        this.syncDeletedFileToServer(ClientConfig.getCurrentClient().getClientID(), directoryName, fileName);
                    }
                    System.out.println("file was deleted: " + fileName);
                } else if (e.kind() == StandardWatchEventKinds.OVERFLOW) {
                    System.out.println("overflow occurred");
                    continue;
                }

                boolean valid = key.reset();
                if (!valid) {
                    System.out.println("object no longer registered");
                    break;
                }
            }
        }
    }

    /**
     *
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
