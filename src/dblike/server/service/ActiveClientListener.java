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
public class ActiveClientListener implements Runnable {

    private ArrayList<ActiveClient> list;

    public ActiveClientListener() {
        this.list = ActiveClientList.getActiveClientList();
    }

    public void run() {
        int timeout = InternetUtil.getTIMEOUT() * 1000;
        while (true) {
            for (int i = 0; i < list.size() - 1; i++) {
                ActiveClient aClient = list.get(i);
                if (aClient.getStatus() == 1) {
                    String clientLabel = aClient.getClientID() + aClient.getDeviceID();
                    System.out.println(clientLabel + " is alive");
                    aClient.setStatus(0);
                } else {
                }
            }
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException ex) {
                Logger.getLogger(ActiveClientListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}