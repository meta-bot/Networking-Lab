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

    Client(String hostName) {
        this.hostName = hostName;
    }

    class SaveData {

        private int seqNumber;
        private String data;

        SaveData(int a, String b) {
            seqNumber = a;
            data = b;
        }

        public String getData() {
            return data;
        }

        public int getNum() {
            return seqNumber;
        }
    }

    private void setTimer() {
        if (timerOn) {
            return;
        }
        timerOn = true;
        startTime = System.currentTimeMillis();
    }

    private boolean timeExeed() {
        if (System.currentTimeMillis() - startTime > timeInterval) {
            return true;
        }
        return false;
    }

    private void calCW() {
        if (timeExeed) {
            ssThresh = cwSize / 2;
            if (ssThresh == 0) {
                ssThresh = 1;
            }
            cwSize = 1;
            timeExeed = false;
        } else if (cwSize < ssThresh) {
            cwSize += ((ack - seqNumber) / 1000);

        } else if (cwSize >= ssThresh) {
            cwSize++;
        }
    }

    public void doTask() throws IOException {
        fIn = new FileInputClass();
        fIn.setFileName("input.txt");

        socket = new Socket(hostName, 6789);
        System.out.println("Client Connection Established...");

        dIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        dOut = new PrintWriter(socket.getOutputStream());

        saveData = new SaveData[1000];

        BUFFERSIZE = Integer.parseInt(dIn.readLine());
        System.out.println("Buffer Size : " + BUFFERSIZE);
        dOut.write(seqNumber + "\n"); dOut.flush();

        while (true) {
            for (int xx = 0; xx < cwSize && !fIn.isEOF() && packOnFly < cwSize; xx++) {
                String dataToSend = fIn.takeInput(MSS);
                while (dataToSend.length() < MSS) {
                    dataToSend += " ";
                }

                unAck += MSS;
                dOut.write(unAck + "\n");
                dOut.flush();
                char arr[] = dataToSend.toCharArray();
                dOut.write(arr, 0, dataToSend.length());
                dOut.flush();

                System.out.println("Data Send... Seq Number:(unAck) " + unAck);
                System.out.println("(first) CWSIZE : = "+cwSize);
                System.out.println("pack on fly "+packOnFly);
                saveData[unAck / 1000] = new SaveData(unAck, dataToSend);
                packOnFly++;
                setTimer();

                if (dIn.ready()) {
                    timerOn = false;
                    ack = Integer.parseInt(dIn.readLine());
                    System.out.println("ACK received (first) -> "+ack);
                    calCW();
                    System.out.println("--CW SIZE : " + cwSize);
                    System.out.println("--pack on fly "+packOnFly);
                    System.out.println("\n\n");
                    packOnFly -= (ack - seqNumber) / 1000;
                    
                    if (prevAck == ack) {
                        dupCount++;
                        if (dupCount == 3) {
                            cwSize >>= 1;
                            //cwSize = cwSize == 0 ? 1 : cwSize;
                            ssThresh = cwSize;
                            if (ssThresh == 0) {
                                ssThresh = 1;
                            }
                            System.out.println("tripple duplicate... CWSIZE: " + cwSize + " ssThresh " + ssThresh);
                        }
                    } else {
                        dupCount = 1;
                    }
                    prevAck = ack;
                    seqNumber = ack;
                }
            }
            if (timeExeed()) {
                if (fIn.isEOF()) {
                    System.out.println("FIle End");
                    //System.exit(0);
                    break;
                }
                System.out.println("TIMEOUT !!!!");
                timeExeed = true;
                calCW();
                char arr[] = saveData[seqNumber / 1000].getData().toCharArray();
                dOut.write(seqNumber + "\n");
                dOut.flush();
                dOut.write(arr, 0, MSS);
                dOut.flush();

                System.out.println("Retransmitted Segment ... " + seqNumber);
                startTime = System.currentTimeMillis();
            }
            if (dIn.ready()) {
                ack = Integer.parseInt(dIn.readLine());
                //System.out.println(ack);
                System.out.println("ACK received (second) -> "+ack);
                System.out.println("prev ack number(seqNumber) "+seqNumber);
                packOnFly -= (ack - seqNumber) / 1000;
                timerOn = false;
                calCW();
                System.out.println("CWSIZE : " + cwSize+" ssThresh "+ssThresh);

                sampleRTT = System.currentTimeMillis() - startTime;
                estRTT = (1 - a) * estRTT + a * sampleRTT;
                devRTT = Math.abs((1 - b) * devRTT + b * (sampleRTT - estRTT));
                timeInterval = (long) (estRTT + 4 * devRTT);

                System.out.println("SampleRTT: " + sampleRTT);
                System.out.println("estRTT: " + estRTT);
                System.out.println("devRTT: " + devRTT);
                System.out.println("timeInterval: " + timeInterval);
                System.out.println("\n\n");

                if (prevAck == ack) {
                    dupCount++;
                    if (dupCount == 3) {
                        cwSize >>= 1;
                        //cwSize = cwSize == 0 ? 1 : cwSize;
                        ssThresh = cwSize;
                        if (ssThresh == 0) {
                            ssThresh = 1;
                        }
                        System.out.println("tripple duplicate... CWSIZE: " + cwSize + " ssThresh " + ssThresh);
                    }
                } else {
                    dupCount = 1;
                    //calCW();
                }
                prevAck = ack;
                seqNumber = ack;

            }
            if(cwSize < 0)break;
        }
        System.out.println("TATA BYE BYE....");
        dOut.write("EOF"+"\n");
        dOut.flush();
        socket.close();
    }
    private String hostName;
    private int BUFFERSIZE;
    private final int MSS = 1000; //maximum segment size
    private int cwSize = 1;
    private int unAck = 0;
    private int prevAck = -1;
    private int ack = -1;
    private int seqNumber = MSS;
    private int packOnFly = 0;
    private int dupCount = 1;
    private int ssThresh = 16;
    private long startTime;
    private long timeInterval=700;
    private boolean timerOn=false;
    private boolean timeExeed = false;
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
