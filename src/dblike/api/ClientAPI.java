package dblike.api;



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */ 
import java.rmi.*;

/**
 *
 * @author wenhanwu
 */
public interface ClientAPI extends Remote {
 
    public void showMessage(String mmm) throws RemoteException;
}
