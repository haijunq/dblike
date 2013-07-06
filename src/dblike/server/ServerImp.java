package dblike.server;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import dblike.api.ClientAPI;
import dblike.api.ServerAPI;
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
        ClientList = ServerStart.getActiveClientList();

    }

    @Override
    public ActiveClient searchClientbyID(String clientID, String deviceID)
            throws RemoteException {
        for (int i = 0; i < ClientList.size(); ++i) {
            if (ClientList.get(i).getClientID().equals(clientID) && ClientList.get(i).getDeviceID().equals(deviceID)) {
                return ClientList.get(i);
            }
        }
        return null;
    }

    @Override
    public void callClient(String clientID, String deviceID, String content)
            throws RemoteException {
        ActiveClient clientTemp = searchClientbyID(clientID, deviceID);
        displayClient(clientTemp, content);

    }

    @Override
    public void addClient(String clientID, String deviceID, String clientIP, int clientPort)
            throws RemoteException {
        System.out.println("Add " + clientID + " " + deviceID + " " + clientIP);
        if (searchClientbyID(clientID, clientIP) == null) {
            ClientList.add(new ActiveClient(clientID, deviceID, clientIP, clientPort));
        }
    }

    @Override
    public boolean removeClient(String clientID, String clientIP)
            throws RemoteException {
        boolean here = false;
        int i = 0;
        for (i = 0; i < ClientList.size(); i++) {
            if (ClientList.get(i).getClientID().equals(clientID)) {
                here = true;
                break;
            }
        }
        if (here) {
            ClientList.remove(ClientList.get(i));
            return true;
        }
        return false;
    }

    @Override
    public void displayClient(ActiveClient target, String message)
            throws RemoteException {
        registry = LocateRegistry.getRegistry(target.getClientIP(), target.getPort());
        try {
            String lookupClient = "clientUtility" + target.getClientID() + target.getDeviceID() + target.getClientIP() + target.getPort();
            client = (ClientAPI) registry.lookup(lookupClient);
        } catch (NotBoundException ex) {
            Logger.getLogger(ServerImp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AccessException ex) {
            Logger.getLogger(ServerImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        client.showMessage(message);
    }
}
