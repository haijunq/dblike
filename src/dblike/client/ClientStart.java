/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.client;

import dblike.api.ClientAPI;
import dblike.api.ServerAPI;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wenhanwu
 */
public class ClientStart {

    private static Registry registry;
    private static String clientID = "001";
    private static String deviceID = "iphone";
    private static String clientIP = "127.0.0.1";
    private static int clientPort = 1098;
    private static String serverIP = "127.0.0.1";
    private static int serverPort = 1099;

    public static void main(String args[]) {
        try {  
            ClientImp client = new ClientImp();
            ClientAPI clientStub = (ClientAPI) UnicastRemoteObject.exportObject(client, 0);
            System.out.println("Client start at "+clientIP+":"+clientPort);
            System.out.println("Will connect to server "+serverIP+":"+serverPort);
            registry = LocateRegistry.createRegistry(clientPort);
            String clientBind="clientUtility" + clientID + deviceID + clientIP + clientPort;
            registry.bind(clientBind, clientStub);
            System.out.println("Yijing bind: "+clientBind);
            System.out.println("Client ready");
            Client clientaaa = new Client(clientID , deviceID,clientIP ,clientPort, serverIP, serverPort);
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
