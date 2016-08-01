/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author anando
 */
public class Server {
    private ServerSocket server;
    private Socket socket;
    private BufferedReader dIn;
    private PrintWriter dOut;
    private String dataFromClient;
    private String dataToClient;
    private Integer prevTrans;
    private Integer balance;
    
    Server() throws IOException{
        server = new ServerSocket(8080);
        prevTrans = -1;
        balance = 10000;
    }
    
    public void doIt(){
        while(true){
            try{
                System.out.println("Server Is trying to get connection");
                socket = server.accept();
                dOut = new PrintWriter(socket.getOutputStream(),true);
                dIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                System.out.println("Connection done");

                while(true){
                                        
                    dataFromClient = dIn.readLine();
                    if(dataFromClient.equals("DONE"))break;
                    System.out.println("Client given: "+dataFromClient);

                    Integer amount = Integer.parseInt(dataFromClient);

                    if(amount < 0)dOut.println("Sorry Invalid Input. DGM");
                    else if(amount > balance)dOut.println("Exceed your balance. DGM");
                    else if(amount <= balance && amount != prevTrans){
                        balance -= amount;
                        prevTrans = amount;
                        dOut.println("your current balance is " + new Integer(balance).toString());
                    }
                    else if(amount == prevTrans){
                        dOut.println("Sorry Something went wrong before. Your current balance is "+new Integer(balance).toString());
                        System.out.println("EQUAL");
                    }
                    System.out.println("Server has sent something: "+ prevTrans);
                }
            }catch(Exception e){
                continue;
            }
        }
    }
}
