package dblike.client;

import dblike.api.ServerAPI;
import dblike.client.service.ActiveServerListClient;
import dblike.client.service.ClientConfig;
import dblike.client.service.ServerListenerClient;
import dblike.client.service.SyncActionClient;
import dblike.server.service.ActiveServerListServer;
import dblike.server.service.FileListXMLService;
import dblike.service.InternetUtil;
import dblike.service.MD5Service;
import java.rmi.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

/**
 * This class contains the methods to initialize the client.
 *
 * @author wenhanwu
 */
public class Client {

    /**
     * @return the testFlag
     */
    public static int getTestFlag() {
        return testFlag;
    }

    /**
     * @param aTestFlag the testFlag to set
     */
    public static void setTestFlag(int aTestFlag) {
        testFlag = aTestFlag;
    }
    private ServerAPI server = null;
    private String userParam, password;
    private static int loginStatus;
    private static int testFlag = 0;
    private static Registry registry;
    private static String clientID;
    private static String deviceID;
    private static String clientIP;
    private static int clientPort;
    private static String serverIP;
    private static int serverPort;
    static Thread sLThread;
    static Thread syncThread;
    private static ServerListenerClient serverListener;
    private static SyncActionClient sync;
    private boolean alreadyAuthorized = false;

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

    /**
     * Pick up a new server. Be used when the client need to change a new
     * server.
     */
    public void pickupNewServer() {
//        System.out.println("in pickupNewServer");
        stopThread();
        while (true) {
            int availableServerIndex = ClientConfig.pickupAvailableServer();
            System.out.println("The new server is No." + availableServerIndex);
            if (availableServerIndex != -1) {
                ClientConfig.setCurrentServerIndex(availableServerIndex);
                serverIP = ClientConfig.getServerList().get(availableServerIndex).getServerIP();
                serverPort = ClientConfig.getServerList().get(availableServerIndex).getPort();
                login();
                initData();
                break;
            } else {
                System.out.println("There is no available server!!!");
            }
            try {
                Thread.sleep(InternetUtil.getCHANGESERVERINTERVAL() * 1000);
            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
        }
    }

    /**
     * This is used to input the name and password.
     */
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

    /**
     * This is to stop the listener thread and sync thread.
     */
    public static void stopThread() {
        serverListener.setRunningFlag(false);
        sync.setRunningFlag(false);
    }

    /**
     * This is to start the listener thread and sync thread.
     */
    public static void startThread(String clientID, String deviceID, String serverIP, int serverPort) {
        //New thread to listen to heartbeat from all servers
        serverListener = new ServerListenerClient();
        sLThread = new Thread(serverListener);
        sLThread.start();
        //New thread to send heartbeat to others, broadcast
        sync = new SyncActionClient();
        sync.setClientID(clientID);
        sync.setDeviceID(deviceID);
        sync.setServerIP(serverIP);
        sync.setServerPort(serverPort);
        syncThread = new Thread(sync);
        syncThread.start();
    }

    /**
     * Constructor, will use the server's IP and port.
     *
     * @param aServerIP
     * @param aServerPort
     */
    public Client(String aServerIP, int aServerPort) {
        this.serverIP = aServerIP;
        this.serverPort = aServerPort;
        loginStatus = 0;
    }

    /**
     * For testing
     *
     * @param message
     * @throws RemoteException
     */
    public void talk(String message) throws RemoteException {
        System.out.println(message + "\n");
    }

    /**
     * This is for login to the server, authentication.
     *
     * @return
     */
    public String login() {
        try {
            boolean flag = true;
            registry = LocateRegistry.getRegistry(serverIP, serverPort);
            lookup();
            if (!alreadyAuthorized) {
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
            }
        } catch (RemoteException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        alreadyAuthorized = true;
        return clientID;
    }

    /**
     * This is to initialize data and will call to start the threads.
     */
    public void initData() {
        try {
            setLoginStatus(1);
            ClientConfig.initCurrentClient(getClientID(), "", "", "", "");
            ClientConfig.loadCurrentUser();
            ClientConfig.setMyFileList(FileListXMLService.loadFileListFromXML("ClientCfg/users/" + ClientConfig.getCurrentClient().getClientID() + "/"));

            this.deviceID = ClientConfig.getCurrentClient().getDeviceID();
            this.clientIP = ClientConfig.getCurrentClient().getIp();
            this.clientPort = Integer.parseInt(ClientConfig.getCurrentClient().getPort());
            server.addClient(clientID, deviceID, clientIP, clientPort);
            //ActiveServerListClient.addServer(serverIP, serverPort);
            System.out.println("Connection built!");

            startThread(getClientID(), deviceID, serverIP, serverPort);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * This is to prompt the welcome information.
     */
    public void promptMessage() {
        System.out.println("--------------------------------------------------------------");
        System.out.println(" Hi, " + getClientID() + " on " + deviceID);
        System.out.println(" Client: " + clientIP + ":" + clientPort);
        System.out.println(" Server: " + serverIP + ":" + serverPort);
        System.out.println("--------------------------------------------------------------");

    }

    /**
     * This is for logout.
     */
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

    /**
     * This is to call the client, for testing.
     */
    private void act() {
        try {
            server.callClient(getClientID(), deviceID, "Client!!!");
        } catch (RemoteException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This is to lookup to prepare to call server.
     */
    public void lookup() {
        try {
            server = (ServerAPI) registry.lookup("serverUtility");
        } catch (RemoteException ex) {
            System.out.println(ex);
        } catch (NotBoundException ex) {
            System.out.println(ex);
        }
    }
}
