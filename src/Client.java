

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.rmi.*;
import java.rmi.server.*;
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
public class Client extends Frame implements ClientAPI, ActionListener {

    private ServerAPI server = null;
    private TextField serverPath, userIDTF, passwordTF, noticeTF;
    private TextArea displayArea;
    private Button login, logout, act;
    private String userParam, userID, password;
    private int loginStatus;
    private static String host;
    private static Registry registry;

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

    public Client(String name) {
        super(name);
        loginStatus = 0;
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        setBounds(100, 100, 500, 500);

        this.add(new Label("Name:"));
        userIDTF = new TextField(18);
        this.add(userIDTF);
        userIDTF.setText("");

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
        
        try {
            UnicastRemoteObject.exportObject(this);
        } catch (RemoteException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent arg0) {
                System.exit(1);
            }
        });
        this.setVisible(true);
    }

    @Override
    public void talk(String message) throws RemoteException {
        displayArea.append(message + "\n");
    }
    private void login() {
        try {
            server = (ServerAPI) registry.lookup("Utility");
            setLoginStatus(1);
            setUserID(userIDTF.getText().trim());
            server.addClient(this, getUserID() + " logined!!!");
        } catch (Exception e) {
        }
    }

    private void logout() {
        try {
            if (server == null) {
                return;
            }
            server.removeClient(this, getUserID() + " logouted!!!");
            server = null;
        } catch (Exception e) {
        }
    }

    @Override
    public void actionPerformed(ActionEvent action) {
        if (action.getSource() == login) {
            login();
            if (getLoginStatus() == 1) {
                login.setEnabled(false);
                userIDTF.setEnabled(false);
            }
        } else if (action.getSource() == logout) {
            this.logout();
            userIDTF.setEnabled(true);
            passwordTF.setEnabled(true);
            serverPath.setEnabled(true);
            login.setEnabled(true);
        } else if (action.getSource() == act) {
            try {
                server.talk(this, noticeTF.getText());
            } catch (RemoteException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
            noticeTF.setText("");
        }
    }


    public static void main(String args[]) {
        host = (args.length < 1) ? null : args[0];
        host="localhost";
        try {
            registry = LocateRegistry.getRegistry(host);
        } catch (RemoteException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        Client oneClient = new Client("DBlikeClient");
    }
}
