/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.server.service;

import dblike.server.ActiveServer;
import dblike.service.InternetUtil;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wenhanwu
 */
public class ServerListenerServer implements Runnable {

    private ArrayList<ActiveServer> ActiveServerList;

    public ServerListenerServer() {
        this.ActiveServerList = ActiveServerListServer.getActiveServerList();
    }

    public boolean checkAllServer() {
        boolean flag = true;
        System.out.println("=====s=====");
        for (int i = 0; i < ActiveServerList.size(); i++) {
            flag = true;
            ActiveServer aServer = ActiveServerList.get(i);
            String serverLabel = aServer.getServerIP() + ":" + aServer.getPort() + "---> " + aServer.getStatus();
            System.out.println(serverLabel);
            if (aServer.getStatus() == InternetUtil.getOK()) {
                //aServer.setStatus(0);
                System.out.println("OK");
            } else {
                if (aServer.getStatus() == 0) {
                    ActiveServerListServer.removeServer(aServer.getServerIP(), aServer.getPort());
                    flag=false;
                } else {
                    System.out.println("decrese!!!!!!!!");
                    int temp=aServer.getStatus() - 1;
                    aServer.setStatus(temp);
                }
            }
        }
        System.out.println("=====s=====");
        return flag;
    }

    public void waitForAWhile(int timeOut) {
        try {
            Thread.sleep(timeOut * 1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerListenerServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void run() {
        while (true) { 
            checkAllServer();
            waitForAWhile(InternetUtil.getTIMEOUT());
        }

    }
}