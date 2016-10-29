package server6;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server6 {
    
    static int BUFFER_SIZE= 18000, N=1000;
    static long startTime;
    static long TIMEOUT=500;
    static boolean[] received;
    static int count=0;
    
    static DataOutputStream outToClient;
 
    
    public static void main(String[] args) throws IOException {
 
        ServerSocket welcomeSocket = new ServerSocket(6789);
 
        Socket connectionSocket = welcomeSocket.accept();
        BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        outToClient = new DataOutputStream(connectionSocket.getOutputStream());
         
        System.out.println("CONNECTED");
        outToClient.writeBytes(Integer.toString(BUFFER_SIZE) + "\n");
        System.out.println("BUFFER_SIZE: " + BUFFER_SIZE+"\n");
        
        while(!inFromClient.ready()) 
            System.out.println("waiting");
        
        
        startTime = System.currentTimeMillis();
        received = new boolean[36];
 
        String SEQ="";
        //char[] chars= new char[1000];
 
        while (true) {   
            
            if(inFromClient.ready()) {
                
                //System.out.println(".");
                
                SEQ = inFromClient.readLine();
                System.out.println("<<<<<<<<<SEQ: "+ SEQ);
                 if(SEQ.equals("EOF")) System.exit(0);    
               
                String read="";
                for(int i=0; i<1000; i++) {
                    read+=(char)inFromClient.read();
                }
                
                
                System.out.println(read);
                //chars = new char[1000];
                
                
                if(!error()){
                    
                    try {
                        count=Integer.parseInt(SEQ)/1000;
                        received[count]= true;
                        System.out.println("###### RCV: "+ count);
                        
                        // if it is a old pack
                        int i;
                        for(i=received.length-1; i>=0; i--) {
                            if(received[i]==true) {
                                break;
                            }
                        }
                        
                        if(i>count) sendAck();
                        
                        

                    } catch (Exception e) {
                        System.err.println(SEQ);
                        e.printStackTrace();
                        System.exit(-1);
                    }
                }
                
                else {
                    if(!received[Integer.parseInt(SEQ)/1000])
                        count++;
                    System.out.println("####### ERR: " +count);
                }                           
                
            }
            
            if(System.currentTimeMillis()-startTime>TIMEOUT) {
                sendAck();
            }
            else {
                //System.out.println(".................."+(System.currentTimeMillis()-startTime));
            }
 
        }
        
    }

    private static boolean error() {
        if(new Random().nextInt(100) < 40) return true;
        return false;
    }

    private static void sendAck() throws IOException {
        //System.out.println(">>>>>>>>>>>>>>>>>>>>>"+(System.currentTimeMillis()-startTime));
        startTime = System.currentTimeMillis();

        int reply=-1;
        int i;
        for(i=1; i<=received.length; i++) {
            if(received[i]==false) {
                reply = i*1000;
                break;
            }
        }
        System.out.println(received.length);
        if(reply==-1) reply = count*1000+1000;


        outToClient.writeBytes(""+reply+"\n");
        System.out.println("\n\n>>>>Cumulative Ack sent: " +reply);

       
    }

}
