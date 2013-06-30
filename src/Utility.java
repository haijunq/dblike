

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

/**
 *
 * @author wenhanwu
 */
public class Utility extends UnicastRemoteObject implements ServerAPI {

    private List<ClientAPI> ClientList = null;
    private static final long serialVersionUID = 1L;

    protected Utility() throws RemoteException {
        super();
        ClientList = new ArrayList<ClientAPI>();
    }

    @Override
    public void addClient(ClientAPI client, String message)
            throws RemoteException {
        if (!ClientList.contains(client)) {
            for (int i = 0; i < ClientList.size(); i++) {
                talk((ClientAPI) ClientList.get(i), message);
            }
            ClientList.add(client);
        }
        System.out.println("Client " + client.getUserID() + " came in!!!");
    }

    @Override
    public void removeClient(ClientAPI client, String message)
            throws RemoteException {
        if (ClientList.contains(client)) {
            for (int i = 0; i < ClientList.size(); i++) {
                talk((ClientAPI) ClientList.get(i), message);
            }
            ClientList.remove(client);
        }
        System.out.println("Client " + client.getUserID() + " left!!!");
    }

    @Override
    public void talk(ClientAPI client, String message)
            throws RemoteException {
        if (!ClientList.contains(client)) {
            return;
        }
        for (int i = 0; i < ClientList.size(); i++) {
            String User = client.getUserID();
            ((ClientAPI) ClientList.get(i)).talk(User + "--->" + message);
        }
    }
}
