/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab1;

import java.io.IOException;
import java.util.Scanner;

/**
 *
 * Project created by Muhaimin Anando & Tanvir shahariar Rifat
 * 
 */
public class Lab1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        // TODO code application logic here
        System.out.println("Who are you ?\n1.Server\n2.Client");
        Scanner sc = new Scanner(System.in);
        int select = sc.nextInt();
        if(select == 2){
            Client client = new Client("127.0.0.1");
            client.doIt();
        }
        else{
            Server server = new Server();
            server.doIt();
        }
        
    }
    
}
