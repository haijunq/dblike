package dblike.api;



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */ 
import dblike.service.FileInfo;
import java.rmi.*;

/**
 * Client API. 
 * @author wenhanwu
 */
public interface ClientAPI extends Remote {
 
    public void printMsg() throws RemoteException;
    
    public void actOnClient() throws RemoteException;
    
    public boolean beatFromServer(String serverIP, int port) throws RemoteException;
    
    public boolean containFileInfo(String serverIP, int port, String userName, String directory, String fileName) throws Exception;
    
    public String getFileInfo(String serverIP, int port, String userName, String directory, String fileName) throws Exception;
    
    public String getFileInfoFromClient(String serverIP, int port, String userName, String directory, String fileName) throws Exception;  
    
    public void setFileInfoToClient(String serverIP, int port, String userName, String directory, String fileName, String fileInfoXML) throws Exception;  
    
    public void syncModifiedFileFromServer(String serverIP, int port, String userName, String directory, String fileName, String fileInfoXML) throws Exception;
    
    public void syncDeletedFileFromServer(String serverIP, int port, String userName, String directory, String fileName, String fileInfoXML) throws Exception;
}
