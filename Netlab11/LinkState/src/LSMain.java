import java.io.IOException;
import java.util.Random;


public class LSMain {
	public static final int inf = 1<<30;
	int basePort = 2000;
	int n = 6;
	LinkState2 ls[];
	
	int graph[][] = {{0,2,3,inf,inf, inf},
			{2,0,4,1,inf,inf},
			{3,4,0,2,inf,inf},
			{inf,1,2,0,6,3},
			{inf,inf,inf,6,0,1},
			{inf,inf,inf,3,1,0}};
	
	public LSMain() throws IOException {
		//graph = randGraph(10);
		ls = new LinkState2[n];
		for(int i=0;i<n;i++) {
			ls[i] = new LinkState2(i+basePort,n);
		}
		for(int i=0;i<n;i++)
			for(int j=i;j<n;j++)
				if(/*!(graph[i][j]==0)&&*/!(graph[i][j]==inf)) {
					ls[i].add(j, graph[i][j]);
					ls[j].add(i, graph[i][j]);
				}
		for(int i=0;i<n;i++)
			ls[i].start();
	}
	
	public static void main(String args[]) throws IOException {
		new LSMain();
	}
	
	public int[][] randGraph(int nodes) {
		Random random = new Random();
		int result[][] = new int[nodes][nodes];
		for(int i=0;i<nodes;i++)
			for(int j=i+1;j<nodes;j++)
				result[i][j] = result[j][i] = random.nextInt(10)+1;
		return result;
	}
}
