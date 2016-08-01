/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author anando
 */
public class Client {
    private Socket socket;
    private String amount;
    private String IP;
    private String dataFromServer;
    private BufferedReader dIn; //data input
    private PrintWriter dOut;
    private BufferedReader sc;
    private Random rand;
    private MyTimer t;
    
    //set IP and Open socket
    Client(String s) throws IOException{
        IP = s;
        socket = new Socket(IP,8080);
        dIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        dOut = new PrintWriter(socket.getOutputStream(), true);
        sc = new BufferedReader(new InputStreamReader(System.in));
        rand = new Random(100);
        t = new MyTimer();
    }
    class MyTimer extends Thread{
        public boolean goOut = false;
        public void run(){
            long prevTime = System.currentTimeMillis()%10000;
            
            while(true){
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("Timeout!!!! resending");
                if(!goOut)dOut.println(amount);
                else break;
            }
            System.out.println("end of Thread");
        }
    }
    void doIt() throws IOException, InterruptedException{

        System.out.println("Enter Your Amount(Total balance is $10000):\n");
        //enter amount
        amount = sc.readLine();
        
        dOut.println(amount);
        System.out.println("data given: "+amount);
        t.start();

        while(true){
            //generate error
            int xx = rand.nextInt(100)%100;
            if(xx<5){
                System.out.println("ERROR!!! Couldn't Receive");
                continue;
            }

            dataFromServer = dIn.readLine();
            if(dataFromServer.length()>0)break;
        }
        t.goOut = true;
        
        System.out.println(dataFromServer);
        //done bye bye
        t.join();
        dOut.println("DONE");
        socket.close();
        dOut.close();
        dIn.close();
    }
}
