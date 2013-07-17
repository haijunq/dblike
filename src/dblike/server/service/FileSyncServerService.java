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
    
    public void uploadModifiedFileToServer(String userName, String directory, String fileName, ActiveServer activeServer, FileInfoDiff diff) throws JSchException, SftpException
    {
        SFTPService sftpService = new SFTPService(activeServer.getServerIP(), activeServer.getPort());
        // to do, upload only changed slices.
        String srcFilePath = "~/" + userName + "/" + fileName;
        String dstFilePath = "~/" + userName + "/" + fileName;
        sftpService.uploadFile(srcFilePath, dstFilePath);
    }
    
    public void uploadDeletedFileToServer(String userName, String directory, String fileName, ActiveServer activeServer) throws JSchException, SftpException
    {
        SFTPService sftpService = new SFTPService(activeServer.getServerIP(), activeServer.getPort());
        String filePath = "~/" + userName + "/" + fileName;
        sftpService.deleteFile(filePath);
    }
    
    // to do
    public void uploadCreatedFileToClient(String userName, String directory, String fileName, ActiveClient activeClient) throws JSchException, SftpException
    {
        ClientAPI client = activeClient.getClientAPI();
        // client upload and download file from server
        String srcFilePath = "~/" + userName + "/" + fileName;
        String dstFilePath = "~/" + userName + "/" + fileName;
    }
    
    // to do
    public void uploadModifiedFileToClient(String userName, String directory, String fileName, ActiveClient activeClient, FileInfoDiff diff) throws JSchException, SftpException
    {
        ClientAPI client = activeClient.getClientAPI();
        // client upload and download file from server
        String srcFilePath = "~/" + userName + "/" + fileName;
        String dstFilePath = "~/" + userName + "/" + fileName;
    }
    
    // to do
    public void uploadDeletedFileToClient(String userName, String directory, String fileName, ActiveClient activeClient) throws JSchException, SftpException
    {
        ClientAPI client = activeClient.getClientAPI();
        // client upload and download file from server
        String srcFilePath = "~/" + userName + "/" + fileName;
        String dstFilePath = "~/" + userName + "/" + fileName;
    }
    
    /**
     *
     * @param userName
     * @param directory
     * @param fileName
     * @param activeServer
     * @throws RemoteException
     */
    public void updateFileInfoToServer(String userName, String directory, String fileName, ActiveServer activeServer, FileInfo fileInfo) throws RemoteException
    {
        // get fileinfo from server
        ServerAPI server = activeServer.getServerAPI();
        server.setFileInfoToServer(activeServer.getServerIP(), activeServer.getPort(), userName, directory, fileName, FileInfoService.fileInfoToXMLString(fileInfo));
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
    public void updateFileInfoToClient(String userName, String directory, String fileName, ActiveClient activeClient, FileInfo fileInfo) throws RemoteException, Exception
    {
        ClientAPI client = activeClient.getClientAPI();
        client.setFileInfoToClient(activeClient.getClientIP(), activeClient.getPort(), userName, directory, fileName, fileInfo.toString());
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
        // get fileinfo from current server
        FileInfo fileInfo = fileListHashtable.get(userName).getFileInfoByFileName(fileName);
        
        // get fileinfo from server
        ServerAPI server = activeServer.getServerAPI();
        String fileInfoStr = server.getFileInfoFromServer(activeServer.getServerIP(), activeServer.getPort(),
                userName, directory, fileName);
        if (fileInfoStr.isEmpty())
        {
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
    public void syncModifiedFileWithServer(String userName, String directory, String fileName, ActiveServer activeServer) throws RemoteException, JSchException, SftpException
    {
        // get fileinfo from current server
        FileInfo fileInfo = fileListHashtable.get(userName).getFileInfoByFileName(fileName);
        
        // get fileinfo from server
        ServerAPI server = activeServer.getServerAPI();
        String fileInfoStr = server.getFileInfoFromServer(activeServer.getServerIP(), activeServer.getPort(),
                userName, directory, fileName);
        FileInfoDiff diff = fileInfo.comparesToFileInfo(FileInfoService.parseXMLStringToFileInfo(fileInfoStr));
        
        if (diff.getFlag() == 1)
        {
            uploadModifiedFileToServer(userName, directory, fileName, activeServer, diff);
            updateFileInfoToServer(userName, directory, fileName, activeServer, fileInfo);
        }
    }
    
    public void syncDeletedFileWithServer(String userName, String directory, String fileName, ActiveServer activeServer) throws RemoteException, JSchException, SftpException
    {
        // get fileinfo from current server
        FileInfo fileInfo = fileListHashtable.get(userName).getFileInfoByFileName(fileName);
        
        // get fileinfo from server
        ServerAPI server = activeServer.getServerAPI();
        String fileInfoStr = server.getFileInfoFromServer(activeServer.getServerIP(), activeServer.getPort(),
                userName, directory, fileName);
        
        if (!fileInfoStr.isEmpty())
        {
            uploadDeletedFileToServer(userName, directory, fileName, activeServer);
            updateFileInfoToServer(userName, directory, fileName, activeServer, fileInfo);
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
    public void syncCreatedFileWithClient(String userName, String directory, String fileName, ActiveClient activeClient) throws RemoteException, JSchException, SftpException, Exception
    {
        // get fileinfo from current server
        FileInfo fileInfo = fileListHashtable.get(userName).getFileInfoByFileName(fileName);
        
        ClientAPI client = activeClient.getClientAPI();
        String fileInfoStr = client.getFileInfoFromClient(activeClient.getClientIP(), activeClient.getPort(),
                userName, directory, fileName);
        
        if (fileInfoStr.isEmpty())
        {
            uploadCreatedFileToClient(userName, directory, fileName, activeClient);
            updateFileInfoToClient(userName, directory, fileName, activeClient, fileInfo);
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
    public void syncModifiedFileWithClient(String userName, String directory, String fileName, ActiveClient activeClient) throws RemoteException, JSchException, SftpException, Exception
    {
        // get fileinfo from current server
        FileInfo fileInfo = fileListHashtable.get(userName).getFileInfoByFileName(fileName);
        
        // get fileinfo from client
        ClientAPI client = activeClient.getClientAPI();
        String fileInfoStr = client.getFileInfoFromClient(activeClient.getClientIP(), activeClient.getPort(),
                userName, directory, fileName);
        FileInfoDiff diff = fileInfo.comparesToFileInfo(FileInfoService.parseXMLStringToFileInfo(fileInfoStr));
        
        if (diff.getFlag() == 1)
        {
            uploadModifiedFileToClient(userName, directory, fileName, activeClient, diff);
            updateFileInfoToClient(userName, directory, fileName, activeClient, fileInfo);
        }
    }
    
    public void syncDeletedFileWithClient(String userName, String directory, String fileName, ActiveClient activeClient) throws RemoteException, JSchException, SftpException, Exception
    {
        // get fileinfo from current server
        FileInfo fileInfo = fileListHashtable.get(userName).getFileInfoByFileName(fileName);
        
        // get fileinfo from client
        ClientAPI client = activeClient.getClientAPI();
        String fileInfoStr = client.getFileInfoFromClient(activeClient.getClientIP(), activeClient.getPort(),
                userName, directory, fileName);
        FileInfoDiff diff = fileInfo.comparesToFileInfo(FileInfoService.parseXMLStringToFileInfo(fileInfoStr));
        
        if (diff.getFlag() == 1)
        {
            uploadModifiedFileToClient(userName, directory, fileName, activeClient, diff);
            updateFileInfoToClient(userName, directory, fileName, activeClient, fileInfo);
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
    public void syncCreatedFile(String userName, String directory, String fileName) throws JSchException, RemoteException, SftpException, Exception
    {
        // sync create file with active servers
        Vector<ActiveServer> activeServerList = ActiveServerListServer.getActiveServerList();
        for (ActiveServer activeServer : activeServerList)
        {
            if (!ServerStart.getServerIP().equals(activeServer.getServerIP()))
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
    public void syncModifiedFile(String userName, String directory, String fileName) throws JSchException, RemoteException, SftpException, Exception
    {
        // sync modified file with active servers
        Vector<ActiveServer> activeServerList = ActiveServerListServer.getActiveServerList();
        for (ActiveServer activeServer : activeServerList)
        {
            if (!ServerStart.getServerIP().equals(activeServer.getServerIP()))
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
    public void syncDeletedFile(String userName, String directory, String fileName) throws JSchException, RemoteException, SftpException, Exception
    {
        // sync deleted file with active servers
        Vector<ActiveServer> activeServerList = ActiveServerListServer.getActiveServerList();
        for (ActiveServer activeServer : activeServerList)
        {
            if (!ServerStart.getServerIP().equals(activeServer.getServerIP()))
                syncDeletedFileWithServer(userName, directory, fileName, activeServer);
        }
        
        // sync deleted file with active clients
        Vector<ActiveClient> activeClientList = ActiveClientListServer.getActiveClientList();
        for (ActiveClient activeClient : activeClientList)
        {
            if (activeClient.getClientID().equals(userName))
                syncDeletedFileWithClient(userName, directory, fileName, activeClient);
        }
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
        } catch (Exception ex) {
            Logger.getLogger(FileSyncServerService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
