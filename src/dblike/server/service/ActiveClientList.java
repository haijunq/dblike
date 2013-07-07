/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.server.service;

import dblike.server.ActiveClient;
import java.util.ArrayList;

/**
 *
 * @author wenhanwu
 */
public class ActiveClientList {

    private static ArrayList<ActiveClient> ActiveClientList = new ArrayList<ActiveClient>();

    /**
     * @return the ActiveClientList
     */
    public static ArrayList<ActiveClient> getActiveClientList() {
        return ActiveClientList;
    }

    /**
     * @param aActiveClientList the ActiveClientList to set
     */
    public static void setActiveClientList(ArrayList<ActiveClient> aActiveClientList) {
        ActiveClientList = aActiveClientList;
    }
    
}
