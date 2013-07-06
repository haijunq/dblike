package dblike.server;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import dblike.api.ClientAPI;
import dblike.api.ServerAPI;
import dblike.service.InternetUtil;
import java.net.InetAddress;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wenhanwu
 */
public class ServerStart {

    private static final int PORT = 1099;
    private static Registry registry;
    private static ArrayList<User> UserList = new ArrayList<User>();

    /**
     * @return the UserList
     */
    public static ArrayList<User> getUserList() {
        return UserList;
    }

    /**
     * @param aUserList the UserList to set
     */
    public static void setUserList(ArrayList<User> aUserList) {
        UserList = aUserList;
    }

    public static void main(String args[]) {
        try {
            ServerImp server = new ServerImp();
            ServerAPI serverStub = (ServerAPI) UnicastRemoteObject.exportObject(server, 0);
            System.out.println("----------");
            System.out.println(InternetUtil.getIPList());
            System.out.println("----------");
            System.out.println("Server start at " + InternetUtil.getMyIPInfo() + ":" + PORT);
            registry = LocateRegistry.createRegistry(PORT);
            String serverBind = "serverUtility";
            registry.bind(serverBind, serverStub);
            System.out.println("Already bind: " + "[" + serverBind + "]");
            System.out.println("Server ready");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
