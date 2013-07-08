/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.server.service;

import dblike.server.ActiveClient;
import dblike.service.InternetUtil;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wenhanwu
 */
public class ClientListenerServer implements Runnable {

    private ArrayList<ActiveClient> ActiveClientList;

    public ClientListenerServer() {
        this.ActiveClientList = ActiveClientListServer.getActiveClientList();
    }

    public boolean checkAllClient() {
        boolean flag = true;
        System.out.println("-----c-----"); 
        for (int i = 0; i < ActiveClientList.size(); i++) {
            flag = true;
            ActiveClient aClient = ActiveClientList.get(i);
            String serverLabel = aClient.getClientID() + ":" + aClient.getDeviceID() + "---> " + aClient.getStatus();
            System.out.println(serverLabel);
            if (aClient.getStatus() == InternetUtil.getOK()) {
                aClient.setStatus(0);
            } else {
                if (aClient.getStatus() == 0) {
                    ActiveClientListServer.removeClient(aClient.getClientID(), aClient.getDeviceID());
                    flag=false;
                } else {
                    aClient.setStatus(aClient.getStatus() - 1);
                }
            }
        }
        System.out.println("-----c-----");
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
        while (true) {
            System.out.println("in run" + ActiveClientList.size());
            checkAllClient();
            waitForAWhile(InternetUtil.getTIMEOUT());
        }

    }
}