/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.client;

import dblike.api.ClientAPI;
import dblike.client.service.ActiveServerListClient;
import dblike.client.service.ClientConfig;
import dblike.server.service.FileListXMLService;
import dblike.service.FileInfo;
import dblike.service.FileInfoService;
import java.rmi.RemoteException;

/**
 *
 * @author wenhanwu
 */
public class ClientImp implements ClientAPI {
    
    @Override
    public void showMessage(String message) throws RemoteException {
        System.out.println(message); 
    }

    @Override
    public boolean beatFromServer(String serverIP, int port) throws RemoteException {
        return ActiveServerListClient.beatCurrentServer(serverIP, port);
    }
    
//    // to do
//    public FileInfo getFileInfoFromServer(String serverIP, int port, String userName, String directory, String fileName) {
//        return FileListXMLService.getFileInfo(userName, directory, fileName);
//    }
//    
//    // to do
//    public void setFileInfoToServer(String serverIP, int port, String userName, String directory, String fileName, FileInfo fileInfo) {
//        FileListXMLService.setFileInfo(userName, directory, fileName, fileInfo);
//    }
    
    public String getFileInfoFromClient(String serverIP, int port, String userName, String directory, String fileName) throws Exception{
        // to do 
        if (ClientConfig.getMyFileList().getFileHashTable().containsKey(fileName))
            return FileInfoService.fileInfoToXMLString(ClientConfig.getMyFileList().getFileHashTable().get(fileName));
        else 
            return FileInfoService.fileInfoToXMLString(new FileInfo());
    }
    
    public void setFileInfoToClient(String serverIP, int port, String userName, String directory, String fileName, String fileInfoXML) throws Exception{
         ClientConfig.getMyFileList().getFileHashTable().put(fileName, FileInfoService.parseXMLStringToFileInfo(fileInfoXML));
    }
    
    public void downloadFileFromServer(String serverIP, int port, String userName, String directory, String fileName, String fileInfoXML) throws Exception {
        
    }
    
    public void deleteClientFileByServer(String serverIP, int port, String userName, String directory, String fileName, String fileInfoXML) throws Exception {
        
    }
}
