/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.client;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import dblike.api.ClientAPI;
import dblike.api.ServerAPI;
import dblike.client.service.ClientConfig;
import dblike.client.service.FileSyncClientService;
import static dblike.client.service.FileSyncClientService.syncCreatedFileFromServer;
import static dblike.client.service.FileSyncClientService.updateFileInfoFromServer;
import dblike.server.service.FileListXMLService;
import dblike.server.service.FileSyncServerService;
import dblike.service.FileInfo;
import dblike.service.FileInfoDiff;
import dblike.service.FileInfoService;
import dblike.service.FileSegmentService;
import dblike.service.SFTPService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the main class for the client, client program will start from here.
 *
 * @author wenhanwu
 */
public class ClientStart {

    private static Registry registry;
    private static String clientID;
    public static Client aClient;

    /**
     * @return the clientID
     */
    public static String getClientID() {
        return clientID;
    }

    /**
     * @param aClientID the clientID to set
     */
    public static void setClientID(String aClientID) {
        clientID = aClientID;
    }

    /**
     * This is to bind for the client on the given port, to prepare be called by
     * server.
     */
    public static void bindForClient() {
        try {
//            System.out.println("Client start at " + ClientConfig.getCurrentClient().getIp() + ":" + ClientConfig.getCurrentClient().getPort());
//            System.out.println("Will connect to server " + ClientConfig.getServerList().get(0).getServerIP() + ":" + ClientConfig.getServerList().get(0).getPort());

            ClientImp client = new ClientImp();
            ClientAPI clientStub = (ClientAPI) UnicastRemoteObject.exportObject(client, 0);
            int cPort = Integer.parseInt(ClientConfig.getCurrentClient().getPort());
            registry = LocateRegistry.createRegistry(cPort);
            String clientBind = "clientUtility" + getClientID() + ClientConfig.getCurrentClient().getDeviceID() + ClientConfig.getCurrentClient().getIp() + ClientConfig.getCurrentClient().getPort();
            try {
                registry.bind(clientBind, clientStub);
            } catch (AlreadyBoundException ex) {
                Logger.getLogger(ClientStart.class.getName()).log(Level.SEVERE, null, ex);
            } catch (AccessException ex) {
                Logger.getLogger(ClientStart.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Already bind: " + "[" + clientBind + "]");
            System.out.println("Client ready");
        } catch (RemoteException ex) {
            Logger.getLogger(ClientStart.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Download FileList XML file from the connected server. 
     * @throws RemoteException
     * @throws JSchException
     * @throws SftpException
     * @throws Exception 
     */
    public static void downloadFileListFromConnectedServer() throws RemoteException, JSchException, SftpException, Exception {

        if (ClientConfig.getServerList().get(ClientConfig.getCurrentServerIndex()).getServerAPI() == null) {
            try {
                ClientConfig.getServerList().get(ClientConfig.getCurrentServerIndex()).setRegistry(
                        LocateRegistry.getRegistry(ClientConfig.getServerList().get(ClientConfig.getCurrentServerIndex()).getServerIP(),
                        ClientConfig.getServerList().get(ClientConfig.getCurrentServerIndex()).getPort()));
                ClientConfig.getServerList().get(ClientConfig.getCurrentServerIndex()).setServerAPI((ServerAPI) (ClientConfig.getServerList().get(ClientConfig.getCurrentServerIndex()).getRegistry()).lookup("serverUtility"));
            } catch (NotBoundException ex) {
            }
        }

        ServerAPI server = ClientConfig.getServerList().get(ClientConfig.getCurrentServerIndex()).getServerAPI();

        server.saveFileListHashtable();

        SFTPService sftpService = new SFTPService(ClientConfig.getServerList().get(ClientConfig.getCurrentServerIndex()).getServerIP());
        sftpService.downloadFile(FileInfoService.getSERVER_USERS_FOLDER() + ClientConfig.getCurrentClient().getClientID() + "/filelist.xml", FileInfoService.getSERVER_USERS_FOLDER() + ClientConfig.getCurrentClient().getClientID() + "/filelist.xml");
    }

    /**
     * Synchronize the local and the connected server. 
     * @throws JSchException
     * @throws SftpException
     * @throws Exception 
     */
    public static void syncWithConnectedServer() throws JSchException, SftpException, Exception {
        FileSyncClientService.updateAllLocalFileInfo(ClientConfig.getCurrentClient().getFolderPath());
        Hashtable<String, FileInfo> fileListThis = ClientConfig.getMyFileList().getFileHashTable();
        Hashtable<String, FileInfo> fileListThat = FileListXMLService.loadFileListFromXML(FileInfoService.getSERVER_USERS_FOLDER() + ClientConfig.getCurrentClient().getClientID() + "/").getFileHashTable();
//        System.out.println("fileListThis.size = " + fileListThis.size());
//        System.out.println("fileListThat.size = " + fileListThat.size());
        Hashtable<String, FileInfoDiff> diffList = compareFileLists(fileListThis, fileListThat);
//        System.out.println("diffList.size = " + diffList.size());
        
        for (String key : diffList.keySet()) {
//            System.out.println("file: " + key + "    diff.flag = " + diffList.get(key).getFlag());
//            System.out.println("thiscontains? " + key + " " + fileListThis.keySet().contains(key));
//            System.out.println("thatcontains? " + key + " " + fileListThat.keySet().contains(key));

            if (diffList.get(key).getFlag() == 1) {

                if (fileListThis.keySet().contains(key) && fileListThat.keySet().contains(key)) {
                    if (!fileListThis.get(key).getFileHashCode().isEmpty()) {
                        FileSyncClientService.uploadCreatedFileToServer(ClientConfig.getCurrentClient().getClientID(),
                                "./users/" + ClientConfig.getCurrentClient().getClientID() + "/", key);
                        FileSyncClientService.updateFileInfoToServer(ClientConfig.getCurrentClient().getClientID(),
                                "./users/" + ClientConfig.getCurrentClient().getClientID() + "/", key, diffList.get(key));
                        System.out.println("After update fileInfo to Server.");
                    }
                }

                if (fileListThis.keySet().contains(key) && !fileListThat.keySet().contains(key)) {
                    if (!fileListThis.get(key).getFileHashCode().isEmpty()) {
                        FileSyncClientService.uploadCreatedFileToServer(ClientConfig.getCurrentClient().getClientID(),
                                "./users/" + ClientConfig.getCurrentClient().getClientID() + "/", key);
                        FileSyncClientService.updateFileInfoToServer(ClientConfig.getCurrentClient().getClientID(),
                                "./users/" + ClientConfig.getCurrentClient().getClientID() + "/", key, diffList.get(key));
                        System.out.println("After update fileInfo to Server.");
                    }
                }
                if (!fileListThis.keySet().contains(key) && fileListThat.keySet().contains(key)) {
                    if (fileListThat.get(key).getFileHashCode().isEmpty()) {
                        FileSyncClientService.syncCreatedFileFromServer(ClientConfig.getCurrentClient().getClientID(),
                                "./users/" + ClientConfig.getCurrentClient().getClientID() + "/", key);
                        FileSyncClientService.updateFileInfoFromServer(ClientConfig.getCurrentClient().getClientID(),
                                "./users/" + ClientConfig.getCurrentClient().getClientID() + "/", key);
                        System.out.println("After update fileInfo from Server.");
                    }
                }
            }

            if ((diffList.get(key).getFlag() == 3 || diffList.get(key).getFlag() == 4) && !fileListThat.get(key).getFileHashCode().isEmpty()) {
                Path conflictFile = new File(ClientConfig.getCurrentClient().getFolderPath() + "/" + key).toPath();
                Path conflictCopy = new File(ClientConfig.getCurrentClient().getFolderPath() + "/conflicted_copy_from_" + ClientConfig.getCurrentClient().getDeviceID() + "_" + key).toPath();
                Files.copy(conflictFile, conflictCopy);
                FileSyncClientService.syncCreatedFileFromServer(ClientConfig.getCurrentClient().getClientID(),
                        "./users/" + ClientConfig.getCurrentClient().getClientID() + "/", key);
                FileSyncClientService.updateFileInfoFromServer(ClientConfig.getCurrentClient().getClientID(),
                        "./users/" + ClientConfig.getCurrentClient().getClientID() + "/", key);
            }
            if (diffList.get(key).getFlag() == 5 && !fileListThat.get(key).getFileHashCode().isEmpty()) {
                FileSyncClientService.syncCreatedFileFromServer(ClientConfig.getCurrentClient().getClientID(),
                        "./users/" + ClientConfig.getCurrentClient().getClientID() + "/", key);
                FileSyncClientService.updateFileInfoFromServer(ClientConfig.getCurrentClient().getClientID(),
                        "./users/" + ClientConfig.getCurrentClient().getClientID() + "/", key);
            }
        }
    }

    /**
     * Compare two file lists from client and server. 
     * @param fileListThis
     * @param fileListThat
     * @return 
     */
    public static Hashtable<String, FileInfoDiff> compareFileLists(Hashtable<String, FileInfo> fileListThis, Hashtable<String, FileInfo> fileListThat) {
        Hashtable<String, FileInfoDiff> diffList = new Hashtable<>();

        // if local has no files, then set flag = 5, for downloading
        if (fileListThis.isEmpty()) {
            for (String key : fileListThat.keySet()) {
                diffList.put(key, new FileInfoDiff(5, fileListThat.get(key)));
            }
            return diffList;
        }

        // if remote has no files, then set flag = 1, for uploading.
        if (fileListThat.isEmpty()) {
            for (String key : fileListThis.keySet()) {
                diffList.put(key, new FileInfoDiff(1, fileListThis.get(key)));
            }
            return diffList;
        }

        // get the union of all keys (fileNames)
//        ArrayList<String> allKeys = new ArrayList<>();
//        for (String key : fileListThis.keySet()) {
//            allKeys.add(key);
//        }
//        for (String key : fileListThat.keySet()) {
//            if (!fileListThat.keySet().contains(key)) {
//                allKeys.add(key);
//            }
//        }

        HashSet<String> allKeys = new HashSet<>();
        for (String key : fileListThis.keySet()) {
            allKeys.add(key);
        }
        for (String key : fileListThat.keySet()) {
            allKeys.add(key);
        }        
        
        for (String key : allKeys) {
            if (!fileListThis.keySet().contains(key)) { // then must be in remote, set flag = 5 for downloading
                diffList.put(key, new FileInfoDiff(5, fileListThat.get(key)));
            } else {
                System.out.println("FileInfo = " + fileListThis.get(key));
                if (!fileListThat.keySet().contains(key)) { // then must be new, set flag = 1 for uploading
                    diffList.put(key, new FileInfoDiff(1, fileListThis.get(key)));
                } else {
                    diffList.put(key, fileListThis.get(key).comparesToFileInfo(fileListThat.get(key)));
                }
            }
        }

        return diffList;
    }

    /**
     * This is the main method, the entry for the client.
     *
     * @param args
     * @throws IOException
     * @throws RemoteException
     * @throws Exception
     */
    public static void main(String args[]) throws IOException, RemoteException, Exception {
        ClientConfig.loadServerList();
        int availableIndex = ClientConfig.pickupAvailableServer();
        System.out.println("Picked No." + availableIndex);
        if (availableIndex == -1) {
            System.out.println("No server available!!!");
            return;
        }

        // new thread to inilialize client.
        ClientConfig.setCurrentServerIndex(ClientConfig.pickupAvailableServer());
        aClient = new Client(ClientConfig.getServerList().get(ClientConfig.getCurrentServerIndex()).getServerIP(), ClientConfig.getServerList().get(ClientConfig.getCurrentServerIndex()).getPort());
        clientID = aClient.login();
        aClient.initData();
        bindForClient();

        downloadFileListFromConnectedServer();
        syncWithConnectedServer();

        // new thread to synchronize files 
        String directory = ClientConfig.getCurrentClient().getFolderPath();
        FileSyncClientService fileSyncServer = new FileSyncClientService(directory);
        Thread fileSyncServerThread = new Thread(fileSyncServer);
        fileSyncServerThread.start();
    }
}
