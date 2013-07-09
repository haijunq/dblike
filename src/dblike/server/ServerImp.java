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
import dblike.service.FileListXMLService;
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

    private ArrayList<ActiveClient> ClientList = null;
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
        ActiveClient clientTemp = ActiveClientListServer.searchClientbyID(clientID, deviceID);
        displayClient(clientTemp, content);

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
    public void displayClient(ActiveClient target, String message)
            throws RemoteException {
        lookup(target);
        client.showMessage(message);
    }

    public void lookup(ActiveClient target) throws RemoteException {
        registry = LocateRegistry.getRegistry(target.getClientIP(), target.getPort());
        try {
            String lookupClient = "clientUtility" + target.getClientID() + target.getDeviceID() + target.getClientIP() + target.getPort();
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
    
    public FileInfo getFileInfoFromServer(String serverIP, int port, String userName, String directory, String fileName) {
        FileListXMLService.loadFileListFromXML();
        return FileListXMLService.getFileInfo(userName, directory, fileName);
    }
}
