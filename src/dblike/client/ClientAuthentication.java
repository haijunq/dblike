/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.client;

import dblike.api.ServerAPI;
import dblike.service.MD5Service;

/**
 * Deprecated, client Authentication method. 
 * @author haijun
 */
public class ClientAuthentication {
    protected final String ERROR_MESSAGE = "Invalid username or password. Please try again.";
    
    public ClientAuthentication() {
        //need to call server side UserListXMLReader() to do the authentication
    }
    
}
