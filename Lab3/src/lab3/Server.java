package lab3;

/**
 *
 * @author student
 */
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    
    int threadID;
    String query,retIP;
    com a;
    net b;
    org c;
    root e;
        
    
    public Server() throws Exception {
        
        threadID=0;
        retIP="";
        DatagramSocket serverSocket = new DatagramSocket(9876);

        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];

        while (true) {

            DatagramPacket receivePacket= new DatagramPacket(receiveData, receiveData.length);
            
            serverSocket.receive(receivePacket);
            query = new String(receivePacket.getData());

            InetAddress IPAddress = receivePacket.getAddress();
           

            int port = receivePacket.getPort();
            
            System.out.println(query);
            
            a= new com();
            b= new net();
            c= new org();
            e= new root();
            
            a.start();
            //b.start();
            //c.start();
            //e.start();
            
          //  b.wait();
          //  c.wait();
          //  e.wait();
             
          //  a.join();
          //  b.join();
          //  c.join();
         //   e.join();
         //   String capitalizedSentence = sentence.toUpperCase();
            
            Thread.sleep(2000);
            System.out.println("main "+retIP);
            sendData = retIP.getBytes();

            DatagramPacket sendPacket= new DatagramPacket(sendData, sendData.length, IPAddress, port);

            serverSocket.send(sendPacket);
        }
        
        
    }
    
    class com extends Thread{
        int id;
        String identifier=".com";
        String parent= "root";
        
        String child[]={"none"};
        
        String host[]={"www.real-timenews.com","www.prothom-alo.com","www.dailyjanakantha.com"};
        String ip[]={"104.28.19.196","45.64.64.216","45.63.122.154"};
        
        @Override
        public void run(){
            
            System.out.println("in com run");
            
            System.out.println("in com "+retIP);
            if(retIP.length() > 0)return;
            
            
            for(int i=0;i<3;i++){
             //   System.out.println(query+" "+host[i]+" "+"done");
                if(query.contains(host[i])){
                  //  System.out.println("true");
                    retIP= ip[i];
                }
            }
            
            if(retIP.length() == 0)e.start();
                /*
                    try {
                        notifyAll();
                        b.wait();
                        c.wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                */
            
        }
    }
    
    class root extends Thread{
        int id;
        String identifier="root";
        String parent= "none";
        
        String child[]={".com",".net",".org"};
        
     //   String host[]={"www.real-timenews.com","www.prothom-alo.com","www.dailyjanakantha.com"};
     //   String ip[]={"104.28.19.196","45.64.64.216","45.63.122.154"};
        
        @Override
        public void run(){
            
            System.out.println("in root run");
            
            System.out.println(retIP);
            if(retIP.length()>0){
                com aa = new com();
                aa.start();
                return;
            }
            for(int i=0;i<3;i++){
                if(query.contains(child[i])){
//                    notifyAll();
                    if(i==0){
                        new com().start();
                    }
                    else if(i==1){
                        
                        new net().start();
                    }
                    else if(i==2){
                        
                        new org().start();
                    }
                }
            }
            
        }
    }
    
    class net extends Thread{
        int id;
        String identifier=".net";
        String parent= "root";
        
        String child[]={"none"};
        
        String host[]={"www.slideshare.net","www.bdlan.net"};
        String ip[]={"144.2.1.10","103.204.208.202"};
        
        @Override
        public void run(){
            
            System.out.println("in net run");
            
            for(int i=0;i<2;i++){
             //   System.out.println(query+" "+host[i]+" "+"done");
                if(query.contains(host[i])){
                //    System.out.println("true in net");
                    retIP= ip[i];
                }
            }
            if(retIP.length() == 0)retIP = "SORRY NOT FOUND";
            
            new root().start();
            
            /*
            try {
                        notifyAll();
                        a.wait();
                    
                        c.wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    */
        }
    }
    
    class org extends Thread{
        int id;
        String identifier=".org";
        String parent= "root";
        
        String child[]={"none"};
        
        String host[]={"uva.onlinejudge.org","sportprogramming.blogspot.org"};
        String ip[]={"92.222.246.128","74.125.200.132"};
        
        @Override
        public void run(){
            System.out.println("in org run");
            for(int i=0;i<2;i++){
                if(query.contains(host[i])){
                    retIP= ip[i];
                }
            }
            
            if(retIP.length() == 0)retIP = "SORRY NOT FOUND";
            
            new root().start();
            
            /*
            try {
                        notifyAll();
                        b.wait();
                        a.wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    */
        }
    }
    
}
