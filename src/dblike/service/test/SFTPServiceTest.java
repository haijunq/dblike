/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.service.test;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import dblike.service.SFTPService;

/**
 *
 * @author BoHu
 */
public class SFTPServiceTest {
    
    public static void main(String args[]) throws JSchException, SftpException {

            System.out.println("Transfer start");
            SFTPService sftpService = new SFTPService();
//            sftpService.uploadFile("E:\\Dropbox\\Course\\CICS525\\dblike\\test\\transfer_test3.txt", "/home/ec2-user/transfer_test3.txt");
//            sftpService.downloadFile("/home/ec2-user/transfer_test3.txt", "E:\\Dropbox\\Course\\CICS525\\dblike\\test\\transfer_test4.txt");
            sftpService.uploadFile("/Users/jingboyu/Dropbox/UBC/CICS525/dblike/test/transfer_test.txt", "/home/ec2-user/transfer_test.txt");
            sftpService.downloadFile("/home/ec2-user/transfer_test.txt", "/Users/jingboyu/Dropbox/UBC/CICS525/dblike/test/transfer_test2.txt");
 
            System.out.println("Transfer done");

    }
}
