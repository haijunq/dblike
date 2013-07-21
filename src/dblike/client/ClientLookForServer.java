/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.client;

/**
 * Return a random server IP, deprecated. 
 * @author haijun
 */
public class ClientLookForServer {
    private static final String [] serverIPs = {
        "23.23.129.169", "54.244.115.114", "54.245.231.227"
    };
    
    /**
     * Return a server IP.
     * @return 
     */
    public static String selectServer() {
        return serverIPs[(int)(Math.random() * serverIPs.length)];
    }
}
