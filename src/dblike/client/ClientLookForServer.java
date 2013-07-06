/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.client;

/**
 *
 * @author haijun
 */
public class ClientLookForServer {
    private static final String [] serverIPs = {
        "23.21.10.103", "", ""
    };
    
    public static String selectServer() {
        return serverIPs[(int)(Math.random() * serverIPs.length)];
    }
}
