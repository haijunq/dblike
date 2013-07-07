/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.client.service;

import dblike.client.ActiveServer;
import dblike.service.InternetUtil;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wenhanwu
 */
public class ServerListenerClient implements Runnable {

    private ArrayList<ActiveServer> ActiveServerList;

    public ServerListenerClient() {
        this.ActiveServerList = ActiveServerListClient.getActiveServerList();
    }

    public boolean checkAllServer() {
        boolean flag = true;
        for (int i = 0; i < ActiveServerList.size(); i++) {
            ActiveServer aServer = ActiveServerList.get(i);
            if (aServer.getStatus() == 1) {
                aServer.setStatus(0);
            } else {
                String serverLabel = aServer.getServerIP() +":"+ aServer.getPort();
                ActiveServerListClient.removeServer(aServer.getServerIP(), aServer.getPort());
                System.out.println(serverLabel + " not available");
                flag = false;
            }
        }
        return flag;
    }

    public void run() {
        int timeout = InternetUtil.getTIMEOUT() * 1000;
        while (true) {
            checkAllServer();
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerListenerClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}
