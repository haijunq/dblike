package dblike.server;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import dblike.api.ClientAPI;
import dblike.api.ServerAPI;
import dblike.server.service.ActiveServerListServer;
import dblike.server.service.ClientListenerServer;
import dblike.server.service.FileListXMLService;
import dblike.server.service.FileSyncServerService;
import dblike.server.service.ServerListenerServer;
import dblike.server.service.SyncActionServer;
import dblike.service.InternetUtil;
import dblike.service.SFTPService;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the main class for the server, server program will start from here.
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

    /**
     * Delete all the file in local server.
     * @param file
     * @throws IOException 
     */
    public static void delete(File file) throws IOException {

        if (file.isDirectory()) {

            //directory is empty, then delete it
            if (file.list().length == 0) {

                file.delete();
                System.out.println("Directory is deleted : "
                        + file.getAbsolutePath());

            } else {

                //list all the directory contents
                String files[] = file.list();

                for (String temp : files) {
                    //construct the file structure
                    File fileDelete = new File(file, temp);

                    //recursive delete
                    delete(fileDelete);
                }

                //check the directory again, if empty then delete it
                if (file.list().length == 0) {
                    file.delete();
                    System.out.println("Directory is deleted : "
                            + file.getAbsolutePath());
                }
            }

        } else {
            //if file, then delete it
            file.delete();
            System.out.println("File is deleted : " + file.getAbsolutePath());
        }
    }

    /**
     * Synchronize all the files and fileInfo with an online server. 
     * @param directory
     * @throws RemoteException
     * @throws JSchException
     * @throws SftpException 
     */
    public static void syncWithAServer(String directory) throws RemoteException, JSchException, SftpException {
        // sync with an active server (any one is fine)
        Vector<ActiveServer> activeServerList = ActiveServerListServer.getActiveServerList();
        System.out.println("active server list size: " + activeServerList.size());
        for (ActiveServer activeServer : activeServerList) {

            if (!ServerStart.getServerIP().equals(activeServer.getServerIP()) && activeServer.isIsConnect() == 1) {

                try {
                    activeServer.setRegistry(LocateRegistry.getRegistry(activeServer.getServerIP(), activeServer.getPort()));
                    activeServer.setServerAPI((ServerAPI) (activeServer.getRegistry()).lookup("serverUtility"));
                } catch (NotBoundException ex) {
                }
                try {

                    delete(new File(directory));

                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Saving xml filelist for server " + activeServer.getServerIP());
                ServerAPI serverAPI = activeServer.getServerAPI();
                System.out.println("Before serverAPI call.");
                serverAPI.saveFileListHashtable();
                System.out.println("downloading from server " + activeServer.getServerIP() + " on dir " + directory);



                SFTPService sftpService = new SFTPService(activeServer.getServerIP());
                sftpService.downloadDirectory(directory, directory, directory, directory);
                break;
            }
        }
    }

    /**
     * This is the main method, the entry for the server.
     *     
     * @param args
     */
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
            try {
                Thread.sleep(3 * 1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ClientListenerServer.class.getName()).log(Level.SEVERE, null, ex);
            }

            String directory = "/home/ec2-user/users";
            boolean isRecursive = true;

            syncWithAServer(directory);
            FileSyncServerService fileSyncServer = new FileSyncServerService(Paths.get(directory), isRecursive);
            Thread fileSyncServerThread = new Thread(fileSyncServer);
            fileSyncServerThread.start();

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}