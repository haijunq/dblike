/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.client.service;

import dblike.client.ActiveServer;
import dblike.service.InternetUtil;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
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
public class ActiveServerListClient {

    private static Vector<ActiveServer> ActiveServerList = new Vector<ActiveServer>();

    /**
     * @return the ActiveServerList
     */
    public static Vector<ActiveServer> getActiveServerList() {
        return ActiveServerList;
    }

    /**
     * @param aActiveServerList the ActiveServerList to set
     */
    public static void setActiveServerList(Vector<ActiveServer> aActiveServerList) {
        ActiveServerList = aActiveServerList;
    }

    public static ActiveServer searchServerByIP_Port(String serverIP, int port) {
        for (int i = 0; i < ActiveServerList.size(); ++i) {
            if (ActiveServerList.get(i).getServerIP().equals(serverIP) && ActiveServerList.get(i).getPort() == port) {
                return ActiveServerList.get(i);
            }
        }

        System.out.println(ActiveServerList.size() + "not found!!!!!!!!!!" + serverIP + port);
        return null;
    }

    public static int checkServerByIP_Port(String serverIP, int port) {
        for (int i = 0; i < ActiveServerList.size(); ++i) {
            if (ActiveServerList.get(i).getServerIP().equals(serverIP) && ActiveServerList.get(i).getPort() == port) {
                return i;
            }
        }
        return -1;
    }

    public static boolean removeServer(String serverIP, int port) {
        if (searchServerByIP_Port(serverIP, port) == null) {
            return false;
        } else {
            ActiveServerList.remove(searchServerByIP_Port(serverIP, port));
            return true;
        }
    }

    public static boolean addServer(String serverIP, int port) {
        if (searchServerByIP_Port(serverIP, port) == null) {
            String serverID = serverIP;
            ActiveServerList.add(new ActiveServer(serverID, serverIP, port));
            System.out.println("Server [" + " " + serverIP + ":" + port + "] Added~!!!");
            return true;
        } else {
            return false;
        }
    }

    public static boolean beatTheServer(String serverIP, int port) {
        System.out.println("Beat server " + serverIP + ":" + port);
        int position = checkServerByIP_Port(serverIP, port);
        if (position == -1) {
            return false;
        } else {
            ActiveServerList.get(position).setStatus(InternetUtil.getOK());
            return true;
        }
    }

}
