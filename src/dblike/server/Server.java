package dblike.server;



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import dblike.api.ServerAPI;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author wenhanwu
 */
public class Server {

    private static final int PORT = 1099;
    private static Registry registry;

    public static void main(String args[]) {
        try {
            Utility util = new Utility();
            System.out.println("Server start");
            UnicastRemoteObject.unexportObject(util, true);
            ServerAPI serverAPI = (ServerAPI) UnicastRemoteObject.exportObject(util, 0);
            registry = LocateRegistry.createRegistry(PORT);
            registry.bind("Utility", serverAPI);
            System.out.println("Server ready");
//            util.talk(null, null);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
