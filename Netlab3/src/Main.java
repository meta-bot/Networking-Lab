import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
	static NameServer start;
	public static void createDNS() {
		NameServer root = new NameServer(null,"","1");
		NameServer com = new NameServer(root,".com","2");
		start = new NameServer(com,".nsc1.com","3");
		start.add("x", "3", null);
		start.add("y", "4", null);
		start.add("z", "5", null);
		NameServer nsc2 = new NameServer(com,".nsc2.com", "6");
		nsc2.add("a", "7", null);
		nsc2.add("b", "8", null);
		nsc2.add("c", "9", null);
		NameServer net = new NameServer(root,".net","10");
		net.add("facebook", "11", null);
		net.add("wikipedia", "12", null);
	}
	
	public static void main(String args[]) {
		createDNS();
		ServerSocket serverSocket = null;
		Socket client = null;
		BufferedReader clientInput = null;
		DataOutputStream out = null;
		String received = null;
		
		try {
			serverSocket = new ServerSocket(1852);
			client = serverSocket.accept();
			System.out.println("got someone");
			clientInput = new BufferedReader(new InputStreamReader(client.getInputStream()));
			out = new DataOutputStream(client.getOutputStream());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		while(true) {
			try {
				received = clientInput.readLine();
				if(received == null)
					continue;
				System.out.println("Got "+received);
				start.request(received);
				received = start.getReply();
				System.out.println("Sending "+received);
				out.writeBytes(received);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
