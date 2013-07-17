package dblike.client;

import dblike.api.ServerAPI;
import dblike.client.service.ActiveServerListClient;
import dblike.client.service.ClientConfig;
import dblike.client.service.ServerListenerClient;
import dblike.client.service.SyncActionClient;
import dblike.service.MD5Service;
import java.rmi.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

/**
 *
 * @author wenhanwu
 */
public class Client {

    private ServerAPI server = null;
    private String userParam, password;
    private int loginStatus;
    private static String host;
    private static Registry registry;
    private String clientID;
    private String deviceID;
    private String clientIP;
    private int clientPort;
    private String serverIP;
    private int serverPort;
    static Thread sLThread;
    static Thread syncThread;

    /**
     * @return the clientID
     */
    public String getClientID() {
        return clientID;
    }

    public ServerAPI getServer() {
        return server;
    }

    
    /**
     * @param clientID the clientID to set
     */
    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    /**
     * @return the userParam
     */
    public String getUserParam() {
        return userParam;
    }

    /**
     * @param userParam the userParam to set
     */
    public void setUserParam(String userParam) {
        this.userParam = userParam;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the loginStatus
     */
    public int getLoginStatus() {
        return loginStatus;
    }

    /**
     * @param loginStatus the loginStatus to set
     */
    public void setLoginStatus(int loginStatus) {
        this.loginStatus = loginStatus;
    }

    public void inputNamePassword() {
        Scanner scanUN = new Scanner(System.in);
        System.out.println("Username:");
        do {
            String userInput = scanUN.nextLine();
            try {
                setClientID(userInput);
                if (getClientID().equals("")) {
                    System.out.println("Username cannot be null.");
                } else {
                    break;
                }
            } catch (Exception e) {
                System.err.println("Client exception: " + e.toString());
                e.printStackTrace();
            }
        } while (true);
        System.out.println("Password:");
        Scanner scanPW = new Scanner(System.in);
        setPassword(scanPW.nextLine());
    }

    public static void startThread(String clientID, String deviceID, String serverIP, int serverPort) {
        //New thread to listen to heartbeat from all servers
        ServerListenerClient serverListener = new ServerListenerClient();
        sLThread = new Thread(serverListener);
        sLThread.start();
        //New thread to send heartbeat to others, broadcast
        SyncActionClient sync = new SyncActionClient();
        sync.setClientID(clientID);
        sync.setDeviceID(deviceID);
        sync.setServerIP(serverIP);
        sync.setServerPort(serverPort);
        syncThread = new Thread(sync);
        syncThread.start();
    }

    public Client(String aServerIP, int aServerPort) {
        this.serverIP = aServerIP;
        this.serverPort = aServerPort;
        loginStatus = 0;
    }

    public void talk(String message) throws RemoteException {
        System.out.println(message + "\n");
    }

    public String login() {
        try {
            boolean flag = true;
            registry = LocateRegistry.getRegistry(serverIP, serverPort);
            lookup();
            while (flag) {
                try {
                    inputNamePassword();
                    if (server.validateUser(getClientID(), MD5Service.getMD5FromString(password))) {
                        flag = false;
                        System.out.println("Login Successfully");
                    } else {
                        System.out.println("Not a validate user, input again!");
                    }
                } catch (RemoteException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (RemoteException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return clientID;
    }

    public void initData() {
        try {
            setLoginStatus(1);
            ClientConfig.initCurrentClient(getClientID(), "", "", "", "");
            ClientConfig.loadCurrentUser();

            this.deviceID = ClientConfig.getCurrentClient().getDeviceID();
            this.clientIP = ClientConfig.getCurrentClient().getIp();
            this.clientPort = Integer.parseInt(ClientConfig.getCurrentClient().getPort());

            server.addClient(clientID, deviceID, clientIP, clientPort);
            System.out.println("Client already added on server!");
            ActiveServerListClient.addServer(serverIP, serverPort);
            System.out.println("Server loaded!");

            startThread(getClientID(), deviceID, serverIP, serverPort);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void promptMessage() {
        System.out.println("--------------------------------------------------------------");
        System.out.println(" Hi, " + getClientID() + " on " + deviceID);
        System.out.println(" Client: " + clientIP + ":" + clientPort);
        System.out.println(" Server: " + serverIP + ":" + serverPort);
        System.out.println("--------------------------------------------------------------");

    }

    private void logout() {
        try {
            if (server == null) {
                return;
            }
            server.removeClient(getClientID(), deviceID);
            server = null;
        } catch (Exception e) {
        }
    }

    private void act() {
        try {
            server.callClient(getClientID(), deviceID, "Client!!!");
        } catch (RemoteException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void lookup() {
        try {
            System.out.println("registry is; -->" + registry);
            System.out.println("before lookup");
            server = (ServerAPI) registry.lookup("serverUtility");
            System.out.println("after lookup");
            System.out.println("server is; -->" + server);
        } catch (RemoteException ex) {
            System.out.println(ex);
        } catch (NotBoundException ex) {
            System.out.println(ex);
        }
    }
}
