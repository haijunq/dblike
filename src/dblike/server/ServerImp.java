package dblike.server;

/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/
import dblike.api.ClientAPI;
import dblike.api.ServerAPI;
import dblike.server.service.ActiveClientListServer;
import dblike.server.service.ActiveServerListServer;
import dblike.server.service.FileListService;
import dblike.service.FileInfo;
import dblike.server.service.FileListXMLService;
import dblike.server.service.FileSyncServerService;
import dblike.server.service.UserListXMLReader;
import dblike.service.FileInfoService;
import java.io.IOException;
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

    /**
     * Constructor.
     */
    public ServerImp() {
        ClientList = ActiveClientListServer.getActiveClientList();
    }

    /**
     * Check whether the client is alive. 
     * @param clientID
     * @param deviceID
     * @return
     * @throws RemoteException 
     */
    @Override
    public int checkClientbyID(String clientID, String deviceID)
            throws RemoteException {
        return (ActiveClientListServer.checkClientbyID(clientID, deviceID));
    }

    /**
     * Call Client method, deprecated. 
     * @param clientID
     * @param deviceID
     * @param content
     * @throws RemoteException 
     */
    @Override
    public void callClient(String clientID, String deviceID, String content)
            throws RemoteException {
        // ActiveClient clientTemp = ActiveClientListServer.searchClientbyID(clientID, deviceID);
        // actClient(clientTemp, content);

    }

    /**
     * Add a client to the active client list.
     * @param clientID
     * @param deviceID
     * @param clientIP
     * @param clientPort
     * @return
     * @throws RemoteException 
     */
    @Override
    public boolean addClient(String clientID, String deviceID, String clientIP, int clientPort)
            throws RemoteException {
        return ActiveClientListServer.addClient(clientID, deviceID, clientIP, clientPort);
    }

    /**
     * Remove a client from the active client list.
     * @param clientID
     * @param deviceID
     * @return
     * @throws RemoteException 
     */
    @Override
    public boolean removeClient(String clientID, String deviceID)
            throws RemoteException {
        return ActiveClientListServer.removeClient(clientID, deviceID);
    }

    /**
     * Bind a client. 
     * @param bindParam
     * @param ip
     * @param port
     * @throws RemoteException 
     */
    @Override
    public void actClient(String bindParam, String ip, int port)
            throws RemoteException {
        lookup(bindParam, ip, port);
        client.actOnClient();
    }

    /**
     * Lookup registry and get the RMI stub.
     * @param bindParam
     * @param ip
     * @param port
     * @throws RemoteException 
     */
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

    /**
     * Check whether the server is alive on the list. 
     * @param serverIP
     * @param port
     * @return
     * @throws RemoteException 
     */
    @Override
    public int checkServerByIP_Port(String serverIP, int port) throws RemoteException {
        return ActiveServerListServer.checkServerByIP_Port(serverIP, port);
    }

    /**
     * Get heartbeat from the server. 
     * @param serverIP
     * @param port
     * @return
     * @throws RemoteException 
     */
    @Override
    public boolean beatFromServer(String serverIP, int port) throws RemoteException {
        return ActiveServerListServer.beatTheServer(serverIP, port);

    }

    /**
     * Get heartbeat from the client.
     * @param clientID
     * @param deviceID
     * @return
     * @throws RemoteException 
     */
    @Override
    public boolean beatFromClient(String clientID, String deviceID) throws RemoteException {
        return ActiveClientListServer.beatTheClient(clientID, deviceID);
    }
    
    /**
     * Check whether the server contains the fileInfo. 
     * @param serverIP
     * @param port
     * @param userName
     * @param directory
     * @param fileName
     * @return 
     */
    public boolean containFileInfoFromServer(String serverIP, int port, String userName, String directory, String fileName) {
        return FileSyncServerService.fileListHashtable.get(directory).containFileInfo(fileName);
    }
    
    /**
     * Get the fileInfo from a server. 
     * @param serverIP
     * @param port
     * @param userName
     * @param directory
     * @param fileName
     * @return 
     */
    public String getFileInfoFromServer(String serverIP, int port, String userName, String directory, String fileName) {
        return FileInfoService.fileInfoToXMLString(FileSyncServerService.fileListHashtable.get(directory).getFileInfo(fileName));
    }
    
    /**
     * Set the fileInfo to the server. 
     * @param serverIP
     * @param port
     * @param userName
     * @param directory
     * @param fileName
     * @param fileInfoStr 
     */
    public void setFileInfoToServer(String serverIP, int port, String userName, String directory, String fileName, String fileInfoStr) {
        FileInfo fileInfo = FileInfoService.parseXMLStringToFileInfo(fileInfoStr);
        if (FileSyncServerService.fileListHashtable.containsKey(directory))
        {
            FileSyncServerService.fileListHashtable.get(directory).addNewFileInfo(fileInfo);
        }
    }

    /**
     * Do the authentication. 
     * @param userID
     * @param hashedPassword
     * @return
     * @throws RemoteException 
     */
    @Override
    public boolean validateUser(String userID, String hashedPassword) throws RemoteException {
        return UserListXMLReader.isValidUser(userID, hashedPassword);
    }
    
    /**
     * Print out message, for testing.
     * @throws RemoteException 
     */
    public void printMsg() throws RemoteException {
        System.out.println("printMsg() in ServerImp!");
    }
    
    /**
     * Save filelist to the disk. 
     * @throws RemoteException 
     */
    public void saveFileListHashtable() throws RemoteException {
        try {
            FileSyncServerService.saveFileListHashtable();
        } catch (IOException ex) {
            Logger.getLogger(ServerImp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}