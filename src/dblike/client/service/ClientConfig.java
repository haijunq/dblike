/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.client.service;

import dblike.client.ActiveServer;
import dblike.client.CurrentClient;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author wenhanwu
 */
public class ClientConfig {

    private static CurrentClient currentClient = new CurrentClient();
    private static Vector<ActiveServer> ServerList = new Vector<ActiveServer>();

    /**
     * @return the currentClient
     */
    public static CurrentClient getCurrentClient() {
        return currentClient;
    }

    /**
     * @param aCurrentClient the currentClient to set
     */
    public static void setCurrentClient(CurrentClient aCurrentClient) {
        currentClient = aCurrentClient;
    }

    /**
     * @return the ServerList
     */
    public static Vector<ActiveServer> getServerList() {
        return ServerList;
    }

    /**
     * @param aServerList the ServerList to set
     */
    public static void setServerList(Vector<ActiveServer> aServerList) {
        ServerList = aServerList;
    }

    public static void initCurrentClient(String aClientID, String aDeviceID, String aFolderPath, String aIP, String aPort) {
        getCurrentClient().setClientID(aClientID);
        getCurrentClient().setDeviceID(aDeviceID);
        getCurrentClient().setFolderPath(aFolderPath);
        getCurrentClient().setIp(aIP);
        getCurrentClient().setPort(aPort);
    }

    /**
     *
     * @return
     */
    public static boolean loadCurrentUser() {
        DocumentBuilderFactory domfac = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder dombuilder = domfac.newDocumentBuilder();
            System.out.println("ClientCfg/users/" + getCurrentClient().getClientID() + "/user.xml");
            InputStream fis = new FileInputStream("ClientCfg/users/" + getCurrentClient().getClientID() + "/user.xml");
            System.out.println(fis);
            Document doc = dombuilder.parse(fis);
            Element root = doc.getDocumentElement();
            Node user = root.getFirstChild();
            if (user != null) {
                String username = "";
                String folderPath = "";
                String deviceID = "";
                String ip = "";
                String port = "";
                for (Node node = user; node != null; node = node.getNextSibling()) {
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        if (node.getNodeName().equals("username")) {
                            username = node.getFirstChild().getNodeValue();
                            System.out.println(username);
                        }
                        if (node.getNodeName().equals("deviceID")) {
                            deviceID = node.getFirstChild() == null ? "" : node.getFirstChild().getNodeValue();
                            if (deviceID.equals("")) {
                                Scanner scanDID = new Scanner(System.in);
                                System.out.println("First time login, must input the Device ID:");
                                deviceID = scanDID.nextLine();
                                node.appendChild(doc.createTextNode(deviceID));
                                node.getFirstChild().setNodeValue(deviceID);
                            }
                        }
                        if (node.getNodeName().equals("folderPath")) {
                            folderPath = node.getFirstChild() == null ? "" : node.getFirstChild().getNodeValue();
                            if (folderPath.equals("")) {
                                Scanner scanFP = new Scanner(System.in);
                                System.out.println("First time login, must specify a sync Folder:");
                                folderPath = scanFP.nextLine();
                                node.appendChild(doc.createTextNode(folderPath));
                                node.getFirstChild().setNodeValue(folderPath);
                            }
                        }
                        
                        if (node.getNodeName().equals("IP")) {
                            ip = node.getFirstChild().getNodeValue(); 
                        }
                        
                        if (node.getNodeName().equals("port")) {
                            port = node.getFirstChild().getNodeValue(); 
                        }
                    }
                }

                initCurrentClient(username, deviceID, folderPath, ip, port);

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = null;
                try {
                    transformer = transformerFactory.newTransformer();
                } catch (TransformerConfigurationException ex) {
                    Logger.getLogger(ClientConfig.class.getName()).log(Level.SEVERE, null, ex);
                }
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(new File("ClientCfg/users/" + getCurrentClient().getClientID() + "/user.xml"));
                try {
                    transformer.transform(source, result);
                } catch (TransformerException ex) {
                    Logger.getLogger(ClientConfig.class.getName()).log(Level.SEVERE, null, ex);
                }
                return true;
            } else {
                return false;
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return false;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (SAXException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     *
     * @return
     */
    public static boolean loadServerList() {
        DocumentBuilderFactory domfac = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder dombuilder = domfac.newDocumentBuilder();
            InputStream fis = new FileInputStream("ClientCfg/serverList.xml");
            Document doc = dombuilder.parse(fis);
            Element root = doc.getDocumentElement();
            NodeList users = root.getChildNodes();
            if (users != null) {
                for (int i = 0; i < users.getLength(); i++) {
                    Node user = users.item(i);
                    if (user.getNodeType() == Node.ELEMENT_NODE) {
                        int checker = 0;
                        String ip = "";
                        String port = "";
                        for (Node node = user.getFirstChild(); node != null; node = node.getNextSibling()) {
                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                if (node.getNodeName().equals("IP")) {
                                    ip = node.getFirstChild().getNodeValue();
                                    ++checker;
                                }
                                if (node.getNodeName().equals("port")) {
                                    port = node.getFirstChild().getNodeValue();
                                    ++checker;
                                }
                            }
                            if (checker == 2) {
                                String serverID = ip;
                                getServerList().add(new ActiveServer(serverID, ip, Integer.parseInt(port)));
                                checker = 0;
                            }
                        }
                    }
                }
                for (int i = 0; i < getServerList().size(); ++i) {
                    System.out.println(getServerList().get(i).getServerIP() + ":" + getServerList().get(i).getPort());
                }
                return true;
            } else {
                return false;
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return false;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (SAXException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
