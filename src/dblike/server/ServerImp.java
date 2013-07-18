package dblike.server;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import dblike.api.ClientAPI;
import dblike.api.ServerAPI;
import dblike.server.service.ActiveClientListServer;
import dblike.server.service.ActiveServerListServer;
import dblike.service.FileInfo;
import dblike.server.service.FileListXMLService;
import dblike.server.service.FileSyncServerService;
import dblike.server.service.UserListXMLReader;
import dblike.service.FileInfoService;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wenhanwu
 */
public class ServerImp implements ServerAPI {

    private Vector<ActiveClient> ClientList = null;
    private ClientAPI client = null;
    private Registry registry;

    public ServerImp() {
        ClientList = ActiveClientListServer.getActiveClientList();
    }

    @Override
    public int checkClientbyID(String clientID, String deviceID)
            throws RemoteException {
        return (ActiveClientListServer.checkClientbyID(clientID, deviceID));
    }

    @Override
    public void callClient(String clientID, String deviceID, String content)
            throws RemoteException {
//        ActiveClient clientTemp = ActiveClientListServer.searchClientbyID(clientID, deviceID);
//        actClient(clientTemp, content);

    }

    @Override
    public boolean addClient(String clientID, String deviceID, String clientIP, int clientPort)
            throws RemoteException {
        return ActiveClientListServer.addClient(clientID, deviceID, clientIP, clientPort);
    }

    @Override
    public boolean removeClient(String clientID, String deviceID)
            throws RemoteException {
        return ActiveClientListServer.removeClient(clientID, deviceID);
    }

    @Override
    public void actClient(String bindParam, String ip, int port)
            throws RemoteException {
        lookup(bindParam, ip, port);
        client.actOnClient();
    }

    public void lookup(String bindParam, String ip, int port) throws RemoteException {
        registry = LocateRegistry.getRegistry(ip, port);
        try {
            String lookupClient = bindParam;
            client = (ClientAPI) registry.lookup(lookupClient);
        } catch (NotBoundException ex) {
            Logger.getLogger(ServerImp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AccessException ex) {
            Logger.getLogger(ServerImp.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public int checkServerByIP_Port(String serverIP, int port) throws RemoteException {
        return ActiveServerListServer.checkServerByIP_Port(serverIP, port);
    }

    @Override
    public boolean beatFromServer(String serverIP, int port) throws RemoteException {
        return ActiveServerListServer.beatTheServer(serverIP, port);

    }

    @Override
    public boolean beatFromClient(String clientID, String deviceID) throws RemoteException {
        return ActiveClientListServer.beatTheClient(clientID, deviceID);
    }
    
    public boolean containFileInfoFromServer(String serverIP, int port, String userName, String directory, String fileName) {
        return FileSyncServerService.fileListHashtable.get(directory).containFileInfo(fileName);
    }
    
    public String getFileInfoFromServer(String serverIP, int port, String userName, String directory, String fileName) {
        return FileInfoService.fileInfoToXMLString(FileSyncServerService.fileListHashtable.get(directory).getFileInfo(fileName));
    }
    

    public void setFileInfoToServer(String serverIP, int port, String userName, String directory, String fileName, String fileInfoStr) {
        
        FileInfo fileInfo = FileInfoService.parseXMLStringToFileInfo(fileInfoStr);
        FileSyncServerService.fileListHashtable.get(directory).addNewFileInfo(fileInfo);

    }
    
//    // to do 
//    public FileInfo getFileInfoFromClient(String serverIP, int port, String userName, String directory, String fileName) {
//        return FileListXMLService.getFileInfo(userName, directory, fileName);
//    }
//    
//    // to do
//    public void setFileInfoToClient(String serverIP, int port, String userName, String directory, String fileName, FileInfo fileInfo) {
//        FileListXMLService.setFileInfo(userName, directory, fileName, fileInfo);
//    } 
    
    @Override
    public boolean validateUser(String userID, String hashedPassword) throws RemoteException {
        return UserListXMLReader.isValidUser(userID, hashedPassword);
    } 
}
