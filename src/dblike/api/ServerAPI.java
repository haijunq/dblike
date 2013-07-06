package dblike.api;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import dblike.server.User;
import java.rmi.*;

/**
 *
 * @author wenhanwu
 */
public interface ServerAPI extends Remote {

    public void addClient(String clientID, String deviceID, String clientIP, int clientPort) throws RemoteException;

    public boolean removeClient(String userID, String userIP) throws RemoteException;

    public User searchClientbyID(String userID, String userIP) throws RemoteException;

    public void callClient(String userID, String userIP, String content) throws RemoteException;

    public void displayClient(User target, String message) throws RemoteException;
}
