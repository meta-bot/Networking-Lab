/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab7;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.sql.Time;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author student
 */
public class Client {

    int previous,dupCount; 
    ReadData[] readData=new ReadData[100];      //Storing sent packets
    boolean exceeded=false;
    Socket clientSocket;
    boolean timerRunning = false, endRun = false;
    int bufferSize;
    String sCurrentPack;
    char[] buffer;
    int c;
    char character;
    int segSize, segNum, cwSize, ackSeg, unAck;
    StringBuilder sb;
    int ssThreshold=16;
    int sendBase=1;
    int packOnFly=0;
    long startTime;
    //For timeout, cwSize=1;
    
    //Retransmit only for timeout
    double a=0.125;
    double b=0.25;
    double estRTT=700;
    double devRTT;
    double TimoutInterval=700;
    double sampleRTT=700;
    //double sampleRTT=curTime-startTime;
    //estRTT= (1-a)*estRTT+a*sampleRTT;
    //devRTT=(1-b)*devRTT+b*(sampleRTT-estRTT);
    
    Client(String hostname) throws IOException {
        String amount, balance;
        int id = 0, ackID = -1;
        String sentence;
        String modifiedSentence;
        previous=0;
        dupCount=1;
        clientSocket = new Socket(hostname, 6789);

        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        String bufferString = inFromServer.readLine();
        System.out.println("Buffer Size is " + bufferString);
        bufferSize = Integer.parseInt(bufferString);

        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream("input.txt"), Charset.forName("UTF-8")));

        } catch (IOException e) {
            e.printStackTrace();
        }
        int i;
        segNum = 1000;
        segSize = 1000;
        cwSize = 1;
        unAck = 0;
        packOnFly=0;
        sendBase=1;
        while (true) 
        {
            
            for (int j = 0; j < cwSize && !endRun && packOnFly<cwSize; j++) {
                //System.out.println("loop "+ j+" "+cwSize+" "+packOnFly);
                sCurrentPack = "";
                unAck += 1000;
                System.out.println("");
                for (i = 0; i < segSize && c != -1; i++) {
                    character = (char) c;
                    //sb.append(character);
                    sCurrentPack += character;
                    c = br.read();
                    if (c == -1) {
                        endRun = true;
                    }
                }
                while (i < segSize) {
                    sCurrentPack += " ";
                    i++;
                }
                if (i < segSize) {
                    System.out.println("ERR " + i);
                }
                System.out.println("Packet Seg : " + unAck);
                //System.out.println("Packet Sent : " + sCurrentPack);
                readData[unAck/1000]=new ReadData(unAck,sCurrentPack);
                //System.out.println("Builder Size "+sb.length());
                outToServer.writeBytes(unAck + "\n");
                outToServer.writeBytes(sCurrentPack);
                packOnFly++;
                setTimer(); 
                if(inFromServer.ready())
                {                  
                    timerRunning=false;
                   // System.out.println("Reading Server in loop");
                    ackSeg = Integer.parseInt(inFromServer.readLine());
                    System.out.println("ackSeg :" + ackSeg);
                    System.out.println(cwSize + " " + ackSeg + " " + segNum);
                    packOnFly-=(ackSeg-segNum)/1000;
                    cwSize = calCWSize();
                    System.out.println("Window : " + cwSize); 
                    segNum = ackSeg;
                    if(ackSeg==previous)
                    {
                        dupCount++;
                        if(dupCount==3)
                        {
                            cwSize=(int) Math.ceil(cwSize/2);
                            ssThreshold=cwSize;
                            if(ssThreshold==0)
                                ssThreshold=1;
                            System.out.println("Same Ack : DupCount "+dupCount+" cwSize "+cwSize+" ssThreshold "+ssThreshold);
                        }
                        System.out.println("Same Ack : DupCount "+dupCount);
                    }
                    else
                    {
                        dupCount=1;
                    }
                    previous=ackSeg;
                }
              
            }
            
            if(timerExceeded())
                {
                     if(endRun)
                    {
                        System.out.println("FIle Finished");
                        System.exit(0);
                    }
                    System.out.println("Timeout ! ");
                    exceeded=true;
                    cwSize=calCWSize();
                    int retransmit=segNum/1000;
                    outToServer.writeBytes(segNum+ "\n");
                    outToServer.writeBytes(readData[retransmit].data);
                    System.out.println("Retransmitted Seg "+segNum);
                    startTime=System.currentTimeMillis();
                }
             if(inFromServer.ready())
            {
               sampleRTT=System.currentTimeMillis()-startTime;
               estRTT= (1-a)*estRTT+a*sampleRTT;
                devRTT=Math.abs((1-b)*devRTT+b*(sampleRTT-estRTT));
               TimoutInterval=estRTT+4*devRTT;
               timerRunning=false;
               System.out.println("SampleRTT ="+sampleRTT +" timeoutInterval "+TimoutInterval);
               // System.out.println("Reading Server");
                ackSeg = Integer.parseInt(inFromServer.readLine());
                System.out.println("ackSeg :" + ackSeg);
                System.out.println(cwSize + " " + ackSeg + " " + segNum);
                packOnFly-=(ackSeg-segNum)/1000;
                
              
                if(ackSeg==previous)
                {
                    dupCount++;
                    System.out.print("Same Ack : DupCount "+dupCount);
                    if(dupCount==3)
                    {
                        cwSize=(int) Math.ceil(cwSize/2);
                        if(cwSize==0)
                            cwSize=1;
                        ssThreshold=cwSize;
                        if(ssThreshold==0)
                            ssThreshold=1;
                        System.out.print(" cwSize "+cwSize+" ssThreshold "+ssThreshold);
                    }
                    System.out.println("");

                }
                else
                {
                    dupCount=1;
                    cwSize = calCWSize();
                    System.out.println("Window Size : " + cwSize+ " SSThreshold "+ssThreshold);
                }
                segNum = ackSeg;
                previous=ackSeg;
            }
                      
            if (cwSize < 0) {
                break;
            }
        }
        System.out.println("Thanks for using our service. Good Bye");
        outToServer.writeBytes("EOF" + "\n");
        if (br != null) {
            br.close();
        }
        clientSocket.close();
    }

    void setTimer() {
        if (timerRunning == true) {
            return;
        } else {
            startTime = System.currentTimeMillis();
        }

    }
    
    boolean timerExceeded()
    {
        boolean condition=false;
        if(System.currentTimeMillis()-startTime>TimoutInterval)
            condition=true;
        return condition;
        //return false;
    }
    
   int calCWSize() {
       if(ssThreshold==0)
           ssThreshold=1;
        int val = 0;
        if (exceeded == true) {
            ssThreshold = (int) Math.ceil(ssThreshold / 2);
             int halfCW=(int) Math.ceil(cwSize/2);
            if(halfCW>ssThreshold)
                ssThreshold=halfCW;
            if(ssThreshold==0)
                ssThreshold=1;
            cwSize = 1;
            val=cwSize;
            exceeded=false;
            System.out.println("SSThreshold Reduced for Timeout.SSThreshold : "+ssThreshold);
            System.out.println("CWSize Reduced for Timeout.Window Size : "+val);
        } else {
            if (cwSize < ssThreshold) {
                
                val = cwSize + ((ackSeg - segNum) / 1000);
                System.out.println("Mode : Slow Start ");
            } else if (cwSize >= ssThreshold) {

                val=cwSize+1;
                System.out.println("Mode : Congestion Avoidance "+val);
            }
        }

        return val;
    }
    
    
   class ReadData
   {
       String data;
       int segNum;
       ReadData(int segNum,String data)
       {
           this.data=data;
           this.segNum=segNum;
       }
   }
}
