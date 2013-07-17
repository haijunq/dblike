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
    private static SFTPService sftpService = new SFTPService(ClientConfig.getServerList().get(ClientConfig.getCurrentServerIndex()).getServerIP(), ClientConfig.getServerList().get(ClientConfig.getCurrentServerIndex()).getPort());

    public FileSyncClientService(String directory) throws IOException {

        // set sync directory and register directory watcher
        FileSystem fs = FileSystems.getDefault();
        this.directory = fs.getPath(directory);
        this.watchService = fs.newWatchService();
        this.directory.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE, OVERFLOW);
        System.out.println("Registered watchService on " + directory);
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
    public void uploadCreatedFileToServer(String userName, String directory, String fileName, ActiveServer activeServer) throws JSchException, SftpException {
        String srcFilePath = ClientConfig.getCurrentClient().getFolderPath() + "/" + fileName;
        String dstFilePath = "./users/" + userName + "/" + fileName;
        sftpService.uploadFile(srcFilePath, dstFilePath);
    }

    public void uploadModifiedFileToServer(String userName, String directory, String fileName, ActiveServer activeServer, FileInfoDiff diff) throws JSchException, SftpException, Exception {
//        SFTPService sftpService = new SFTPService(ClientConfig.getServerList().get(ClientConfig.getCurrentServerIndex()).getServerIP(), ClientConfig.getServerList().get(ClientConfig.getCurrentServerIndex()).getPort());
        // to do, upload only changed slices.
        if (!diff.getFileHashCode().isEmpty()) {
            for (String fileChunkName : diff.getFileHashCode().keySet()) {
                FileSegmentService.getChunkFromSingleFile(ClientConfig.getCurrentClient().getFolderPath(), fileName, fileChunkName);
                String srcFilePath = FileSegmentService.getTEMP_DIR() + "/" + fileChunkName;
                String dstFilePath = "./users/" + userName + "/" + fileChunkName;
                sftpService.uploadFile(srcFilePath, dstFilePath);
            }
            clearTmpDir(); // to be tested
        }
    }

    public void uploadDeletedFileToServer(String userName, String directory, String fileName, ActiveServer activeServer) throws JSchException, SftpException {
//        SFTPService sftpService = new SFTPService(activeServer.getServerIP(), activeServer.getPort());
//        String filePath = "~/" + userName + "/" + fileName;
//        sftpService.deleteFile(filePath);
    }

    // to do
    public void createFile(String directory, String fileName) throws JSchException, SftpException {
//        ClientAPI client = activeClient.getClientAPI();
//        // client upload and download file from server
//        String srcFilePath = "~/" + userName + "/" + fileName;
//        String dstFilePath = "~/" + userName + "/" + fileName;
    }

    // to do
    public void modifieFile(String directory, String fileName, FileInfoDiff diff) throws JSchException, SftpException {
//        ClientAPI client = activeClient.getClientAPI();
//        // client upload and download file from server
//        String srcFilePath = "~/" + userName + "/" + fileName;
//        String dstFilePath = "~/" + userName + "/" + fileName;
    }

    // to do
    public void deleteFile(String directory, String fileName) throws JSchException, SftpException {
//        ClientAPI client = activeClient.getClientAPI();
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
    public void updateFileInfoToServer(String userName, String directory, String fileName, ActiveServer activeServer, FileInfo fileInfo) throws RemoteException {
        // get fileinfo from server
        ServerAPI server = activeServer.getServerAPI();
        server.setFileInfoToServer(activeServer.getServerIP(), activeServer.getPort(), userName, directory, fileName, fileInfo.toString());
    }

    /**
     *
     * @param userName
     * @param directory
     * @param fileName
     * @param activeClient
     * @param fileInfo
     * @throws RemoteException
     * @throws Exception
     */
    public void updateFileInfo(String userName, String directory, String fileName, FileInfo fileInfo) throws RemoteException, Exception {
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
    public void syncCreatedFileWithServer(String userName, String directory, String fileName, ActiveServer activeServer) throws RemoteException, JSchException, SftpException {
//        // get fileinfo from current server
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
    public void syncModifiedFileWithServer(String userName, String directory, String fileName, ActiveServer activeServer) throws RemoteException, JSchException, SftpException {
//        // get fileinfo from current server
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

    public void syncDeletedFileWithServer(String userName, String directory, String fileName, ActiveServer activeServer) throws RemoteException, JSchException, SftpException {
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
    public void syncCreatedFile(String directoryName, String fileName) throws JSchException, RemoteException, SftpException, Exception {
        // get md5 table
        String filePath = directory.toString() + fileName;
        Hashtable<String, String> md5Hashtable = MD5Service.getMD5StringTableFromSingleFile(directory.toString(), filePath);

        // compare md5 with fileinfo
        if (ClientConfig.getMyFileList().getFileInfoByFileName(fileName) == null) // created from own client
        {
            // update local
            // update server
//            syncCreatedFileWithServer(directoryName, fileName, ClientConfig.getServerList().elementAt(ClientConfig.getCurrentServerIndex()));
        }
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
    public void syncModifiedFile(String directory, String fileName) throws JSchException, RemoteException, SftpException, Exception {
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
    public void syncDeletedFile(String directory, String fileName) throws JSchException, RemoteException, SftpException, Exception {
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

                // get userName, directoryName, and fileName
                String directoryName = directory.getName(directory.getNameCount() - 1).toString();
                String fileName = directory.getName(directory.getNameCount()).toString();

                if (e.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                    syncCreatedFile(directoryName, fileName);
                    System.out.println("directory: " + directory.getParent() + " file was created: " + fileName);
                } else if (e.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                    syncModifiedFile(directoryName, fileName);
                    System.out.println("file was modified: " + fileName);
                } else if (e.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                    syncDeletedFile(directoryName, fileName);
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

    public void clearTmpDir() {
        File tmp = new File(FileSegmentService.getTEMP_DIR());
        String[] files = tmp.list();
        for (int i = 0; i < files.length; i++) {
            File cur = new File(tmp, files[i]);
            cur.delete();
        }
    }
}
