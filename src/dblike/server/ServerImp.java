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
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of ServerAPI.
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

    @Override
    public int checkClientbyID(String clientID, String deviceID)
            throws RemoteException {
        return (ActiveClientListServer.checkClientbyID(clientID, deviceID));
    }

    @Override
    /**
     * Deprecated.
     */
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

    /**
     * Lookup the registry stub.
     *
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

    @Override
    /**
     * Get server index by its port.
     */
    public int checkServerByIP_Port(String serverIP, int port) throws RemoteException {
        return ActiveServerListServer.checkServerByIP_Port(serverIP, port);
    }

    @Override
    /**
     * Heartbeat message from server.
     */
    public boolean beatFromServer(String serverIP, int port) throws RemoteException {
        return ActiveServerListServer.beatTheServer(serverIP, port);

    }

    @Override
    /**
     * Heartbeat message from client.
     */
    public boolean beatFromClient(String clientID, String deviceID) throws RemoteException {
        return ActiveClientListServer.beatTheClient(clientID, deviceID);
    }

    /**
     * Check whether the fileInfo is contained in server.
     *
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
     * Get the fileInfo XML string from server.
     *
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
     * Set the fileInfo to server.
     *
     * @param serverIP
     * @param port
     * @param userName
     * @param directory
     * @param fileName
     * @param fileInfoStr
     */
    public void setFileInfoToServer(String serverIP, int port, String userName, String directory, String fileName, String fileInfoStr) {
        FileInfo fileInfo = FileInfoService.parseXMLStringToFileInfo(fileInfoStr);
        if (FileSyncServerService.fileListHashtable.containsKey(directory)) {
            FileSyncServerService.fileListHashtable.get(directory).addNewFileInfo(fileInfo);
        }
    }

    @Override
    /**
     * User authentication.
     */
    public boolean validateUser(String userID, String hashedPassword) throws RemoteException {
        return UserListXMLReader.isValidUser(userID, hashedPassword);
    }

    /**
     * For test.
     *
     * @throws RemoteException
     */
    public void printMsg() throws RemoteException {
        System.out.println("printMsg() in ServerImp!");
    }
}
