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
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
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
public class FileSyncServerService extends WatchDirectoryService implements Runnable {
    
    public static Hashtable<String, FileListService> fileListHashtable;
    
    public FileSyncServerService(Path dir, boolean recursive) throws IOException {
        

        
        super(dir, recursive);
        
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("Before sync in server side");
        
        // load a list of users
        Hashtable<String, String> userListHashtable;
        userListHashtable = UserListXMLReader.getValidUserList();
        System.out.println("Loaded a user list, size: " + userListHashtable.size());
        for (Map.Entry<String, String> entry : userListHashtable.entrySet())
        {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
        
        // load file info from xml file on the server
        fileListHashtable = new Hashtable<String, FileListService>();
        for (Map.Entry<String, String> entry : userListHashtable.entrySet())
        {
            String userName = entry.getKey();
            System.out.println("Load user: " + userName);
            FileListService fileList = FileListXMLService.loadFileListFromXML("./users/" + userName + "/");
            fileListHashtable.put(fileList.getPathname(), fileList);
            System.out.println("User: " + userName + " filelist size: " + fileList.getFileHashTable().size());
            
            for (Map.Entry<String, FileInfo> tmpEntry : fileList.getFileHashTable().entrySet())
            {
                System.out.println("pathName: " + fileList.getPathname() + " fileName: " + tmpEntry.getKey());
            }
        }
        System.out.println("Loaded file info lists for each user.");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~");
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
        //        System.out.println("Func: uploadCreatedFileToServer");
        
        SFTPService sftpService = new SFTPService(activeServer.getServerIP());
        
        // to do, upload only changed slices.
        String srcFilePath = "./" + userName + "/" + fileName;
        String dstFilePath = "./" + userName + "/" + fileName;
        
        sftpService.uploadFile(srcFilePath, dstFilePath, this.getDir().toString(), this.getDir().toString());
        //        System.out.println("Func: uploadCreatedFileToServer done");
        
    }
    
    public void uploadModifiedFileToServer(String userName, String directory, String fileName, ActiveServer activeServer, FileInfoDiff diff) throws JSchException, SftpException
    {
        //        System.out.println("Func: uploadModifiedFileToServer");
        
        SFTPService sftpService = new SFTPService(activeServer.getServerIP());
        
        // to do, upload only changed slices.
        String srcFilePath = "./" + userName + "/" + fileName;
        String dstFilePath = "./" + userName + "/" + fileName;
        
        sftpService.uploadFile(srcFilePath, dstFilePath, this.getDir().toString(), this.getDir().toString());
        //        System.out.println("Func: uploadModifiedFileToServer done");
    }
    
    public void uploadDeletedFileToServer(String userName, String directory, String fileName, ActiveServer activeServer) throws JSchException, SftpException
    {
        //        System.out.println("Func: uploadDeletedFileToServer");
        
        SFTPService sftpService = new SFTPService(activeServer.getServerIP());
        
        // to do, upload delete file.
        String filePath = "./" + userName + "/" + fileName;
        sftpService.deleteFile(filePath, this.getDir().toString());
        
        //        System.out.println("Func: uploadDeletedFileToServer done");
    }
    
    // to do
    public void uploadCreatedFileToClient(String userName, String directory, String fileName, ActiveClient activeClient) throws JSchException, SftpException, RemoteException
    {
        ClientAPI client = activeClient.getClientAPI();
        client.printMsg();
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
        //        System.out.println("Func: updateFileInfoToServer");
        
        // get fileinfo from server
        ServerAPI server = activeServer.getServerAPI();
        server.setFileInfoToServer(activeServer.getServerIP(), activeServer.getPort(), userName, directory, fileName, FileInfoService.fileInfoToXMLString(fileInfo));
        //        System.out.println("Func: updateFileInfoToServer done");
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
        client.syncModifiedFileFromServer(activeClient.getClientIP(), activeClient.getPort(), userName, directory, fileName, FileInfoService.fileInfoToXMLString(fileInfo));
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
        FileInfo fileInfo = fileListHashtable.get(directory).getFileInfoByFileName(fileName);
        
        uploadCreatedFileToServer(userName, directory, fileName, activeServer);
        updateFileInfoToServer(userName, directory, fileName, activeServer, fileInfo);
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
        //        System.out.println("Func: syncModifiedFileWithServer");
        
        // get fileinfo from current server
        FileInfo fileInfo = fileListHashtable.get(directory).getFileInfoByFileName(fileName);
        //        System.out.println("fileinfo from current server: " + fileInfo.toString());
        
        // get fileinfo from server
        ServerAPI server = activeServer.getServerAPI();
        String fileInfoStr;
        FileInfoDiff diff;
        System.out.println(activeServer.getServerIP() + ":" + activeServer.getPort());
        if (server.containFileInfoFromServer(activeServer.getServerIP(), activeServer.getPort(), userName, directory, fileName))
        {
            fileInfoStr = server.getFileInfoFromServer(activeServer.getServerIP(), activeServer.getPort(), userName, directory, fileName);
            //            System.out.println("fileinfo from remote server: " + activeServer.getServerIP() + " :fileinfo " + fileInfoStr.toString());
            diff = fileInfo.comparesToFileInfo(FileInfoService.parseXMLStringToFileInfo(fileInfoStr));
            if (diff.getFlag() == 1)
            {
                uploadModifiedFileToServer(userName, directory, fileName, activeServer, diff);
                updateFileInfoToServer(userName, directory, fileName, activeServer, fileInfo);
            }
        }
        
        //        System.out.println("Func: syncModifiedFileWithServer done");
    }
    
    public void syncDeletedFileWithServer(String userName, String directory, String fileName, ActiveServer activeServer) throws RemoteException, JSchException, SftpException
    {
        // get fileinfo from current server
        FileInfo fileInfo = fileListHashtable.get(directory).getFileInfoByFileName(fileName);
        
        
        uploadDeletedFileToServer(userName, directory, fileName, activeServer);
        updateFileInfoToServer(userName, directory, fileName, activeServer, fileInfo);
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
        boolean isDebug = true;
        // get fileinfo from current server
        FileInfo fileInfo = fileListHashtable.get(directory).getFileInfoByFileName(fileName);
        // get fileinfo from client
        ClientAPI client = activeClient.getClientAPI();
        
        if (isDebug)
        {
            client.syncModifiedFileFromServer(activeClient.getClientIP(), activeClient.getPort(), userName, directory, fileName, FileInfoService.fileInfoToXMLString(fileInfo));
        }
        else
        {
            if (client.containFileInfo(activeClient.getClientIP(), activeClient.getPort(), userName, directory, fileName))
            {
                String fileInfoStrFromClient = client.getFileInfo(activeClient.getClientIP(), activeClient.getPort(), userName, directory, fileName);
                FileInfoDiff diff = fileInfo.comparesToFileInfo(FileInfoService.parseXMLStringToFileInfo(fileInfoStrFromClient));
                if (diff.getFlag() == 1)
                {
                    client.syncModifiedFileFromServer(activeClient.getClientIP(), activeClient.getPort(), userName, directory, fileName, FileInfoService.fileInfoToXMLString(fileInfo));
                    //                uploadModifiedFileToClient(userName, directory, fileName, activeClient, diff);
                    //                updateFileInfoToClient(userName, directory, fileName, activeClient, fileInfo);
                }
            }
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
        boolean isDebug = true;
        // get fileinfo from current server
        FileInfo fileInfo = fileListHashtable.get(directory).getFileInfoByFileName(fileName);
        // get fileinfo from client
        ClientAPI client = activeClient.getClientAPI();
        
        if (isDebug)
        {
            client.syncModifiedFileFromServer(activeClient.getClientIP(), activeClient.getPort(), userName, directory, fileName, FileInfoService.fileInfoToXMLString(fileInfo));
        }
        else
        {
            if (client.containFileInfo(activeClient.getClientIP(), activeClient.getPort(), userName, directory, fileName))
            {
                String fileInfoStrFromClient = client.getFileInfo(activeClient.getClientIP(), activeClient.getPort(), userName, directory, fileName);
                FileInfoDiff diff = fileInfo.comparesToFileInfo(FileInfoService.parseXMLStringToFileInfo(fileInfoStrFromClient));
                if (diff.getFlag() == 1)
                {
                    client.syncModifiedFileFromServer(activeClient.getClientIP(), activeClient.getPort(), userName, directory, fileName, FileInfoService.fileInfoToXMLString(fileInfo));
                    //                uploadModifiedFileToClient(userName, directory, fileName, activeClient, diff);
                    //                updateFileInfoToClient(userName, directory, fileName, activeClient, fileInfo);
                }
            }
        }
    }
    
    public void syncDeletedFileWithClient(String userName, String directory, String fileName, ActiveClient activeClient) throws RemoteException, JSchException, SftpException, Exception
    {
        boolean isDebug = true;
        // get fileinfo from current server
        FileInfo fileInfo = fileListHashtable.get(directory).getFileInfoByFileName(fileName);
        // get fileinfo from client
        ClientAPI client = activeClient.getClientAPI();
        
        if (isDebug)
        {
            client.syncModifiedFileFromServer(activeClient.getClientIP(), activeClient.getPort(), userName, directory, fileName, FileInfoService.fileInfoToXMLString(fileInfo));
        }
        else
        {
            if (client.containFileInfo(activeClient.getClientIP(), activeClient.getPort(), userName, directory, fileName))
            {
                String fileInfoStrFromClient = client.getFileInfo(activeClient.getClientIP(), activeClient.getPort(), userName, directory, fileName);
                FileInfoDiff diff = fileInfo.comparesToFileInfo(FileInfoService.parseXMLStringToFileInfo(fileInfoStrFromClient));
                if (diff.getFlag() == 1)
                {
                    client.syncDeletedFileFromServer(activeClient.getClientIP(), activeClient.getPort(), userName, directory, fileName, FileInfoService.fileInfoToXMLString(fileInfo));
                    //                uploadModifiedFileToClient(userName, directory, fileName, activeClient, diff);
                    //                updateFileInfoToClient(userName, directory, fileName, activeClient, fileInfo);
                }
            }
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
        System.out.println("Func: syncCreatedFile");
        
        // sync create file with active servers
        Vector<ActiveServer> activeServerList = ActiveServerListServer.getActiveServerList();
        for (ActiveServer activeServer : activeServerList)
        {
            if (!ServerStart.getServerIP().equals(activeServer.getServerIP()) && activeServer.isIsConnect() == 1)
                syncCreatedFileWithServer(userName, directory, fileName, activeServer);
        }
        
        // sync create file with active clients
        Vector<ActiveClient> activeClientList = ActiveClientListServer.getActiveClientList();
        for (ActiveClient activeClient : activeClientList)
        {
            if (activeClient.getClientID().equals(userName))
                syncCreatedFileWithClient(userName, directory, fileName, activeClient);
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
    public void syncModifiedFile(String userName, String directory, String fileName) throws JSchException, RemoteException, SftpException, Exception
    {
        System.out.println("\nFunc: syncModifiedFile");
        
        // sync modified file with active servers
        Vector<ActiveServer> activeServerList = ActiveServerListServer.getActiveServerList();
        for (ActiveServer activeServer : activeServerList)
        {
            if (!ServerStart.getServerIP().equals(activeServer.getServerIP()) && activeServer.isIsConnect() == 1)
                syncModifiedFileWithServer(userName, directory, fileName, activeServer);
        }
        
        // sync modified file with active clients
        Vector<ActiveClient> activeClientList = ActiveClientListServer.getActiveClientList();
        for (ActiveClient activeClient : activeClientList)
        {
            if (activeClient.getClientID().equals(userName))
                syncModifiedFileWithClient(userName, directory, fileName, activeClient);
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
    public void syncDeletedFile(String userName, String directory, String fileName) throws JSchException, RemoteException, SftpException, Exception
    {
        System.out.println("Func: syncDeletedFile");
        // sync deleted file with active servers
        Vector<ActiveServer> activeServerList = ActiveServerListServer.getActiveServerList();
        for (ActiveServer activeServer : activeServerList)
        {
            if (!ServerStart.getServerIP().equals(activeServer.getServerIP()) && activeServer.isIsConnect() == 1)
                syncDeletedFileWithServer(userName, directory, fileName, activeServer);
        }
        
        // sync deleted file with active clients
        Vector<ActiveClient> activeClientList = ActiveClientListServer.getActiveClientList();
        for (ActiveClient activeClient : activeClientList)
        {
            if (activeClient.getClientID().equals(userName))
                syncDeletedFileWithClient(userName, directory, fileName, activeClient);
        }
        System.out.println("Func: syncDeletedFile done");
    }
    
    /**
     *
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
            
            for (WatchEvent<?> event: key.pollEvents()) {
                
                WatchEvent.Kind kind = event.kind();
                
                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);
                
                String userName = child.getName(child.getNameCount()-2).toString();
                String directory = "./users/" + userName + "/";
                String fileName = child.getName(child.getNameCount()-1).toString();
                
                // print out event
                System.out.format("%s: %s\n", event.kind().name(), child);
                System.out.format("userName: %s, directory: %s, fileName: %s\n", userName, directory, fileName);
                
                Thread.sleep(3000);
                
                // TBD - provide example of how OVERFLOW event is handled
                if (kind == OVERFLOW) {
                    continue;
                }
                else if (kind == ENTRY_CREATE) {
                    syncCreatedFile(userName, directory, fileName);
                }
                else if (kind == ENTRY_MODIFY) {
                    syncModifiedFile(userName, directory, fileName);
                }
                else if (kind == ENTRY_DELETE) {
                    syncDeletedFile(userName, directory, fileName);
                }
                else {
                    System.out.println("ERROR: un-registered event!"); // never reach here!
                }
                
                
                
                // if directory is created, and watching recursively, then
                // register it and its sub-directories
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
                    //                    break;
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
