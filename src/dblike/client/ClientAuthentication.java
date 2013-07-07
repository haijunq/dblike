/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.client;

import dblike.api.ServerAPI;
import dblike.service.MD5Service;

/**
 *
 * @author haijun
 */
public class ClientAuthentication {
    protected final String ERROR_MESSAGE = "Invalid username or password. Please try again.";
    
    public ClientAuthentication() {
        //need to call server side UserListXMLReader() to do the authentication
    }
    
//    public boolean authenticate(ServerAPI serverAPI, String username, String password) {
//        return serverAPI.checkAuthentication(username, MD5Service.getInstance().getMD5HashCode(password));
//    }
}
