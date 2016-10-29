/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcp.reno;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Random;

public class Server {

    public static void main(String[] args) throws IOException {

        int bufferSize = 18000, N = 1000;
        long curTime;
        final long timeOut = 500;

        ServerSocket server = new ServerSocket(6789);
        FileOutputClass fOut = new FileOutputClass();
        fOut.setFileName("out.txt");

        Socket socket = server.accept();
        BufferedReader dIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter dOut = new PrintWriter(socket.getOutputStream());
        HashMap<String, char[]> map = new HashMap<>();
        Random rand = new Random();

        dOut.write(String.valueOf(bufferSize) + "\n");
        dOut.flush();

        while (!dIn.ready()) {
            System.out.println("waiting.......");
        }

        

        String seqNumber = "";
        int desiredAckNumber = Integer.parseInt(dIn.readLine());
        System.out.println("Desired Ack Number "+desiredAckNumber);
        
        curTime = System.currentTimeMillis();
        while (true) {

            if (dIn.ready()) {
                seqNumber = dIn.readLine();
                //System.out.println("Desired Number "+ desiredAckNumber);
                if (seqNumber.equals("EOF")) {
                    break;
                }

                System.out.println("Client sequence Number: " + seqNumber);

                char dataFromClient[] = new char[N + 10];
                dIn.read(dataFromClient, 0, N);
                //String str = dataFromClient.toString();
                //fOut.writeFile(dataFromClient , N);
                if (rand.nextInt(100) > 20) {
                    if (!map.containsKey(seqNumber)) {
                        
                        map.put(seqNumber, dataFromClient);
                    }
                }

            }

            if (System.currentTimeMillis() - curTime > timeOut) {
                
                while(map.containsKey(String.valueOf(desiredAckNumber))){
                    char []arr = map.get(String.valueOf(desiredAckNumber));
                    fOut.writeFile(arr,N);
                    map.remove(String.valueOf(desiredAckNumber));
                    desiredAckNumber+=1000;
                }
                
                String dataToClient = String.valueOf(desiredAckNumber);
                dOut.write(dataToClient + "\n");
                dOut.flush();
                System.out.println("\n------------->" + dataToClient + "<-----------------");
                curTime = System.currentTimeMillis();
            }

        }

        fOut.closeFile();
    }

}
