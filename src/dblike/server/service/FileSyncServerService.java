/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.server.service;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import dblike.api.ServerAPI;
import dblike.server.ActiveClient;
import dblike.server.ActiveServer;
import dblike.service.FileInfo;
import dblike.service.SFTPService;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author JingboYu
 */
public class FileSyncServerService implements Runnable {
    
    private Hashtable<String, FileListService> fileListHashtable;
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
    
    public ServerAPI getServerAPI(ActiveServer activeServer) throws RemoteException
    {
        Registry registry = null;
        ServerAPI server = null;
        try {
            registry = LocateRegistry.getRegistry(activeServer.getServerIP(), activeServer.getPort());
            server = (ServerAPI) registry.lookup("serverUtility");
        } catch (NotBoundException ex) {
            Logger.getLogger(SyncActionServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AccessException ex) {
            Logger.getLogger(SyncActionServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return server;
    }
    
    public ServerAPI getClientAPI(ActiveClient activeClient) throws RemoteException
    {
        Registry registry = null;
        ServerAPI server = null;
        try {
            registry = LocateRegistry.getRegistry(activeClient.getClientIP(), activeClient.getPort());
            server = (ServerAPI) registry.lookup("serverUtility");
        } catch (NotBoundException ex) {
            Logger.getLogger(SyncActionServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AccessException ex) {
            Logger.getLogger(SyncActionServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return server;
    }
    
    public void uploadCreateFileToServer(String userName, String directory, String fileName, ActiveServer activeServer) throws JSchException, SftpException
    {
        SFTPService sftpService = new SFTPService();
        sftpService.uploadFile(userName + "/" + directory + "/" + fileName, "./" + userName + "/" + directory + "/" + fileName);
    }
    
    public void updateFileInfoToServer(String userName, String directory, String fileName, ActiveServer activeServer) throws RemoteException
    {
        // get fileinfo from server
        ServerAPI server = getServerAPI(activeServer);
        server.setFileInfoToServer(activeServer.getServerIP(), activeServer.getPort(), userName, directory, fileName,
                fileListHashtable.get(userName).getFileInfoByFileName(fileName));
    }
    
    public void uploadCreateFileToClient(String userName, String directory, String fileName, ActiveClient activeClient) throws JSchException, SftpException
    {
        SFTPService sftpService = new SFTPService();
        sftpService.uploadFile(userName + "/" + directory + "/" + fileName, "./" + userName + "/" + directory + "/" + fileName);
    }
    
    public void updateFileInfoToClient(String userName, String directory, String fileName, ActiveClient activeClient) throws RemoteException
    {
        // get fileinfo from server
        ServerAPI server = getClientAPI(activeClient);
        server.setFileInfoToServer(activeClient.getClientIP(), activeClient.getPort(), userName, directory, fileName,
                fileListHashtable.get(userName).getFileInfoByFileName(fileName));
    }
    
    public void syncCreatedFileWithServer(String userName, String directory, String fileName, ActiveServer activeServer) throws RemoteException, JSchException, SftpException
    {
        // get fileinfo from server
        ServerAPI server = getServerAPI(activeServer);
        FileInfo fileInfo = server.getFileInfoFromServer(activeServer.getServerIP(), activeServer.getPort(), userName, directory, fileName);
        ArrayList<FileInfo> diff = fileListHashtable.get(userName).getFileInfoByFileName(fileName).compareByHashCode(fileInfo);
        if (diff.isEmpty()) // not found then upload files
        {
            uploadCreateFileToServer(userName, directory, fileName, activeServer);
            updateFileInfoToServer(userName, directory, fileName, activeServer);
        }
    }
    
    public void syncCreatedFileWithClient(String userName, String directory, String fileName, ActiveClient activeClient) throws RemoteException, JSchException, SftpException
    {
        ServerAPI server = getClientAPI(activeClient);
        FileInfo fileInfo = server.getFileInfoFromServer(activeClient.getClientIP(), activeClient.getPort(), userName, directory, fileName);
        ArrayList<FileInfo> diff = fileListHashtable.get(userName).getFileInfoByFileName(fileName).compareByHashCode(fileInfo);
        if (diff.isEmpty()) // not found then upload files
        {
            uploadCreateFileToClient(userName, directory, fileName, activeClient);
            updateFileInfoToClient(userName, directory, fileName, activeClient);
        }
    }
    
    public void syncCreatedFile(String userName, String directory, String fileName) throws JSchException, RemoteException, SftpException
    {
        // sync create file with active servers
        ArrayList<ActiveServer> activeServerList = ActiveServerListServer.getActiveServerList();
        for (ActiveServer activeServer : activeServerList)
            syncCreatedFileWithServer(userName, directory, fileName, activeServer);
        
        // sync create file with active clients
        ArrayList<ActiveClient> activeClientList = ActiveClientListServer.getActiveClientList();
        for (ActiveClient activeClient : activeClientList)
            syncCreatedFileWithClient(userName, directory, fileName, activeClient);
    }
    
    
    public void watchFile() throws IOException, JSchException, RemoteException, SftpException {
        
        for (;;) {
            
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
                
                Path fileName = event.context();
                Path child = directory.resolve(fileName);
                String contentType = Files.probeContentType(child);
                
                if (e.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                    String userName = directory.getName(directory.getNameCount()-2).toString();
                    String directoryName = directory.getName(directory.getNameCount()-1).toString();
                    syncCreatedFile(userName, directoryName, fileName.toString());
                    System.out.println("directory: " + directory.getParent() + " file was created: " + child + " contentType: " + contentType);
                } else if (e.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                    System.out.println("file was modified: " + fileName + " contentType: " + contentType);
                } else if (e.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
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
