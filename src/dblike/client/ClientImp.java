/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.client;

import dblike.api.ClientAPI;
import dblike.client.service.ActiveServerListClient;
import dblike.client.service.ClientConfig;
import dblike.client.service.FileSyncClientService;
import dblike.server.service.FileListXMLService;
import dblike.service.FileInfo;
import dblike.service.FileInfoService;
import java.rmi.RemoteException;

/**
 * Implementation of the Client API.
 * @author wenhanwu
 */
public class ClientImp implements ClientAPI {
    
    @Override
    public void actOnClient() throws RemoteException {
        Client.setTestFlag(1);
    }

    @Override
    public boolean beatFromServer(String serverIP, int port) throws RemoteException {
        return ActiveServerListClient.beatCurrentServer(serverIP, port);
    }
    
    /**
     * Check whether a server contains a fileInfo. 
     * @param serverIP
     * @param port
     * @param userName
     * @param directory
     * @param fileName
     * @return
     * @throws Exception 
     */
    public boolean containFileInfo(String serverIP, int port, String userName, String directory, String fileName) throws Exception {
        return ClientConfig.getMyFileList().getFileHashTable().containsKey(fileName);
    }
   
    /**
     * Get the fileInfo from Server. 
     * @param serverIP
     * @param port
     * @param userName
     * @param directory
     * @param fileName
     * @return 
     */
    public String getFileInfo(String serverIP, int port, String userName, String directory, String fileName) {
        return FileInfoService.fileInfoToXMLString(ClientConfig.getMyFileList().getFileHashTable().get(fileName));
    }
            
    /**
     * Get fileInfo from Client.
     * @param serverIP
     * @param port
     * @param userName
     * @param directory
     * @param fileName
     * @return
     * @throws Exception 
     */
    public String getFileInfoFromClient(String serverIP, int port, String userName, String directory, String fileName) throws Exception{
        if (ClientConfig.getMyFileList().getFileHashTable().containsKey(fileName))
            return FileInfoService.fileInfoToXMLString(ClientConfig.getMyFileList().getFileHashTable().get(fileName));
        else {
            FileInfo newFileInfo = FileInfoService.getFileInfoByFileName(ClientConfig.getCurrentClient().getFolderPath(), fileName);
            newFileInfo.setVersion(0);
            newFileInfo.setDeviceID(ClientConfig.getCurrentClient().getDeviceID());
            return FileInfoService.fileInfoToXMLString(newFileInfo);
        }
    }
    
    /**
     * Set the fileInfo to Client.
     * @param serverIP
     * @param port
     * @param userName
     * @param directory
     * @param fileName
     * @param fileInfoXML
     * @throws Exception 
     */
    public void setFileInfoToClient(String serverIP, int port, String userName, String directory, String fileName, String fileInfoXML) throws Exception{
         ClientConfig.getMyFileList().updateFileInfo(FileInfoService.parseXMLStringToFileInfo(fileInfoXML));
    }
    
    /**
     * Synchronize the modified file from Server.
     * @param serverIP
     * @param port
     * @param userName
     * @param directory
     * @param fileName
     * @param fileInfoXML
     * @throws Exception 
     */
    public synchronized void syncModifiedFileFromServer(String serverIP, int port, String userName, String directory, String fileName, String fileInfoXML) throws Exception {
//        System.out.println("Func: syncModifiedFileFromServer");
        FileSyncClientService.syncCreatedFileFromServer(userName, directory, fileName);
    }
    
    /**
     * Synchronize the deleted file from server. 
     * @param serverIP
     * @param port
     * @param userName
     * @param directory
     * @param fileName
     * @param fileInfoXML
     * @throws Exception 
     */
    public void syncDeletedFileFromServer(String serverIP, int port, String userName, String directory, String fileName, String fileInfoXML) throws Exception {
        FileSyncClientService.syncDeletedFileFromServer(userName, directory, fileName);
        
    }
    
    /**
     * Test method.
     * @throws RemoteException 
     */
    public void printMsg() throws RemoteException {
        System.out.println("printMsg() from ClientAPI!");
    }
}
