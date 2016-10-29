/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab7;

import java.io.IOException;

/**
 *
 * @author student
 */
public class Lab7 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        String hostname="172.16.13.200";
        //String hostname="localhost";
        Client C=new Client(hostname);
    } 
}
