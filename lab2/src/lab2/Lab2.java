/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab2;

import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author anando
 */
public class Lab2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        System.out.println("Who are you?\n1.client\n2.Server");
        Scanner sc = new Scanner(System.in);
        int choose=sc.nextInt();
        if(choose == 1){
            Client client = new Client("127.0.0.1");
            client.doTask();
            
        }
        else{
            Server server = new Server();
            server.doTask();
        }
    }
    
}
