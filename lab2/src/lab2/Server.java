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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author anando
 */
public class Server {
    private int searchString;
    private StringBuilder foundAt;
    private Socket socket;
    private ServerSocket server;
    private BufferedReader dIn;
    private PrintWriter dOut;
    private String dataFromClient;
    private String dataToClient;
    private Integer prevTrans;
    private Integer balance;
    
    private A a;
    private B b;
    private C c;
    private D d;
    private E e;
    
    Server() throws IOException{
        server = new ServerSocket(8080);
    }
    //at first create 5 other object
    
    /*
    * Thread creation start
    */
    
    class A extends Thread{
        private int [] arr;
        public boolean nowStop;
        A(){
            arr = new int[5];
            for(int i=1;i<=5;i++)arr[i-1] = (i);
            nowStop = false;
        }
        public void run(){
            for(int i=0;i<5 && !nowStop;i++)if(searchString==(arr[i])){
                synchronized(foundAt){
                    if(nowStop)break;
                    foundAt.append("Found in A. position: ");
                    foundAt.append(String.valueOf(i));
                    closeAllOther();
                }
            }
        }
    }
    
    class B extends Thread{
        private int [] arr;
        public boolean nowStop;
        B(){
            arr = new int[5];
            for(int i=1;i<=5;i++)arr[i-1] = (i+5);
            nowStop = false;
        }
        public void run(){
            for(int i=0;i<5 && !nowStop;i++)if(searchString==(arr[i])){
                synchronized(foundAt){
                    if(nowStop)break;
                    foundAt.append("Found in B. position: ");
                    foundAt.append(String.valueOf(i));
                    closeAllOther();
                }
            }
        }
    }
    
    
    class C extends Thread{
        private int [] arr;
        public boolean nowStop;
        C(){
            arr = new int[5];
            for(int i=1;i<=5;i++)arr[i-1] = (i+10);
            nowStop = false;
        }
        public void run(){
            for(int i=0;i<5 && !nowStop;i++)if(searchString==(arr[i])){
                synchronized(foundAt){
                    if(nowStop)break;
                    foundAt.append("Found in C. position: ");
                    foundAt.append(String.valueOf(i));
                    closeAllOther();
                }
            }
        }
    }
    
    
    class D extends Thread{
        private int [] arr;
        public boolean nowStop;
        D(){
            arr = new int[5];
            for(int i=1;i<=5;i++)arr[i-1] = (i+15);
            nowStop = false;
        }
        public void run(){
            for(int i=0;i<5 && !nowStop;i++)if(searchString==(arr[i])){
                synchronized(foundAt){
                    if(nowStop)break;
                    foundAt.append("Found in D. position: ");
                    foundAt.append(String.valueOf(i));
                    closeAllOther();
                }
            }
        }
    }
    
    
    class E extends Thread{
        private int [] arr;
        public boolean nowStop;
        E(){
            arr = new int[5];
            for(int i=1;i<=5;i++)arr[i-1] = (i+20);
            nowStop = false;
        }
        public void run(){
            for(int i=0;i<5 && !nowStop;i++)if(searchString==(arr[i])){
                synchronized(foundAt){
                    if(nowStop)break;
                    foundAt.append("Found in E. position: ");
                    foundAt.append(String.valueOf(i));
                    closeAllOther();
                }
            }
        }
    }
    
    /*
    * END OF ALL CREATION
    */
    
    public void closeAllOther(){
        a.nowStop = true;
        b.nowStop = true;
        c.nowStop = true;
        d.nowStop = true;
        e.nowStop = true;
        
    }
    
    public void doTask(){
        while(true){
            try{
                System.out.println("Server Is trying to get connection");
                socket = server.accept();
                dOut = new PrintWriter(socket.getOutputStream(),true);
                dIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                System.out.println("Connection done");
                
                while(true){
                    searchString = Integer.parseInt(dIn.readLine());
                    if(searchString==-1)break;
                    
                    System.out.println("searching for : "+searchString);
                    foundAt = new StringBuilder();
                    
                    a = new A();
                    b = new B();
                    c = new C();
                    d = new D();
                    e = new E();
                    
                    a.start();
                    b.start();
                    c.start();
                    d.start();
                    e.start();
                    
                    a.join();
                    b.join();
                    c.join();
                    d.join();
                    e.join();
                    
                    if(foundAt.length() == 0)foundAt.append("SORRY NOT FOUND");
                    
                    dOut.println(foundAt);
                    dOut.flush();
                }
                
            }catch(Exception e){
                continue;
            }
        }
    }
    
}
