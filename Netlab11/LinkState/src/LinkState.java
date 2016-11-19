import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Scanner;

public class LinkState extends Thread {
	public static final int inf = 1 << 30;
	public static final int pktSize = 1024;
	DatagramSocket ds;
	DatagramPacket dp;

	int Graph[][], SSSP[];
	ArrayList<Integer> neighbours;	//port number for neighbours
	int idPort;						//port number for self

	public LinkState(int ID, int noOfNodes) throws IOException {
		this.idPort = ID;
		Graph = new int[noOfNodes][noOfNodes];
		neighbours = new ArrayList<Integer>();
		ds = new DatagramSocket(idPort);
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
		for (int i = 0; i < Graph.length; i++)
			for (int j = 0; j < Graph[i].length; j++)
				data += Graph[i][j] + " ";							//create a string that contains the entire current state of the graph
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
		int cost;
		for (int i = 0; i < Graph.length; i++)
			for (int j = 0; j < Graph[i].length; j++) {
				//System.out.println("reading " + i + "," + j + " in " + idPort);
				cost = S.nextInt();
				//System.out.println(cost + " " + idPort);
				if (cost != inf && cost != Graph[i][j]) {
					changed = true;
					Graph[i][j] = Graph[j][i] = cost;
				}
			}
		S.close();
		return changed;
	}

	private void broadcastGraph(int seconds) throws IOException {
		ds.setSoTimeout(1000);
		long start = System.currentTimeMillis();
		boolean changed = true;
		while (System.currentTimeMillis() - start < (seconds * 1000)) {
			if (changed)
				sendGraph();
			changed = receiveGraph();
		}
	}

	public void run() {
		while (true) {
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
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
