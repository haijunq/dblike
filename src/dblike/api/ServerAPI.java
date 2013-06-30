package dblike.api;



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import dblike.api.ClientAPI;
import java.rmi.*;

/**
 *
 * @author wenhanwu
 */
public interface ServerAPI extends Remote {

    public void addClient(ClientAPI client, String message) throws RemoteException;

    public void removeClient(ClientAPI client, String message) throws RemoteException;

    public void talk(ClientAPI client, String message) throws RemoteException;
}
