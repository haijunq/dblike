package dblike.server;



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

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
            registry = LocateRegistry.createRegistry(PORT);
            registry.bind("Utility", util);
            System.out.println("Server ready");
        } catch (Exception e) {
        }
    }
}
