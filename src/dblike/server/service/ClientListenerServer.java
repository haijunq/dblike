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
        for (int i = 0; i < ActiveClientList.size() - 1; i++) {
            ActiveClient aClient = ActiveClientList.get(i);
            if (aClient.getStatus() == 1) {
                aClient.setStatus(0);
            } else {
                String clientLabel = aClient.getClientID() + aClient.getDeviceID();
                ActiveClientListServer.removeClient(aClient.getClientID(), aClient.getDeviceID());
                System.out.println(clientLabel + " not available");
                flag = false;
            }
        }
        return flag;
    }

    public void run() {
        int timeout = InternetUtil.getTIMEOUT() * 1000;
        while (true) {
            checkAllClient();
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException ex) {
                Logger.getLogger(ClientListenerServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}