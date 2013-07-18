/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.server;

import dblike.api.ClientAPI;
import dblike.service.InternetUtil;
import java.rmi.registry.Registry;

/**
 *
 * @author wenhanwu
 */
public class ActiveClient {

    private String clientID;
    private String deviceID;
    private String clientIP;
    private int port;
    private int status;
    private Registry registry;
    private ClientAPI clientAPI = null;

    /**
     * @return the registry
     */
    public Registry getRegistry() {
        return registry;
    }

    /**
     * @param registry the registry to set
     */
    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    /**
     * @return the clientAPI
     */
    public ClientAPI getClientAPI() {
        return clientAPI;
    }

    /**
     * @param clientAPI the clientAPI to set
     */
    public void setClientAPI(ClientAPI clientAPI) {
        this.clientAPI = clientAPI;
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
     * @return the clientIP
     */
    public String getClientIP() {
        return clientIP;
    }

    /**
     * @param clientIP the clientIP to set
     */
    public void setClientIP(String clientIP) {
        this.clientIP = clientIP;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(int status) {
        this.status = status;
    }


    public ActiveClient(String aClientID, String aDeviceID, String aClientIP, int aPort) {
        this.clientID = aClientID;
        this.deviceID = aDeviceID;
        this.clientIP = aClientIP;
        this.port = aPort;
        this.status = InternetUtil.getOK();
    }

    @Override
    public String toString() {
        return "ActiveClient{" + "clientID=" + clientID + ", deviceID=" + deviceID + ", clientIP=" + clientIP + ", port=" + port + ", status=" + status + ", registry=" + registry + ", clientAPI=" + clientAPI + '}';
    }
    
    
}
