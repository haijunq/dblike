/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.client;

import dblike.api.ClientAPI;
import dblike.client.service.ServerListenerClient;
import dblike.client.service.SyncActionClient;
import dblike.service.InternetUtil;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author wenhanwu
 */
public class ClientStart {

    private static Registry registry;
    private static String clientID = "001";
    private static String deviceID = "iphone";
    private static String clientIP = "127.0.0.1";
    private static int clientPort = 7860;
    //private static String serverIP = "23.23.129.199";
    private static String serverIP = "127.0.0.1";
    private static int serverPort = 1099;

    /**
     * @return the clientID
     */
    public static String getClientID() {
        return clientID;
    }

    /**
     * @param aClientID the clientID to set
     */
    public static void setClientID(String aClientID) {
        clientID = aClientID;
    }

    /**
     * @return the deviceID
     */
    public static String getDeviceID() {
        return deviceID;
    }

    /**
     * @param aDeviceID the deviceID to set
     */
    public static void setDeviceID(String aDeviceID) {
        deviceID = aDeviceID;
    }

    /**
     * @return the serverIP
     */
    public static String getServerIP() {
        return serverIP;
    }

    /**
     * @param aServerIP the serverIP to set
     */
    public static void setServerIP(String aServerIP) {
        serverIP = aServerIP;
    }

    /**
     * @return the serverPort
     */
    public static int getServerPort() {
        return serverPort;
    }

    /**
     * @param aServerPort the serverPort to set
     */
    public static void setServerPort(int aServerPort) {
        serverPort = aServerPort;
    }

    public static void main(String args[]) {
        try {
            ClientImp client = new ClientImp();
            ClientAPI clientStub = (ClientAPI) UnicastRemoteObject.exportObject(client, 0);
            System.out.println("Client start at " + clientIP + ":" + clientPort);
            System.out.println("Will connect to server " + getServerIP() + ":" + getServerPort());
            registry = LocateRegistry.createRegistry(clientPort);
            String clientBind = "clientUtility" + getClientID() + getDeviceID() + clientIP + clientPort;
            registry.bind(clientBind, clientStub);
            System.out.println("Already bind: " + "[" + clientBind + "]");
            System.out.println("Client ready");
            Client aClient = new Client(getClientID(), getDeviceID(), clientIP, clientPort, getServerIP(), getServerPort());

        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
