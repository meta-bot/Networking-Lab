package lab3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 *
 * @author student
 */
public class Client {
    private BufferedReader inFromUser;
    private DatagramSocket clientSocket;
    private InetAddress IPAddress;
    private byte[] sendData ;
    private byte[] receiveData;
    private String sentence;
    private DatagramPacket sendPacket;
    private DatagramPacket receivePacket;
    
    
    Client() throws SocketException, UnknownHostException{
        inFromUser = new BufferedReader(new InputStreamReader(System.in));
        clientSocket = new DatagramSocket();
        IPAddress = InetAddress.getByName("172.16.13.197"); 
        sendData = new byte[1024]; 
        receiveData= new byte[1024];
    }
    
    public void doIt() throws IOException{
        System.out.println("ENTER WEBADDRESS: ");
        String sentence = inFromUser.readLine(); 
        sendData = sentence.getBytes();
        
        sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876); 
        clientSocket.send(sendPacket); 
        receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket); 
        String modifiedSentence = new String(receivePacket.getData());
        
        System.out.println("FROM SERVER:" + modifiedSentence); 
        clientSocket.close();
    }
}
