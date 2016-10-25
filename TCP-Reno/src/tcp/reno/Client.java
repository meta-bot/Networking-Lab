/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcp.reno;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author anando
 */
public class Client {
    
    Client(String hostName){
        this.hostName = hostName;
    }
    
    
    class SaveData{
        private int seqNumber;
        private String data;
        SaveData(int a , String b){
            seqNumber = a; data = b;
        }
        public String getData(){return data;}
        public int getNum(){return seqNumber;}
    }
    
    private void setTimer(){
        if(timerOn)return;
        timerOn = true;
        startTime = System.currentTimeMillis();
    }
    
    private boolean timeExeed(){
        if(System.currentTimeMillis() - startTime > timeInterval)return true;
        return false;
    }
    private void calCW(){
        if(timeExeed){
            ssThresh = cwSize / 2;
            if(ssThresh == 0)ssThresh = 1;
            cwSize = 1;
            timeExeed = false;
        }
        else{
            if(cwSize < ssThresh){
                cwSize+=((ack - seqNumber)/1000);
                
            }
            else if(cwSize >= ssThresh){
                cwSize++;
            }
        }
    }
    
    public void doTask() throws FileNotFoundException, IOException{
        fIn = new FileInputClass();
        fIn.setFileName("in.txt");
        
        socket = new Socket(hostName , 6789);
        
        dIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        dOut = new PrintWriter(socket.getOutputStream());
        
        saveData = new SaveData[1000];
        
        BUFFERSIZE = Integer.parseInt(dIn.readLine());
        System.out.println("Buffer Size "+BUFFERSIZE);
        while(true){
            for(int xx = 0 ; xx < cwSize && !fIn.isEOF() && packOnFly < cwSize; xx++){
                String dataToSend = fIn.takeInput(MSS);
                while(dataToSend.length() < MSS)dataToSend+=" ";
                
                unAck += 1000;
                saveData[unAck/1000] = new SaveData(unAck , dataToSend);
                
                dOut.write(unAck + "\n");
                dOut.flush();
                dOut.write(dataToSend);
                dOut.flush();
                
                System.out.println("seg Number: "+unAck+"\n"+"data: "+dataToSend);
                packOnFly++;
                setTimer();
                
                if(dIn.ready()){
                    timerOn = false;
                    ack = Integer.parseInt(dIn.readLine());
                    packOnFly -= (ack - seqNumber)/1000;
                    seqNumber = ack;
                    calCW();
                    System.out.println("ACK received : "+ack+" CW = "+cwSize);
                    
                    if(ack == prevAck){
                        dupCount++;
                        if(dupCount == 3){
                            ssThresh >>=1;
                            if(ssThresh == 0)ssThresh = 1;
                            cwSize = ssThresh;
                            System.out.println("DUP COUNT 3. Fast recovery");
                        }
                    }else dupCount = 1;
                    prevAck = ack;
                    System.out.println("Duplicate ACK found");
                }
            }
            if(timeExeed()){
                if(fIn.isEOF()){
                    
                }
            }
        }
        
    }
    private String hostName;
    private int BUFFERSIZE;
    private final int MSS = 1000; //maximum segment size
    private int cwSize=1;
    private int unAck = 0;
    private int prevAck = -1;
    private int ack = -1;
    private int seqNumber = MSS;
    private int packOnFly = 0;
    private int dupCount = 1;
    private int ssThresh = 16;
    private long startTime;
    private long timeInterval;
    private boolean timerOn;
    private boolean timeExeed=false;
    private FileInputClass fIn;
    private BufferedReader dIn;
    private PrintWriter dOut;
    private Socket socket;
    private SaveData saveData[];
    private final double a = 0.125;
    private final double b = 0.25;
    private double estRTT = 700;
    private double sampleRTT = 700;
    private double devRTT;
}
