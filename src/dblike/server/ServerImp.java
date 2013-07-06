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

    private ArrayList<User> ClientList = null;
    private ClientAPI client = null;
    private Registry registry;

    public ServerImp() {
        ClientList = ServerStart.getUserList();

    }

    @Override
    public User searchClientbyID(String clientID, String deviceID)
            throws RemoteException {
        for (int i = 0; i < ClientList.size(); ++i) {
            if (ClientList.get(i).getUserID().equals(clientID) && ClientList.get(i).getDeviceID().equals(deviceID)) {
                return ClientList.get(i);
            }
        }
        return null;
    }

    @Override
    public void callClient(String clientID, String deviceID, String content)
            throws RemoteException {
        User userTemp = searchClientbyID(clientID, deviceID);
        displayClient(userTemp, content);

    }

    @Override
    public void addClient(String clientID, String deviceID, String clientIP, int clientPort)
            throws RemoteException {
        System.out.println("Add " + clientID + " " + deviceID + " " + clientIP);
        if (searchClientbyID(clientID, clientIP) == null) {
            for (int i = 0; i < ClientList.size(); i++) {
                displayClient(ClientList.get(i), "Add " + clientID + " " + deviceID + " " + clientIP);
            }
            ClientList.add(new User(clientID, deviceID, clientIP, clientPort));
        }
    }

    @Override
    public boolean removeClient(String clientID, String clientIP)
            throws RemoteException {
        boolean here = false;
        int i = 0;
        for (i = 0; i < ClientList.size(); i++) {
            if (ClientList.get(i).getUserID().equals(clientID)) {
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
    public void displayClient(User target, String message)
            throws RemoteException {
        registry = LocateRegistry.getRegistry(target.getUserIP(), target.getPort());
        try {
            String lookupClient = "clientUtility" + target.getUserID() + target.getDeviceID() + target.getUserIP() + target.getPort();
            client = (ClientAPI) registry.lookup(lookupClient);
        } catch (NotBoundException ex) {
            Logger.getLogger(ServerImp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AccessException ex) {
            Logger.getLogger(ServerImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        client.showMessage(message);
    }
}
