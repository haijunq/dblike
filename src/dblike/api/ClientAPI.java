package dblike.api;



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import dblike.api.ServerAPI;
import java.rmi.*;

/**
 *
 * @author wenhanwu
 */
public interface ClientAPI extends Remote {

    public String getUserID() throws RemoteException;

    public void talk(String message) throws RemoteException;
}