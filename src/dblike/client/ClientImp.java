/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.client;

import dblike.api.ClientAPI;
import dblike.client.service.ActiveServerListClient;
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
        return ActiveServerListClient.beatTheServer(serverIP, port);
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
    
    public String getFileInfoFromClient(String serverIP, int port, String userName, String directory, String fileName) {
        // to do 
        return FileInfoService.fileInfoToXMLString(new FileInfo());
    }
    
    public void setFileInfoToClient(String serverIP, int port, String userName, String directory, String fileName, FileInfo fileInfo) {
        
        // to do
    }
}
