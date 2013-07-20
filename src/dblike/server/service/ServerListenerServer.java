/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.server.service;

import dblike.server.ActiveServer;
import dblike.server.ServerStart;
import dblike.service.InternetUtil;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This is the listener thread to keep track on the ActiveServerList. Will be
 * used to check the heartbeat of servers.
 *
 * @author wenhanwu
 */
public class ServerListenerServer implements Runnable {

    private boolean runningFlag = true;
    private Vector<ActiveServer> ActiveServerList;

    /**
     * This is to stop the thread.
     *
     * @param flag
     */
    public void setRunningFlag(boolean flag) {
        this.runningFlag = flag;
    }

    /**
     * To get the ActiveServerList.
     */
    public ServerListenerServer() {
        this.ActiveServerList = ActiveServerListServer.getActiveServerList();
    }

    /**
     * Keep checking all the servers to see the heartbeat.
     *
     * @return
     */
    public boolean checkAllServer() {
        boolean flag = true;
        for (int i = 0; i < ActiveServerList.size(); i++) {
            flag = true;
            ActiveServer aServer = ActiveServerList.get(i);
            if (aServer.getStatus() == InternetUtil.getOK()) {
                aServer.setIsConnect(1);
                //System.out.println(aServer.getServerIP() + ":" + aServer.getPort() + "---> OK");
                aServer.setStatus(aServer.getStatus() - 1);
            } else {
                aServer.setStatus(aServer.getStatus() - 1);
                if (aServer.getStatus() == 0) {
                    //System.out.println(aServer.getServerIP() + ":" + aServer.getPort() + "---> NO");
                    aServer.setIsConnect(0);
                    //ActiveServerListServer.removeServer(aServer.getServerIP(), aServer.getPort());
                    flag = false;
                } else {
                    //System.out.println("Connection problem, wait to see..."+ aServer.getServerIP() + ":" + aServer.getPort());
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
            Logger.getLogger(ServerListenerServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Put the checking into a loop to keep track of all servers.
     */
    public void run() {
        waitForAWhile(2);
        ActiveServerListServer.loadServerList();
        while (runningFlag) {
            checkAllServer();
            waitForAWhile(InternetUtil.getTIMEOUT());
        }

    }
}