package dblike.server;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import dblike.api.ClientAPI;
import dblike.api.ServerAPI;
import dblike.server.service.ClientListenerServer;
import dblike.server.service.FileListXMLService;
import dblike.server.service.FileSyncServerService;
import dblike.server.service.ServerListenerServer;
import dblike.server.service.SyncActionServer;
import dblike.service.InternetUtil;
import java.net.InetAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    private static String serverIP;
    private static int PORT;
    private static Registry registry;

    /**
     * @param aPORT the PORT to set
     */
    public static void setPORT(int aPORT) {
        PORT = aPORT;
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
     * @return the PORT
     */
    public static int getPORT() {
        return PORT;
    }

    /**
     * @return the registry
     */
    public static Registry getRegistry() {
        return registry;
    }

    /**
     * @param aRegistry the registry to set
     */
    public static void setRegistry(Registry aRegistry) {
        registry = aRegistry;
    }

    public static void main(String args[]) {
        try {
            FileListXMLService.loadServerInfo();
            ServerImp server = new ServerImp();
            ServerAPI serverStub = (ServerAPI) UnicastRemoteObject.exportObject(server, 0);
            System.out.println("----------");
            System.out.println(InternetUtil.getIPList());
            System.out.println("----------");
            System.out.println("Server start at " + getServerIP() + ":" + getPORT());
            setRegistry(LocateRegistry.createRegistry(getPORT()));
            String serverBind = "serverUtility";
            getRegistry().bind(serverBind, serverStub);
            System.out.println("Already bind: " + "[" + serverBind + "]");
            System.out.println("Server ready");
            //New thread to listen to heartbeat from all servers
            ServerListenerServer serverListener = new ServerListenerServer();
            Thread sLThread = new Thread(serverListener);
            sLThread.start();
            //New thread to listen to heartbeat from all clients
            ClientListenerServer clientListener = new ClientListenerServer();
            Thread cLThread = new Thread(clientListener);
            cLThread.start();
            //New thread to send heartbeat to others, broadcast
            SyncActionServer sync = new SyncActionServer();
            Thread syncThread = new Thread(sync);
            syncThread.start();

            // new thread to synchronize files
            String directory = "/home/ec2-user/users";
            boolean isRecursive = true;
            FileSyncServerService fileSyncServer = new FileSyncServerService(Paths.get(directory), isRecursive);
            Thread fileSyncServerThread = new Thread(fileSyncServer);
            fileSyncServerThread.start();

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
