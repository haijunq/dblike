package dblike.client;

import dblike.api.ServerAPI;
import dblike.client.service.ActiveServerListClient;
import dblike.client.service.ServerListenerClient;
import dblike.client.service.SyncActionClient;
import dblike.server.ActiveClient;
import java.rmi.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.*;
import java.awt.event.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author wenhanwu
 */
public class Client extends Frame implements ActionListener {

    private ServerAPI server = null;
    private TextField serverPath, userIDTF, passwordTF, noticeTF;
    private TextArea displayArea;
    private Button login, logout, act;
    private String userParam, userID, password;
    private int loginStatus;
    private static String host;
    private static Registry registry;
    private String clientID;
    private String deviceID;
    private String clientIP;
    private int clientPort;
    private String serverIP;
    private int serverPort;

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
     * @return the userID
     */
    public String getUserID() {
        return userID;
    }

    /**
     * @param userID the userID to set
     */
    public void setUserID(String userID) {
        this.userID = userID;
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

    public static void startThread(String clientID, String deviceID, String serverIP, int serverPort) {

        //New thread to listen to heartbeat from all servers
        ServerListenerClient serverListener = new ServerListenerClient();
        Thread sLThread = new Thread(serverListener);
        sLThread.start();
        //New thread to send heartbeat to others, broadcast
        SyncActionClient sync = new SyncActionClient();
        sync.setClientID(clientID);
        sync.setDeviceID(deviceID);
        sync.setServerIP(serverIP);
        sync.setServerPort(serverPort);
        Thread syncThread = new Thread(sync);
        syncThread.start();
    }

    public Client(String aClientID, String aDeviceID, String aClientIP, int aClientPort, String aServerIP, int aServerPort) {
        super(aClientID);
        try {
            this.clientID = aClientID;
            this.deviceID = aDeviceID;
            this.clientIP = aClientIP;
            this.clientPort = aClientPort;
            this.serverIP = aServerIP;
            this.serverPort = aServerPort;
            loginStatus = 0;
            this.setLayout(new FlowLayout(FlowLayout.LEFT));
            setBounds(100, 100, 500, 500);

            this.add(new Label("Name:"));
            userIDTF = new TextField(18);
            this.add(userIDTF);
            userIDTF.setText(clientID);
            userIDTF.setEnabled(false);

            this.add(new Label("Password:"));
            passwordTF = new TextField(18);
            this.add(passwordTF);
            passwordTF.setText("");

            this.add(new Label("Server: "));
            serverPath = new TextField(50);
            serverPath.setEnabled(false);
            this.add(serverPath);
            serverPath.setText("Local");


            noticeTF = new TextField(50);
            this.add(noticeTF);

            act = new Button("Act");
            act.addActionListener(this);
            this.add(act);

            displayArea = new TextArea(15, 60);
            this.add(displayArea);
            displayArea.setEditable(false);

            login = new Button("Login");
            login.addActionListener(this);
            this.add(login);

            logout = new Button("Logout");
            logout.addActionListener(this);
            this.add(logout);
            registry = LocateRegistry.getRegistry(serverIP, serverPort);
        } catch (RemoteException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.setVisible(true);
        lookup();
    }

    public void talk(String message) throws RemoteException {
        displayArea.append(message + "\n");
    }

    private void login() {
        try {
            setLoginStatus(1);
            setUserID(userIDTF.getText().trim());
            server.addClient(clientID, deviceID, clientIP, clientPort);
            ActiveServerListClient.addServer(serverIP, serverPort);
            startThread(clientID, deviceID, serverIP, serverPort);
        } catch (Exception e) {
        }
    }

    private void logout() {
        try {
            if (server == null) {
                return;
            }
            server.removeClient(getUserID(), deviceID);
            server = null;
        } catch (Exception e) {
        }
    }

    private void act() {
        try {
            server.callClient(clientID, deviceID, "Client!!!");
        } catch (RemoteException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void actionPerformed(ActionEvent action) {
        if (action.getSource().equals(login)) {
            login();
            if (getLoginStatus() == 1) {
                login.setEnabled(false);
                userIDTF.setEnabled(false);
            }
        } else if (action.getSource().equals(logout)) {
            this.logout();
            userIDTF.setEnabled(true);
            passwordTF.setEnabled(true);
            serverPath.setEnabled(true);
            login.setEnabled(true);
        } else if (action.getSource().equals(act)) {
            act();

        }
    }

    public void lookup() {
        try {
            server = (ServerAPI) registry.lookup("serverUtility");
        } catch (RemoteException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NotBoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
