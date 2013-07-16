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
 *
 * @author wenhanwu
 */
public class CurrentClient {

    private String clientID;
    private String deviceID;
    private String folderPath;
    private String ip;
    private String port;

    /**
     * @return the ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * @param ip the ip to set
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * @return the port
     */
    public String getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(String port) {
        this.port = port;
    }

    public CurrentClient() {
    }

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

    public CurrentClient(String aClientID, String aDeviceID, String aFolderPath, String aIP, String aPort) {
        this.clientID = aClientID;
        this.deviceID = aDeviceID;
        this.folderPath = aFolderPath;
        this.ip = aIP;
        this.port = aPort;
    }
}
