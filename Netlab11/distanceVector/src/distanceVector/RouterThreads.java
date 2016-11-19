package distanceVector;

import java.io.IOException;
import java.util.LinkedList;

public class RouterThreads extends Thread{
	int ID;
	DistanceVector dv;
	int inf = 1000;
	int[] portList = {2001,2002,2003,2004,2005,2006};
	int[][] cost = {{0,2,5,1,inf, inf},
			{2,0,3,2,inf,inf},
			{5,3,0,3,1,5},
			{1,2,3,0,1,inf},
			{inf,inf,1,1,0,2},
			{inf,inf,5,inf,2,0}};
	LinkedList<Integer> neighbourList[] = new LinkedList[6];
	public RouterThreads(int ID){
		this.ID = ID;
		for(int i = 0 ; i < 6 ; i ++) neighbourList[i] = new LinkedList<Integer>();
		for(int i = 0 ; i < 6 ; i++){
			for(int j = 0 ; j  < 6 ; j++){
				if(cost[i][j] != inf && cost[i][j] != 0){
					neighbourList[i].add(j);
				}
			}
		}
		
	}
	@Override
	public void run(){
		if(ID == 0)
			DistanceVector.startTime = System.currentTimeMillis();
		try {
			dv = new DistanceVector(ID, 6, portList[ID], neighbourList[ID], cost, portList);
			dv.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
