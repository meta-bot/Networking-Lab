package distanceVector;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;

public class DistanceVector {
	int ID;
	int noOfNodes;
	LinkedList<Integer> neighbourList;
	HashSet<Integer> set;
	int[][] cost;
	int[][] shortestPath;
	int[] portList;
	int port;
	
	public static long startTime,finishedTime,count=0;
	
	Boolean dataReceived = false;
	boolean stopped = false;
	boolean printed = false;
	
	DatagramSocket socket;
	DatagramPacket receivePacket;
	
	byte[] sendData = new byte[1024];
	byte[] receiveData = new byte[1024];
	
	
	public DistanceVector(int ID, int noOfNodes, int port, LinkedList<Integer> neighbourList, int[][]c
			, int portList[]) throws IOException{
		this.ID = ID;
		this.noOfNodes = noOfNodes;
		this.port = port;
		this.neighbourList = neighbourList;
		this.portList = portList;
		
		cost = new int[noOfNodes][noOfNodes];
		shortestPath = new int[noOfNodes][noOfNodes];
		
		set = new HashSet<Integer>();
		
		for(int i = 0 ; i < noOfNodes; i++){
			for(int j = 0 ; j < noOfNodes; j++){
				cost[i][j] = 1000;
				shortestPath[i][j] = 1000;
				if(i == j){
					shortestPath[i][j] = 0;
					cost[i][j] = 0;
				}
			}
		}
		
		//D x (y) = c(x,y)
		for(int x: this.neighbourList){
			cost[ID][x] = c[ID][x];
			shortestPath[ID][x] = c[ID][x];
		}
		
		socket = new DatagramSocket(this.port);
		
		(new inputThread()).start();
		
		sendData();
	}
	
	public void start() throws IOException{
		
		while(true){
			synchronized(dataReceived){
				if(dataReceived){
					dataReceived = false;
					boolean changed = false;
					for(int i = 0 ; i < noOfNodes; i++){
						if( i == ID) continue;
						int min = 1000;
						for(int x: neighbourList){
							min = Integer.min(min, cost[ID][x] + shortestPath[x][i]);
						}
						if(shortestPath[ID][i]!=min)
							changed = true;
						shortestPath[ID][i] = min;
					}
					if(changed) {
						sendData();
						if(!stopped)
							count++;
						stopped = true;
					}
					synchronized(System.out){
						System.out.println(ID + " :");
						for(int i = 0 ; i < noOfNodes; i ++){
							System.out.print(shortestPath[ID][i] + " ");
						}
						System.out.println();
					}
				}

				if(count == noOfNodes&&!printed) {
					System.out.println("Convergence Time: " + (System.currentTimeMillis()-startTime));
					printed = true;
					//break;
				}
				
			}
		}
	}
	
	void sendData() throws IOException{
		String sendDistanceVector = "" + ID + " ";
		for(int i = 0 ; i < noOfNodes; i++) sendDistanceVector += shortestPath[ID][i] + " ";
		
		for(int x: this.neighbourList){
			DatagramPacket sendPacket = new DatagramPacket(sendDistanceVector.getBytes(), sendDistanceVector.length()
					,InetAddress.getByName("localhost"), portList[x]);
			socket.send(sendPacket);
		}
	}
	void receiveData() throws IOException{
		receiveData = new byte[1024];
		receivePacket = new DatagramPacket(receiveData, receiveData.length);
		socket.receive(receivePacket);
		
		dataReceived = true;
		
		Scanner s = new Scanner(new String(receivePacket.getData()));
		int rid = s.nextInt();
		int index = 0;
		
		while(s.hasNextInt()){
			shortestPath[rid][index++] = s.nextInt();
		}
	}
	
	class inputThread extends Thread{
		public void run(){
			while(true){
				try {
					synchronized(dataReceived){
						if(!dataReceived)
							receiveData();
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
					
		}
	}
}
