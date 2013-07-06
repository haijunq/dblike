/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.server;

/**
 *
 * @author wenhanwu
 */
public class ActiveClient {

    private String clientID;
    private String deviceID;
    private String clientIP;
    private int port;

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
 
 

    public ActiveClient(String aClientID, String aDeviceID, String aClientIP, int aPort) {
        this.clientID = aClientID;
        this.deviceID = aDeviceID;
        this.clientIP = aClientIP;
        this.port = aPort;
    }
}