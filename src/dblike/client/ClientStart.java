/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.client;

import dblike.api.ClientAPI;
import dblike.api.ServerAPI;
import dblike.service.InternetUtil;
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
    private static String clientIP;
    private static int clientPort = 7860;
    private static String serverIP = "23.23.129.199";
    private static int serverPort = 1099;

    public static void main(String args[]) {
        try {
            clientIP = InternetUtil.getMyIPInfo();
            //clientIP="";
            ClientImp client = new ClientImp();
            ClientAPI clientStub = (ClientAPI) UnicastRemoteObject.exportObject(client, 0);
            System.out.println("Client start at " + clientIP + ":" + clientPort);
            System.out.println("Will connect to server " + serverIP + ":" + serverPort);
            registry = LocateRegistry.createRegistry(clientPort);
            String clientBind = "clientUtility" + clientID + deviceID + clientIP + clientPort;
            registry.bind(clientBind, clientStub);
            System.out.println("Already bind: " + "[" + clientBind + "]");
            System.out.println("Client ready");
            Client aClient = new Client(clientID, deviceID, clientIP, clientPort, serverIP, serverPort);
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
