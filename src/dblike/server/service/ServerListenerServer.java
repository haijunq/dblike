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
 *
 * @author wenhanwu
 */
public class ServerListenerServer implements Runnable {

    private boolean runningFlag = true;

    public void setRunningFlag(boolean flag) {
        this.runningFlag = flag;
    }
    private Vector<ActiveServer> ActiveServerList;

    public ServerListenerServer() {
        this.ActiveServerList = ActiveServerListServer.getActiveServerList();
    }

    public boolean checkAllServer() {
        boolean flag = true; 
        for (int i = 0; i < ActiveServerList.size(); i++) {
            flag = true;
            ActiveServer aServer = ActiveServerList.get(i); 
            if (aServer.getStatus() == InternetUtil.getOK()) { 
                aServer.setStatus(aServer.getStatus() - 1);
            } else {
                aServer.setStatus(aServer.getStatus() - 1);
                if (aServer.getStatus() == 0) {
                    ActiveServerListServer.removeServer(aServer.getServerIP(), aServer.getPort());
                    flag = false;
                } else {
                    System.out.println("Connection problem, wait to see..."+ aServer.getServerIP() + ":" + aServer.getPort());
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
        ActiveServerListServer.loadServerList();
        while (runningFlag) {
            checkAllServer();
            waitForAWhile(InternetUtil.getTIMEOUT());
        }

    }
}