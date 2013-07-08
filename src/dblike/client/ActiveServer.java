/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.client;

import dblike.server.*;
import dblike.service.InternetUtil;

/**
 *
 * @author wenhanwu
 */
public class ActiveServer {

    private String serverID;
    private String serverIP;
    private int port;
    private int status;

    /**
     * @return the serverID
     */
    public String getServerID() {
        return serverID;
    }

    /**
     * @param serverID the serverID to set
     */
    public void setServerID(String serverID) {
        this.serverID = serverID;
    }

    /**
     * @return the serverIP
     */
    public String getServerIP() {
        return serverIP;
    }

    /**
     * @param serverIP the serverIP to set
     */
    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
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

    public ActiveServer(String aServerID, String aClientIP, int aPort) {
        this.serverID = aServerID;
        this.serverIP = aClientIP;
        this.port = aPort;
        this.status = InternetUtil.getOK();
    }
}
