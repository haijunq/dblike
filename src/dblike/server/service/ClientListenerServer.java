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
 *
 * @author wenhanwu
 */
public class ClientListenerServer implements Runnable {

    private boolean runningFlag = true;

    public void setRunningFlag(boolean flag) {
        this.runningFlag = flag;
    }
    private Vector<ActiveClient> ActiveClientList;

    public ClientListenerServer() {
        this.ActiveClientList = ActiveClientListServer.getActiveClientList();
    }

    public boolean checkAllClient() {
        boolean flag = true; 
        for (int i = 0; i < ActiveClientList.size(); i++) {
            flag = true;
            ActiveClient aClient = ActiveClientList.get(i); 
            if (aClient.getStatus() == InternetUtil.getOK()) {
                  System.out.println(aClient.getClientIP()+aClient.getPort()+"---OK");
                aClient.setStatus(aClient.getStatus() - 1);
            } else {
                aClient.setStatus(aClient.getStatus() - 1);
                if (aClient.getStatus() == 0) {
                    ActiveClientListServer.removeClient(aClient.getClientID(), aClient.getDeviceID());
                    flag = false;
                } else {
                    System.out.println("Connection problem, wait to see..."+ aClient.getClientIP() + ":" + aClient.getPort());
                }
            }
        } 
        return flag;
    }

    public void waitForAWhile(int timeOut) {
        try {
            Thread.sleep(timeOut * 1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ClientListenerServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void run() {
        while (runningFlag) {
            checkAllClient();
            waitForAWhile(InternetUtil.getTIMEOUT());
        }

    }
}