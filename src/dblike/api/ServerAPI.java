package dblike.api;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import dblike.server.ActiveClient;
import dblike.server.service.FileListService;
import dblike.service.FileInfo;
import java.rmi.*;
import java.util.Hashtable;

/**
 *
 * @author wenhanwu
 */
public interface ServerAPI extends Remote {

    public boolean addClient(String clientID, String deviceID, String clientIP, int clientPort) throws RemoteException;

    public boolean removeClient(String clientID, String deviceID) throws RemoteException;

    public int checkClientbyID(String clientID, String deviceID) throws RemoteException;

    public int checkServerByIP_Port(String serverIP, int port) throws RemoteException;

    public void callClient(String clientID, String deviceID, String content) throws RemoteException;

    public void actClient(String bindParam, String ip, int port) throws RemoteException;

    public boolean beatFromServer(String serverIP, int port) throws RemoteException;

    public boolean beatFromClient(String clientID, String deviceID) throws RemoteException;

    public boolean containFileInfoFromServer(String serverIP, int port, String userName, String directory, String fileName) throws RemoteException;
            
    public String getFileInfoFromServer(String serverIP, int port, String userName, String directory, String fileName) throws RemoteException;

    public void setFileInfoToServer(String serverIP, int port, String userName, String directory, String fileName, String fileInfoXML) throws RemoteException;
 
//    public FileInfo getFileInfoFromClient(String serverIP, int port, String userName, String directory, String fileName) throws RemoteException;
//
//    public void setFileInfoToClient(String serverIP, int port, String userName, String directory, String fileName, FileInfo fileInfo) throws RemoteException;

    public boolean validateUser(String userID, String hashedPassword) throws RemoteException;
    
    public void printMsg() throws RemoteException;
    
    public FileListService getFileListServiceFromServer(String pathName) throws RemoteException;
    
    public Hashtable<String, FileListService> getFileListHashtableFromServer() throws RemoteException;
}
