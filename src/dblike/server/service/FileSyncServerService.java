/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.server.service;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import dblike.api.ClientAPI;
import dblike.api.ServerAPI;
import dblike.server.ActiveClient;
import dblike.server.ActiveServer;
import dblike.server.ServerStart;
import dblike.service.FileInfo;
import dblike.service.FileInfoDiff;
import dblike.service.FileInfoService;
import dblike.service.SFTPService;
import dblike.service.WatchDirectoryService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import java.nio.file.Path;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class handles the file synchronization on the server side
 *
 * @author JingboYu
 */
public class FileSyncServerService extends WatchDirectoryService implements Runnable {

    public static Hashtable<String, FileListService> fileListHashtable; // file info of all users in the memory

    public FileSyncServerService(Path dir, boolean recursive) throws IOException, RemoteException, JSchException, SftpException, Exception {

        super(dir, recursive);

        // load a list of users
        Hashtable<String, String> userListHashtable = UserListXMLReader.getValidUserList();
        System.out.println("Load " + userListHashtable.size() + " users");
        for (Map.Entry<String, String> entry : userListHashtable.entrySet()) {
            System.out.println("User name: " + entry.getKey());
        }

        // load filelist.xml of users
        fileListHashtable = new Hashtable<String, FileListService>();
        for (Map.Entry<String, String> entry : userListHashtable.entrySet()) {
            String userName = entry.getKey();
            FileListService fileList = FileListXMLService.loadFileListFromXML("./users/" + userName + "/");
            fileListHashtable.put(fileList.getPathname(), fileList);
            System.out.println("Load filelist.xml of user: " + userName + " filelist size: " + fileList.getFileHashTable().size());
            //            for (Map.Entry<String, FileInfo> tmpEntry : fileList.getFileHashTable().entrySet())
            //                System.out.println("pathName: " + fileList.getPathname() + " fileName: " + tmpEntry.getKey());
        }

        // sync with an active server (any one is fine)
        Vector<ActiveServer> activeServerList = ActiveServerListServer.getActiveServerList();
        if (!activeServerList.isEmpty()) {
            syncWithAServer(activeServerList.get(0));
        }
    }

    /**
     *
     */
    public void syncWithAll() throws RemoteException, JSchException, SftpException, Exception {

        System.out.println("Func: syncWithAll");

        // sync everything with active clients and servers
        syncWithAllServers();
        syncWithAllClients();

        System.out.println("Func: syncWithAll done");
    }

    /**
     *
     * @param activeServer
     * @throws RemoteException
     * @throws JSchException
     * @throws SftpException
     * @throws Exception
     */
    public void syncWithAServer(ActiveServer activeServer) throws RemoteException, JSchException, SftpException, Exception {

        System.out.println("Func: syncWithAServer");

        // sync everything with an active server
        ServerAPI server = activeServer.getServerAPI();
        Hashtable<String, FileListService> upToDateFileListHashtable = server.getFileListHashtableFromServer();

        // update hashtable
        fileListHashtable.clear();
        fileListHashtable = upToDateFileListHashtable;

        // update files
        for (Map.Entry<String, FileListService> entry : upToDateFileListHashtable.entrySet()) {
            String pathName = entry.getKey();
            int last = pathName.lastIndexOf("/");
            int lastButOne = pathName.lastIndexOf("/", last - 1);
            String userName = pathName.substring(lastButOne + 1, last);
            System.out.println("Pathname: " + pathName + " username: " + userName);
            for (Map.Entry<String, FileInfo> subEntry : entry.getValue().getFileHashTable().entrySet()) {
                String fileName = subEntry.getKey();
                downloadCreatedFileFromServer(userName, pathName, fileName, activeServer);
            }
        }

        System.out.println("Func: syncWithAServer done");
    }

    /**
     *
     * @throws RemoteException
     * @throws JSchException
     * @throws SftpException
     * @throws Exception
     */
    public void syncWithAllServers() throws RemoteException, JSchException, SftpException, Exception {

        System.out.println("Func: syncWithAllServers");

        // sync everything with active servers
        Vector<ActiveServer> activeServerList = ActiveServerListServer.getActiveServerList();
        for (ActiveServer activeServer : activeServerList) {
            if (!ServerStart.getServerIP().equals(activeServer.getServerIP()) && activeServer.isIsConnect() == 1) {
                syncWithAServer(activeServer);
            }
        }

        System.out.println("Func: syncWithAllServers done");
    }

