/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

/**
 *
 * @author anando
 */
public class Client {
    private Socket socket;
    private String search;
    private String IP;
    private String dataFromServer;
    private BufferedReader dIn; //data input
    private PrintWriter dOut;
    private BufferedReader sc;
    private Random rand;
    
    Client(String s) throws IOException{
        IP = s;
        socket = new Socket(IP , 8080);
        dIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        dOut = new PrintWriter(socket.getOutputStream(), true);
        sc = new BufferedReader(new InputStreamReader(System.in));
        rand = new Random();
    }
    
    public void doTask() throws IOException{
        
        System.out.println("Enter search string");
        search = sc.readLine();
        
        System.out.println("Entered String: "+search);
        
        dOut.println(search);
        dOut.flush();
        dataFromServer = dIn.readLine();
        
        System.out.println(dataFromServer);
        dOut.println("-1");
        dOut.flush();
        dIn.close();
        dOut.close();
        socket.close();
    }
}
