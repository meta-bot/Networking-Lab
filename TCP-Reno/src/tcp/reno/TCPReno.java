/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcp.reno;

import java.io.IOException;

/**
 *
 * @author anando
 */
public class TCPReno {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        String Hostname = "127.0.0.1";
        Client client = new Client(Hostname);
        
        client.doTask();
    }
    
}
