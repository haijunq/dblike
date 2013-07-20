/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.server.service;

import dblike.server.ActiveClient;
import dblike.service.InternetUtil;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the listener thread to keep track on the ActiveClientList. Will be
 * used to check the heartbeat of clients.
 *
 * @author wenhanwu
 */
public class ClientListenerServer implements Runnable {

    private boolean runningFlag = true;
    private Vector<ActiveClient> ActiveClientList;

    /**
     * This is to stop the thread.
     *
     * @param flag
     */
    public void setRunningFlag(boolean flag) {
        this.runningFlag = flag;
    }

    /**
     * To get the ActiveClientList.
     */
    public ClientListenerServer() {
        this.ActiveClientList = ActiveClientListServer.getActiveClientList();
    }

    /**
     * Keep checking all the clients to see the heartbeat.
     *
     * @return
     */
    public boolean checkAllClient() {
        boolean flag = true;
        for (int i = 0; i < ActiveClientList.size(); i++) {
            flag = true;
            ActiveClient aClient = ActiveClientList.get(i);
            if (aClient.getStatus() == InternetUtil.getOK()) {
                System.out.println(aClient.getClientIP() + aClient.getPort() + "---OK");
                aClient.setStatus(aClient.getStatus() - 1);
            } else {
                aClient.setStatus(aClient.getStatus() - 1);
                if (aClient.getStatus() == 0) {
                    ActiveClientListServer.removeClient(aClient.getClientID(), aClient.getDeviceID());
                    flag = false;
                } else {
                    System.out.println("Connection problem, wait to see..." + aClient.getClientIP() + ":" + aClient.getPort());
                }
            }
        }
        return flag;
    }

    /**
     * Wait for a while.
     *
     * @param timeOut
     */
    public void waitForAWhile(int timeOut) {
        try {
            Thread.sleep(timeOut * 1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ClientListenerServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Put the checking into a loop to keep track of all clients.
     */
    public void run() {
        while (runningFlag) {
            checkAllClient();
            waitForAWhile(InternetUtil.getTIMEOUT());
        }

    }
}