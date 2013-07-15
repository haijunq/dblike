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

    private Vector<ActiveClient> ActiveClientList;

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
                //aClient.setStatus(0);
                System.out.println("OK");
                aClient.setStatus(aClient.getStatus() - 1);
            } else {
                aClient.setStatus(aClient.getStatus() - 1);
                if (aClient.getStatus() == 0) {
                    ActiveClientListServer.removeClient(aClient.getClientID(), aClient.getDeviceID());
                    flag = false;
                } else {
                    System.out.println("still have some...");
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
            checkAllClient();
            waitForAWhile(InternetUtil.getTIMEOUT());
        }

    }
}