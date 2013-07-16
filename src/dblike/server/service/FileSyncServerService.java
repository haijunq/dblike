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
import dblike.service.FileInfo;
import dblike.service.SFTPService;
import dblike.service.ServiceUtils;
import dblike.service.WatchDirectoryService;
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
public class FileSyncServerService implements Runnable {
    
    public static Hashtable<String, FileListService> fileListHashtable;
    private final WatchService watchService;
    private final Path directory;
    
    public FileSyncServerService(String directory) throws IOException {
        
        // load a list of users
        Hashtable<String, String> userList = UserListXMLReader.getValidUserList();
        System.out.println("Loaded a user list.");
        
        // load file info from xml file on the server
        fileListHashtable = new Hashtable<String, FileListService>();
        for (Map.Entry<String, String> entry : userList.entrySet())
        {
            String userName = entry.getKey();
            FileListService fileList = new FileListService(userName);
            fileListHashtable.put(userName, fileList);
        }
        System.out.println("Loaded file info lists for each user.");
        
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
    public void uploadCreatedFileToServer(String userName, String directory, String fileName, ActiveServer activeServer) throws JSchException, SftpException
    {
        SFTPService sftpService = new SFTPService(activeServer.getServerIP(), activeServer.getPort());
        String srcFilePath = "~/" + userName + "/" + fileName;
        String dstFilePath = "~/" + userName + "/" + fileName;
        sftpService.uploadFile(srcFilePath, dstFilePath);
    }
    
    /**
     *
     * @param userName
     * @param directory
     * @param fileName
     * @param activeServer
     * @throws RemoteException
     */
    public void updateFileInfoToServer(String userName, String directory, String fileName, ActiveServer activeServer) throws RemoteException
    {
        // get fileinfo from server
        ServerAPI server = activeServer.getServerAPI();
        server.setFileInfoToServer(activeServer.getServerIP(), activeServer.getPort(), userName, directory, fileName,
                fileListHashtable.get(userName).getFileInfoByFileName(fileName));
    }
    
    /**
     *
     * @param userName
     * @param directory
     * @param fileName
     * @param activeClient
     * @throws JSchException
     * @throws SftpException
     */
    public void uploadCreateFileToClient(String userName, String directory, String fileName, ActiveClient activeClient) throws JSchException, SftpException
    {
//        ClientAPI client = activeClient.getClientAPI();
//        client.
//                String srcFilePath = "~/" +
//                sftpService.uploadFile(userName + "/" + directory + "/" + fileName, "./" + userName + "/" + directory + "/" + fileName);
    }
    
    public void updateFileInfoToClient(String userName, String directory, String fileName, ActiveClient activeClient) throws RemoteException
    {
//        // get fileinfo from server
//        ServerAPI server = activeClient.
//                server.setFileInfoToServer(activeClient.getClientIP(), activeClient.getPort(), userName, directory, fileName,
//                fileListHashtable.get(userName).getFileInfoByFileName(fileName));
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
    public void syncCreatedFileWithServer(String userName, String directory, String fileName, ActiveServer activeServer) throws RemoteException, JSchException, SftpException
    {
//        // get fileinfo from server
//        ServerAPI server = activeServer.getServerAPI();
//        FileInfo fileInfo = server.getFileInfoFromServer(activeServer.getServerIP(), activeServer.getPort(), userName, directory, fileName);
//        ArrayList<FileInfo> diff = fileListHashtable.get(userName).getFileInfoByFileName(fileName).compareByHashCode(fileInfo);
//        if (!diff.isEmpty()) // not found then upload files
//        {
//            uploadCreatedFileToServer(userName, directory, fileName, activeServer);
//            updateFileInfoToServer(userName, directory, fileName, activeServer);
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
    public void syncModifiedFileWithServer(String userName, String directory, String fileName, ActiveServer activeServer) throws RemoteException, JSchException, SftpException
    {
//        // get fileinfo from server
//        ServerAPI server = activeServer.getServerAPI();
//        FileInfo fileInfo = server.getFileInfoFromServer(activeServer.getServerIP(), activeServer.getPort(), userName, directory, fileName);
//        ArrayList<FileInfo> diff = fileListHashtable.get(userName).getFileInfoByFileName(fileName).compareByHashCode(fileInfo);
//        if (!diff.isEmpty()) // not found then upload files
//        {
//            uploadCreatedFileToServer(userName, directory, fileName, activeServer);
//            updateFileInfoToServer(userName, directory, fileName, activeServer);
//        }
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
    public void syncCreatedFileWithClient(String userName, String directory, String fileName, ActiveClient activeClient) throws RemoteException, JSchException, SftpException
    {
//        ClientAPI client = activeClient.getClientAPI();
//        FileInfo fileInfo = client. // get file info from client
//                ArrayList<FileInfo> diff = fileListHashtable.get(userName).getFileInfoByFileName(fileName).compareByHashCode(fileInfo); // compare with file info
//        if (diff.isEmpty()) // not found then upload files
//        {
//            uploadCreateFileToClient(userName, directory, fileName, activeClient);
//            updateFileInfoToClient(userName, directory, fileName, activeClient);
//        }
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
    public void syncModifiedFileWithClient(String userName, String directory, String fileName, ActiveClient activeClient) throws RemoteException, JSchException, SftpException
    {
//        ClientAPI client = activeClient.getClientAPI();
//        FileInfo fileInfo = client. // get file info from client
//                ArrayList<FileInfo> diff = fileListHashtable.get(userName).getFileInfoByFileName(fileName).compareByHashCode(fileInfo); // compare with file info
//        if (diff.isEmpty()) // not found then upload files
//        {
//            uploadCreateFileToClient(userName, directory, fileName, activeClient);
//            updateFileInfoToClient(userName, directory, fileName, activeClient);
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
    public void syncCreatedFile(String userName, String directory, String fileName) throws JSchException, RemoteException, SftpException
    {
        // sync create file with active servers
        Vector<ActiveServer> activeServerList = ActiveServerListServer.getActiveServerList();
        for (ActiveServer activeServer : activeServerList)
        {
            syncCreatedFileWithServer(userName, directory, fileName, activeServer);
        }
        
        // sync create file with active clients
        Vector<ActiveClient> activeClientList = ActiveClientListServer.getActiveClientList();
        for (ActiveClient activeClient : activeClientList)
        {
            if (activeClient.getClientID().equals(userName))
                syncCreatedFileWithClient(userName, directory, fileName, activeClient);
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
    public void syncModifiedFile(String userName, String directory, String fileName) throws JSchException, RemoteException, SftpException
    {
        // sync modified file with active servers
        Vector<ActiveServer> activeServerList = ActiveServerListServer.getActiveServerList();
        for (ActiveServer activeServer : activeServerList)
        {
            syncModifiedFileWithServer(userName, directory, fileName, activeServer);
        }
        
        // sync modified file with active clients
        Vector<ActiveClient> activeClientList = ActiveClientListServer.getActiveClientList();
        for (ActiveClient activeClient : activeClientList)
        {
            if (activeClient.getClientID().equals(userName))
                syncModifiedFileWithClient(userName, directory, fileName, activeClient);
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
    public void syncDeletedFile(String userName, String directory, String fileName) throws JSchException, RemoteException, SftpException
    {
//        // sync deleted file with active servers
//        Vector<ActiveServer> activeServerList = ActiveServerListServer.getActiveServerList();
//        for (ActiveServer activeServer : activeServerList)
//        {
//            syncDeletedFileWithServer(userName, directory, fileName, activeServer);
//        }
//        
//        // sync deleted file with active clients
//        Vector<ActiveClient> activeClientList = ActiveClientListServer.getActiveClientList();
//        for (ActiveClient activeClient : activeClientList)
//        {
//            if (activeClient.getClientID().equals(userName))
//                syncDeletedFileWithClient(userName, directory, fileName, activeClient);
//        }
    }
    
    
    
    /**
     *
     * @throws IOException
     * @throws JSchException
     * @throws RemoteException
     * @throws SftpException
     */
    public void watchFile() throws IOException, JSchException, RemoteException, SftpException {
        
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
                
                // get file info
                String userName = directory.getName(directory.getNameCount()-1).toString();
                String directoryName = "";
                String fileName = directory.getName(directory.getNameCount()).toString();
                
                if (e.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                    syncCreatedFile(userName, directoryName, fileName);
                    System.out.println("directory: " + directory.getParent() + " file was created: " + fileName);
                } else if (e.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                    syncModifiedFile(userName, directoryName, fileName);
                    System.out.println("file was modified: " + fileName);
                } else if (e.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                    syncDeletedFile(userName, directoryName, fileName);
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
        }
    }
}
