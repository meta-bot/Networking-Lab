import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class LinkState2 extends Thread {
	public static final int inf = 1 << 30;
	public static final int pktSize = 256;
	DatagramSocket ds;
	DatagramPacket dp;
	
	long tym;
	
	HashSet<Integer> set;
	int Graph[][], SSSP[];
	ArrayList<Integer> neighbours;	//port number for neighbours
	int idPort;						//port number for self

	public LinkState2(int ID, int noOfNodes) throws IOException {
		this.idPort = ID;
		Graph = new int[noOfNodes][noOfNodes];
		neighbours = new ArrayList<Integer>();
		ds = new DatagramSocket(idPort);
		set = new HashSet<Integer>();
		set.add(idPort);
		initGraph();
	}

	private void initGraph() {							//empty graph, all infinite costs
		for (int i = 0; i < Graph.length; i++)
			for (int j = 0; j < Graph[i].length; j++)
				Graph[i][j] = inf;
	}

	public void add(int j, int cost) {
		Graph[idPort - 2000][j] = Graph[j][idPort - 2000] = cost;	//add to self and store neighbour port
		neighbours.add(2000 + j);
	}

	private void sendGraph() throws IOException {
		String data = "";
		data += idPort + " ";
			for (int j = 0; j < Graph[0].length; j++)
				data += Graph[idPort-2000][j] + " ";					//create a string that contains the entire current state of the graph
		while (data.length() < pktSize)
			data += " ";											//space padding for certainty
		// System.out.println(data + "\n" + idPort);
		for (int x : this.neighbours) {
			DatagramPacket send = new DatagramPacket(data.getBytes(), pktSize, InetAddress.getByName("localhost"), x);
			ds.send(send);
		}
	}

	private boolean receiveGraph() throws IOException {
		boolean changed = false;
		byte bdata[] = new byte[pktSize];
		dp = new DatagramPacket(bdata, pktSize);
		ds.receive(dp);
		String str = new String(dp.getData());
		Scanner S = new Scanner(str);
		int cost, port = S.nextInt();
		//for (int i = 0; i < Graph.length; i++)
			for (int j = 0; j < Graph[0].length; j++) {
				//System.out.println("reading " + i + "," + j + " in " + idPort);
				cost = S.nextInt();
				//System.out.println(cost + " " + idPort);
				if (cost != inf && cost != Graph[port-2000][j]) {
					changed = true;
					Graph[port-2000][j] = cost;
				}
			}
		S.close();
		set.add(port);
		if(!changed) return changed;
		
		for (int x : this.neighbours) {
			if(x==port) continue;
			DatagramPacket send = new DatagramPacket(str.getBytes(), pktSize, InetAddress.getByName("localhost"), x);
			ds.send(send);
		}
		
		return changed;
	}

	private void broadcastGraph(int seconds) throws IOException {
		ds.setSoTimeout(1000);
		long start = System.currentTimeMillis();
		boolean changed = true;
		sendGraph();
		while (set.size()<6/*System.currentTimeMillis() - start < (seconds * 1000)*/) {
			changed = receiveGraph();
		}
	}

	public void run() {
		while (true) {
			tym = System.currentTimeMillis();
			try {
				broadcastGraph(5);
			} catch (IOException e) {
				System.err.println("I guess " + idPort + " is done");
			}
			/*
			 * if(idPort==2000) { for(int i=0;i<Graph.length;i++) { for(int
			 * j=0;j<Graph[i].length;j++) System.out.print(Graph[i][j] + " ");
			 * System.out.println(); } }
			 */
			Dijkstra D = new Dijkstra(Graph);
			D.shortestPath(idPort - 2000);
			SSSP = D.getDistances();
			synchronized (System.out) {
				System.out.println((char) (idPort - 2000 + 'A') + "\n");
				for (int i = 0; i < SSSP.length-1; i++)
					System.out.println((char) (i + 'A') + " " + SSSP[i]);
				System.out.println();
				if(idPort == 2000)
					System.out.println("Convergence Time: " + (System.currentTimeMillis()-tym) + "ms");
			}
			
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
