/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.server.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
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
 * @author haijun
 */
public class UserListXMLReader {

    private static Hashtable<String, String> validUserList = new Hashtable<>();

    /**
     * 
     * @param username
     * @param password
     * @return 
     */
    public static boolean isValidUser(String username, String password) {
        getValidUserList();
        if (validUserList.isEmpty()) {
            return false;
        }
        return validUserList.containsKey(username) && password.equals(validUserList.get(username));
    }

    public static Hashtable<String, String> getValidUserList() {
        loadUserListFromXML();
        return validUserList;
    }
    
    /**
     * 
     * @return 
     */
    public static boolean loadUserListFromXML() {
        DocumentBuilderFactory domfac = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder dombuilder = domfac.newDocumentBuilder();
            InputStream fis = new FileInputStream("users/userlist.xml");
            Document doc = dombuilder.parse(fis);
            Element root = doc.getDocumentElement();
            NodeList users = root.getChildNodes();
            if (users != null) {
                for (int i = 0; i < users.getLength(); i++) {
                    Node user = users.item(i);
                    if (user.getNodeType() == Node.ELEMENT_NODE) {
                        int checker = 0;
                        String username = "";
                        String password = "";
                        for (Node node = user.getFirstChild(); node != null; node = node.getNextSibling()) {
                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                if (node.getNodeName().equals("username")) {
                                    username = node.getFirstChild().getNodeValue();
                                    ++checker;
                                }
                                if (node.getNodeName().equals("password")) {
                                    password = node.getFirstChild().getNodeValue();
                                    ++checker;
                                }
                            }
                            if (checker == 2) {
                                validUserList.put(username, password);
                                checker = 0;
                            }
                        }
                    }
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
