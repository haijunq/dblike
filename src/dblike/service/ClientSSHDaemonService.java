/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.service;

import java.net.PasswordAuthentication;
import org.apache.sshd.SshServer;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.shell.ProcessShellFactory;

/**
 * Client side SSH Daemon service, deprecated. 
 * @author haijun
 */
public class ClientSSHDaemonService {

    private static SshServer sshd = SshServer.setUpDefaultServer();    

    /**
     * Start a SSH session. 
     */
    public static void startSSHServer() {
        if (sshd == null) {
            sshd =  SshServer.setUpDefaultServer(); 
        }
        sshd.setPort(22);
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("./key/clientHostKey.pem"));
        sshd.setPasswordAuthenticator(new PasswordAuthenticator(){
            @Override
            public boolean authenticate(String string, String string1, ServerSession ss) {
                return true;
            }
        });
        try {
            sshd.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        while (true) ;
    }
}
