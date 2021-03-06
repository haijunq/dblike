/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.client;

import dblike.server.*;
import dblike.api.ClientAPI;
import dblike.service.InternetUtil;
import java.rmi.registry.Registry;

/**
 * Define a user, deprecated. 
 * @author wenhanwu
 */
public class User {

    private String clientID;
    private String deviceID; 
    private String folderPath;

    /**
     * @return the folderPath
     */
    public String getFolderPath() {
        return folderPath;
    }

    /**
     * @param folderPath the folderPath to set
     */
    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    /**
     * @return the clientID
     */
    public String getClientID() {
        return clientID;
    }

    /**
     * @param clientID the clientID to set
     */
    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    /**
     * @return the deviceID
     */
    public String getDeviceID() {
        return deviceID;
    }

    /**
     * @param deviceID the deviceID to set
     */
    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }
 

    /**
     * Constructor. 
     * @param aClientID
     * @param aDeviceID
     * @param aFolderPath 
     */
    public User(String aClientID, String aDeviceID, String aFolderPath) {
        this.clientID = aClientID;
        this.deviceID = aDeviceID; 
        this.folderPath = aFolderPath;
    }
}