    /**
     *
     * @param activeClient
     * @throws RemoteException
     * @throws JSchException
     * @throws SftpException
     * @throws Exception
     */
    public void syncWithAClient(ActiveClient activeClient) throws RemoteException, JSchException, SftpException, Exception {

        System.out.println("Func: syncWithAClient");

        // sync everything with an active client
        for (Map.Entry<String, FileListService> entry : fileListHashtable.entrySet()) {
            String pathName = entry.getKey();
            int last = pathName.lastIndexOf("/");
            int lastButOne = pathName.lastIndexOf("/", last - 1);
            String userName = pathName.substring(lastButOne + 1, last);
            System.out.println("Pathname: " + pathName + " username: " + userName);
            for (Map.Entry<String, FileInfo> subEntry : entry.getValue().getFileHashTable().entrySet()) {
                String fileName = subEntry.getKey();
                if (activeClient.getClientID().equals(userName)) {
                    syncCreatedFileWithClient(userName, pathName, fileName, activeClient);
                }
            }
        }

        System.out.println("Func: syncWithAClient done");
    }

    /**
     *
     * @throws RemoteException
     * @throws JSchException
     * @throws SftpException
     * @throws Exception
     */
    public void syncWithAllClients() throws RemoteException, JSchException, SftpException, Exception {

        System.out.println("Func: syncWithAllClients");

        // sync everything with active clients
        Vector<ActiveClient> activeClientList = ActiveClientListServer.getActiveClientList();
        for (Map.Entry<String, FileListService> entry : fileListHashtable.entrySet()) {
            String pathName = entry.getKey();
            int last = pathName.lastIndexOf("/");
            int lastButOne = pathName.lastIndexOf("/", last - 1);
            String userName = pathName.substring(lastButOne + 1, last);
            System.out.println("Pathname: " + pathName + " username: " + userName);
            for (Map.Entry<String, FileInfo> subEntry : entry.getValue().getFileHashTable().entrySet()) {
                String fileName = subEntry.getKey();
                for (ActiveClient activeClient : activeClientList) {
                    if (activeClient.getClientID().equals(userName)) {
                        syncModifiedFileWithClient(userName, pathName, fileName, activeClient);
                    }
                }
            }
        }

        System.out.println("Func: syncWithAllClients done");
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

        SFTPService sftpService = new SFTPService(activeServer.getServerIP());
        String srcFilePath = "./" + userName + "/" + fileName;
        String dstFilePath = "./" + userName + "/" + fileName;
        sftpService.uploadFile(srcFilePath, dstFilePath, this.getDir().toString(), this.getDir().toString());
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
    public void downloadCreatedFileFromServer(String userName, String directory, String fileName, ActiveServer activeServer) throws JSchException, SftpException {

        SFTPService sftpService = new SFTPService(activeServer.getServerIP());
        String srcFilePath = "./" + userName + "/" + fileName;
        String dstFilePath = "./" + userName + "/" + fileName;
        sftpService.uploadFile(srcFilePath, dstFilePath, this.getDir().toString(), this.getDir().toString());
    }

    /**
     *
     * @param userName
     * @param directory
     * @param fileName
     * @param activeServer
     * @param diff
     * @throws JSchException
     * @throws SftpException
     */
    public void uploadModifiedFileToServer(String userName, String directory, String fileName, ActiveServer activeServer, FileInfoDiff diff) throws JSchException, SftpException {

        SFTPService sftpService = new SFTPService(activeServer.getServerIP());
        String srcFilePath = "./" + userName + "/" + fileName;
        String dstFilePath = "./" + userName + "/" + fileName;
        sftpService.uploadFile(srcFilePath, dstFilePath, this.getDir().toString(), this.getDir().toString());
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
    public void uploadDeletedFileToServer(String userName, String directory, String fileName, ActiveServer activeServer) throws JSchException, SftpException {

        SFTPService sftpService = new SFTPService(activeServer.getServerIP());
        String filePath = "./" + userName + "/" + fileName;
        sftpService.deleteFile(filePath, this.getDir().toString());
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

        ServerAPI server = activeServer.getServerAPI();
        server.setFileInfoToServer(activeServer.getServerIP(), activeServer.getPort(), userName, directory, fileName, FileInfoService.fileInfoToXMLString(fileInfo));
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
    public void syncCreatedFileWithServer(String userName, String directory, String fileName, ActiveServer activeServer) throws RemoteException, JSchException, SftpException, IOException {

        System.out.println("Func: syncCreatedFileWithServer: " + activeServer.getServerIP());
        FileInfo fileInfo = fileListHashtable.get(directory).getFileInfoByFileName(fileName);
        ServerAPI server = activeServer.getServerAPI();
        String fileInfoStr;
        FileInfoDiff diff;
        if (server.containFileInfoFromServer(activeServer.getServerIP(), activeServer.getPort(), userName, directory, fileName)) {
            fileInfoStr = server.getFileInfoFromServer(activeServer.getServerIP(), activeServer.getPort(), userName, directory, fileName);
            diff = fileInfo.comparesToFileInfo(FileInfoService.parseXMLStringToFileInfo(fileInfoStr));
            System.out.println("diff.flag = " + diff.getFlag());
            if (diff.getFlag() == 1) {
                uploadCreatedFileToServer(userName, directory, fileName, activeServer);
                updateFileInfoToServer(userName, directory, fileName, activeServer, fileInfo);
            }
            if (diff.getFlag() == 3 | diff.getFlag() == 4) {
                System.out.println("Func: syncModifiedFileWithServer flag = 3 or 4");
                Path conflictFile = new File(this.getDir() + "/" + userName + "/" + fileName).toPath();
                File conflictFileCopy = new File(this.getDir() + "/" + userName + "/" + "conflicted_copy_from_" + fileInfo.getDeviceID() + "_" + fileInfo.getTimestamp() + "_" + fileName);
                if (!conflictFileCopy.exists()) {
                    Path conflictCopy = conflictFileCopy.toPath();
                    Files.copy(conflictFile, conflictCopy);
                    FileInfo newFileInfo = new FileInfo(fileInfo);
                    newFileInfo.setFileName(conflictCopy.getName(conflictCopy.getNameCount() - 1).toString());
                    Hashtable<String, String> conflictHashtable = new Hashtable<String, String>();
                    for (String key : newFileInfo.getFileHashCode().keySet()) {
                        String newkey = "conflicted_copy_from_" + fileInfo.getDeviceID() + "_" + key;
                        conflictHashtable.put(newkey, newFileInfo.getFileHashCode().get(key));
                    }
                    newFileInfo.setFileHashCode(conflictHashtable);
                    System.out.println("conflict file fileInfo: " + newFileInfo);
                    fileListHashtable.get(directory).addNewFileInfo(newFileInfo);
                }
            }
        } else {
            uploadCreatedFileToServer(userName, directory, fileName, activeServer);
            updateFileInfoToServer(userName, directory, fileName, activeServer, fileInfo);
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
    public synchronized void syncModifiedFileWithServer(String userName, String directory, String fileName, ActiveServer activeServer) throws RemoteException, JSchException, SftpException, IOException {

        System.out.println("Func: syncModifiedFileWithServer: " + activeServer.getServerIP());
        FileInfo fileInfo = fileListHashtable.get(directory).getFileInfoByFileName(fileName);
        ServerAPI server = activeServer.getServerAPI();
        String fileInfoStr;
        FileInfoDiff diff;

        if (server.containFileInfoFromServer(activeServer.getServerIP(), activeServer.getPort(), userName, directory, fileName)) {
            fileInfoStr = server.getFileInfoFromServer(activeServer.getServerIP(), activeServer.getPort(), userName, directory, fileName);
            diff = fileInfo.comparesToFileInfo(FileInfoService.parseXMLStringToFileInfo(fileInfoStr));
            System.out.println("diff.flag = " + diff.getFlag());
            System.out.println("local server fileinfo: " + fileInfo);
            System.out.println("remote server fileinfo: " + FileInfoService.parseXMLStringToFileInfo(fileInfoStr));
            if (diff.getFlag() == 1) {
                System.out.println("Func: syncModifiedFileWithServer flag = 1");
                uploadModifiedFileToServer(userName, directory, fileName, activeServer, diff);
                updateFileInfoToServer(userName, directory, fileName, activeServer, fileInfo);
            }
            if (diff.getFlag() == 3 || diff.getFlag() == 4) {
                System.out.println("Func: syncModifiedFileWithServer flag = 3 or 4");
                Path conflictFile = new File(this.getDir() + "/" + userName + "/" + fileName).toPath();
                File conflictFileCopy = new File(this.getDir() + "/" + userName + "/" + "conflicted_copy_from_" + fileInfo.getDeviceID() + "_" + fileInfo.getTimestamp() + "_" + fileName);
                if (!conflictFileCopy.exists()) {
                    Path conflictCopy = conflictFileCopy.toPath();
                    Files.copy(conflictFile, conflictCopy);
                    FileInfo newFileInfo = new FileInfo(fileInfo);
                    newFileInfo.setFileName(conflictCopy.getName(conflictCopy.getNameCount() - 1).toString());
                    Hashtable<String, String> conflictHashtable = new Hashtable<String, String>();
                    for (String key : newFileInfo.getFileHashCode().keySet()) {
                        String newkey = "conflicted_copy_from_" + fileInfo.getDeviceID() + "_" + key;
                        conflictHashtable.put(newkey, newFileInfo.getFileHashCode().get(key));
                    }
                    newFileInfo.setFileHashCode(conflictHashtable);
                    System.out.println("conflict file fileInfo: " + newFileInfo);
                    fileListHashtable.get(directory).addNewFileInfo(newFileInfo);
                }
            }
        } else {
            uploadCreatedFileToServer(userName, directory, fileName, activeServer);
            updateFileInfoToServer(userName, directory, fileName, activeServer, fileInfo);
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
    public void syncDeletedFileWithServer(String userName, String directory, String fileName, ActiveServer activeServer) throws RemoteException, JSchException, SftpException, IOException {

//        System.out.println("Func: syncDeletedFileWithServer: " + activeServer.getServerIP());
        FileInfo fileInfo = fileListHashtable.get(directory).getFileInfoByFileName(fileName);
//        uploadDeletedFileToServer(userName, directory, fileName, activeServer);
//        updateFileInfoToServer(userName, directory, fileName, activeServer, fileInfo);
        ServerAPI server = activeServer.getServerAPI();
        String fileInfoStr;
        FileInfoDiff diff;

        if (server.containFileInfoFromServer(activeServer.getServerIP(), activeServer.getPort(), userName, directory, fileName)) {
            fileInfoStr = server.getFileInfoFromServer(activeServer.getServerIP(), activeServer.getPort(), userName, directory, fileName);
            diff = fileInfo.comparesToFileInfo(FileInfoService.parseXMLStringToFileInfo(fileInfoStr));
            System.out.println("diff.flag = " + diff.getFlag());
            System.out.println("local server fileinfo: " + fileInfo);
            System.out.println("remote server fileinfo: " + FileInfoService.parseXMLStringToFileInfo(fileInfoStr));
            if (diff.getFlag() == 1 || diff.getFlag() == 5) {
                System.out.println("Func: syncModifiedFileWithServer flag = 1 or 5");
                uploadDeletedFileToServer(userName, directory, fileName, activeServer);
                updateFileInfoToServer(userName, directory, fileName, activeServer, fileInfo);
            }
            if (diff.getFlag() == 3 || diff.getFlag() == 4) {
                System.out.println("Func: syncModifiedFileWithServer flag = 3 or 4");
                Path conflictFile = new File(this.getDir() + "/" + userName + "/" + fileName).toPath();
                File conflictFileCopy = new File(this.getDir() + "/" + userName + "/" + "conflicted_copy_from_" + fileInfo.getDeviceID() + "_" + fileInfo.getTimestamp() + "_" + fileName);
                if (!conflictFileCopy.exists()) {
                    Path conflictCopy = conflictFileCopy.toPath();
                    Files.copy(conflictFile, conflictCopy);
                    FileInfo newFileInfo = new FileInfo(fileInfo);
                    newFileInfo.setFileName(conflictCopy.getName(conflictCopy.getNameCount() - 1).toString());
                    Hashtable<String, String> conflictHashtable = new Hashtable<String, String>();
                    for (String key : newFileInfo.getFileHashCode().keySet()) {
                        String newkey = "conflicted_copy_from_" + fileInfo.getDeviceID() + "_" + key;
                        conflictHashtable.put(newkey, newFileInfo.getFileHashCode().get(key));
                    }
                    newFileInfo.setFileHashCode(conflictHashtable);
                    System.out.println("conflict file fileInfo: " + newFileInfo);
                    fileListHashtable.get(directory).addNewFileInfo(newFileInfo);
                }
            }
        }
    }

    /**
     *
     * @param directory
     * @param fileName
     * @param activeClient
     * @throws RemoteException
     * @throws JSchException
     * @throws SftpException
     */
    public void syncCreatedFileWithClient(String userName, String directory, String fileName, ActiveClient activeClient) throws RemoteException, JSchException, SftpException, Exception {

        System.out.println("Func: syncCreatedFileWithClient: " + activeClient.getClientIP());
        FileInfo fileInfo = fileListHashtable.get(directory).getFileInfoByFileName(fileName);
        ClientAPI client = activeClient.getClientAPI();
        if (client.containFileInfo(activeClient.getClientIP(), activeClient.getPort(), userName, directory, fileName)) {
            String fileInfoStrFromClient = client.getFileInfoFromClient(activeClient.getClientIP(), activeClient.getPort(),
                    userName, directory, fileName);
            FileInfoDiff diff = fileInfo.comparesToFileInfo(FileInfoService.parseXMLStringToFileInfo(fileInfoStrFromClient));
            if (diff.getFlag() == 1) {
                client.syncModifiedFileFromServer(activeClient.getClientIP(), activeClient.getPort(),
                        userName, directory, fileName, FileInfoService.fileInfoToXMLString(fileInfo));
            }
        } else {
            client.syncModifiedFileFromServer(activeClient.getClientIP(), activeClient.getPort(),
                    userName, directory, fileName, FileInfoService.fileInfoToXMLString(fileInfo));
        }
    }

    /**
     *
     * @param userName
     * @param directory
     * @param fileName
     * @param activeClient
     * @throws RemoteException
     * @throws JSchException
     * @throws SftpException
     */
    public synchronized void syncModifiedFileWithClient(String userName, String directory, String fileName, ActiveClient activeClient) throws RemoteException, JSchException, SftpException, Exception {

        System.out.println("Func: syncModifiedFileWithClient: " + activeClient.getClientIP());
        FileInfo fileInfo = fileListHashtable.get(directory).getFileInfoByFileName(fileName);
        ClientAPI client = activeClient.getClientAPI();
        if (client.containFileInfo(activeClient.getClientIP(), activeClient.getPort(), userName, directory, fileName)) {
            String fileInfoStrFromClient = client.getFileInfo(activeClient.getClientIP(), activeClient.getPort(),
                    userName, directory, fileName);
            FileInfoDiff diff = fileInfo.comparesToFileInfo(FileInfoService.parseXMLStringToFileInfo(fileInfoStrFromClient));
            if (diff.getFlag() == 1) {
                client.syncModifiedFileFromServer(activeClient.getClientIP(), activeClient.getPort(),
                        userName, directory, fileName, FileInfoService.fileInfoToXMLString(fileInfo));
            }
        } else {
            client.syncModifiedFileFromServer(activeClient.getClientIP(), activeClient.getPort(),
                    userName, directory, fileName, FileInfoService.fileInfoToXMLString(fileInfo));
        }
    }

    /**
     *
     * @param userName
     * @param directory
     * @param fileName
     * @param activeClient
     * @throws RemoteException
     * @throws JSchException
     * @throws SftpException
     * @throws Exception
     */
    public synchronized void syncDeletedFileWithClient(String userName, String directory, String fileName, ActiveClient activeClient) throws RemoteException, JSchException, SftpException, Exception {

        System.out.println("Func: syncDeletedFileWithClient: " + activeClient.getClientIP());
        FileInfo fileInfo = fileListHashtable.get(directory).getFileInfoByFileName(fileName);
        ClientAPI client = activeClient.getClientAPI();
        if (client.containFileInfo(activeClient.getClientIP(), activeClient.getPort(), userName, directory, fileName)) {
            String fileInfoStrFromClient = client.getFileInfo(activeClient.getClientIP(), activeClient.getPort(),
                    userName, directory, fileName);
            FileInfoDiff diff = fileInfo.comparesToFileInfo(FileInfoService.parseXMLStringToFileInfo(fileInfoStrFromClient));
            if (diff.getFlag() == 1) {
                client.syncDeletedFileFromServer(activeClient.getClientIP(), activeClient.getPort(),
                        userName, directory, fileName, FileInfoService.fileInfoToXMLString(fileInfo));
            }
        } else {
            client.syncDeletedFileFromServer(activeClient.getClientIP(), activeClient.getPort(),
                    userName, directory, fileName, FileInfoService.fileInfoToXMLString(fileInfo));
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
    public void syncCreatedFile(String userName, String directory, String fileName) throws JSchException, RemoteException, SftpException, Exception {

        System.out.println("Func: syncCreatedFile");

        // sync create file with active servers
        Vector<ActiveServer> activeServerList = ActiveServerListServer.getActiveServerList();
        for (ActiveServer activeServer : activeServerList) {
            if (!ServerStart.getServerIP().equals(activeServer.getServerIP()) && activeServer.isIsConnect() == 1) {
                syncCreatedFileWithServer(userName, directory, fileName, activeServer);
            }
        }

        // sync create file with active clients
        Vector<ActiveClient> activeClientList = ActiveClientListServer.getActiveClientList();
        for (ActiveClient activeClient : activeClientList) {
            if (activeClient.getClientID().equals(userName)) {
                syncCreatedFileWithClient(userName, directory, fileName, activeClient);
            }
        }

        System.out.println("Func: syncCreatedFile done");
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
    public void syncModifiedFile(String userName, String directory, String fileName) throws JSchException, RemoteException, SftpException, Exception {

        System.out.println("\nFunc: syncModifiedFile");

        // sync modified file with active servers
        Vector<ActiveServer> activeServerList = ActiveServerListServer.getActiveServerList();
        for (ActiveServer activeServer : activeServerList) {
            if (!ServerStart.getServerIP().equals(activeServer.getServerIP()) && activeServer.isIsConnect() == 1) {
                syncModifiedFileWithServer(userName, directory, fileName, activeServer);
            }
        }

        // sync modified file with active clients
        Vector<ActiveClient> activeClientList = ActiveClientListServer.getActiveClientList();
        for (ActiveClient activeClient : activeClientList) {
            if (activeClient.getClientID().equals(userName)) {
                syncModifiedFileWithClient(userName, directory, fileName, activeClient);
            }
        }

        System.out.println("Func: syncModifiedFile done");
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
    public void syncDeletedFile(String userName, String directory, String fileName) throws JSchException, RemoteException, SftpException, Exception {

        System.out.println("Func: syncDeletedFile");

        // sync deleted file with active servers
        Vector<ActiveServer> activeServerList = ActiveServerListServer.getActiveServerList();
        for (ActiveServer activeServer : activeServerList) {
            if (!ServerStart.getServerIP().equals(activeServer.getServerIP()) && activeServer.isIsConnect() == 1) {
                syncDeletedFileWithServer(userName, directory, fileName, activeServer);
            }
        }

        // sync deleted file with active clients
        Vector<ActiveClient> activeClientList = ActiveClientListServer.getActiveClientList();
        for (ActiveClient activeClient : activeClientList) {
            if (activeClient.getClientID().equals(userName)) {
                syncDeletedFileWithClient(userName, directory, fileName, activeClient);
            }
        }

        System.out.println("Func: syncDeletedFile done");
    }

    /**
     *
     * @throws JSchException
     * @throws RemoteException
     * @throws SftpException
     * @throws Exception
     */
    public void watchDir() throws JSchException, RemoteException, SftpException, Exception {

        for (;;) {

            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {

                WatchEvent.Kind kind = event.kind();

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);

                String userName = child.getName(child.getNameCount() - 2).toString();
                String directory = "./users/" + userName + "/";
                String fileName = child.getName(child.getNameCount() - 1).toString();

                // print out event
                System.out.format("%s: %s\n", event.kind().name(), child);
                System.out.format("userName: %s, directory: %s, fileName: %s\n", userName, directory, fileName);

                Thread.sleep(5000);

                if (kind == OVERFLOW) {
                    continue;
                } else if (kind == ENTRY_CREATE) {
                    if (!fileName.equalsIgnoreCase("filelist.xml")) {
                        syncCreatedFile(userName, directory, fileName);
                    }
                } else if (kind == ENTRY_MODIFY) {
                    if (!fileName.equalsIgnoreCase("filelist.xml")) {
                        syncModifiedFile(userName, directory, fileName);
                    }
                } else if (kind == ENTRY_DELETE) {
                    if (!fileName.equalsIgnoreCase("filelist.xml")) {
                        syncDeletedFile(userName, directory, fileName);
                    }
                } else {
                    System.out.println("ERROR: un-registered event!"); // never reach here!
                }

                // if directory is created, and watching recursively, then register it and its sub-directories
                if (recursive && (kind == ENTRY_CREATE)) {
                    try {
                        if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                            registerAll(child);
                        }
                    } catch (IOException x) {
                        // ignore to keep sample readbale
                    }
                }
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);
                // all directories are inaccessible
                if (keys.isEmpty()) {
                    // break;
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
            watchDir();
        } catch (JSchException ex) {
            Logger.getLogger(FileSyncServerService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(FileSyncServerService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SftpException ex) {
            Logger.getLogger(FileSyncServerService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(FileSyncServerService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
