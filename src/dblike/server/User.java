/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.server;

/**
 * User in the server side, deprecated. 
 * @author wenhanwu
 */
public class User {

    private String userID;
    private String deviceID; 

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
     * @param aUserID
     * @param aDeviceID
     * @param aUserIP
     * @param aPort 
     */
    public User(String aUserID, String aDeviceID, String aUserIP, int aPort) {
        this.userID = aUserID;
        this.deviceID = aDeviceID; 
    }
}
