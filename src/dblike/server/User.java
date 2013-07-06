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
 

    public User(String aUserID, String aDeviceID, String aUserIP, int aPort) {
        this.userID = aUserID;
        this.deviceID = aDeviceID; 
    }
}
