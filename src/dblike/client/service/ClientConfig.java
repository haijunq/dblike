/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.client.service;

import dblike.client.CurrentClient;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Scanner;
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

    public static void initCurrentClient(String aClientID, String aDeviceID, String aFolderPath) {
        currentClient.setClientID(aClientID);
        currentClient.setDeviceID(aDeviceID);
        currentClient.setFolderPath(aFolderPath);
    }

    /**
     *
     * @return
     */
    public static boolean loadUserList() {
        DocumentBuilderFactory domfac = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder dombuilder = domfac.newDocumentBuilder();
            System.out.println("ClientCfg/users/" + currentClient.getClientID() + "/user.xml");
            InputStream fis = new FileInputStream("ClientCfg/users/" + currentClient.getClientID() + "/user.xml");
            System.out.println(fis);
            Document doc = dombuilder.parse(fis);
            Element root = doc.getDocumentElement();
            Node user = root.getFirstChild();
            if (user != null) {
                String username = "";
                String folderPath = "";
                String deviceID = "";
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
                    }
                }

                initCurrentClient(username, deviceID, folderPath);

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = null;
                try {
                    transformer = transformerFactory.newTransformer();
                } catch (TransformerConfigurationException ex) {
                    Logger.getLogger(ClientConfig.class.getName()).log(Level.SEVERE, null, ex);
                }
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(new File("ClientCfg/users/" + currentClient.getClientID() + "/user.xml"));
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
}
