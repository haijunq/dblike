/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.server;

/**
 *
 * @author wenhanwu
 */
public class User {

    private String userID;
    private String deviceID;
    private String userIP;
    private int port;

    /**
     * @return the userID
     */
    public String getUserID() {
        return userID;
    }

    /**
     * @param userID the userID to set
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * @return the userIP
     */
    public String getUserIP() {
        return userIP;
    }

    /**
     * @param userIP the userIP to set
     */
    public void setUserIP(String userIP) {
        this.userIP = userIP;
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

    public User(String aUserID, String aDeviceID, String aUserIP, int aPort) {
        this.userID = aUserID;
        this.deviceID = aDeviceID;
        this.userIP = aUserIP;
        this.port = aPort;
    }
}
