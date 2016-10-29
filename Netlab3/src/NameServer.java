import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

public class NameServer implements Runnable {
	private NameServer parent;
	private Thread t;
	private String name;
	private String IP;
	private ArrayList<Mapping> map;
	private LinkedList<String> requestBuffer,replyBuffer;
	private String requestMessage = null;

	class Mapping {
		String name, IP;
		NameServer ns;

		public Mapping(String name, String IP, NameServer ns) {
			this.name = name;
			this.IP = IP;
			this.ns = ns;
		}

		public Mapping(String name, String IP) {
			this(name, IP, null);
		}
	}

	public NameServer(NameServer parent, String name, String IP) {
		map = new ArrayList<Mapping>();
		this.name = name;
		this.IP = IP;
		map.add(new Mapping(name, IP));
		this.parent = parent;
		if (parent != null) {
			parent.add(name, IP, this);
		}
		requestBuffer = new LinkedList<String>();
		replyBuffer = new LinkedList<String>();
		t = new Thread(this, name);
		t.start();

	}

	public void add(String name, String IP, NameServer ns) {
		map.add(new Mapping(name+this.name, IP, ns));
	}

	@Override
	public void run() {
		while (true) {
			synchronized (requestBuffer) {
				while (requestBuffer.isEmpty()) {
					try {
						requestBuffer.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					//requestMessage = null;
				}
				requestMessage = requestBuffer.removeFirst();
				//requestBuffer.notify();
			}
			boolean found = false;
			for (Mapping x : map) {
				if (x.name.equals(requestMessage)) {
					
					reply(x.IP+"\n");
					found = true;
					break;
				}
			}
			if (found == true) {
				requestMessage = null;
				continue;
			}
			for (Mapping x : map) {
				if (requestMessage.contains(x.name) && x.ns != null) {
					x.ns.request(requestMessage);
					reply(x.ns.getReply());
					found = true;
					break;
				}
			}
			if (found == true) {
				requestMessage = null;
				continue;
			}
			if(parent==null) {
				reply("Domain Not Found\n");
			}
			else {
				parent.request(requestMessage);
				reply(parent.getReply());
			}
		}
	}

	private void reply(String str) {
		synchronized(replyBuffer) {
			replyBuffer.addFirst(str);
			replyBuffer.notifyAll();
		}
	}
	
	public String getReply() {
		while(true) {
			synchronized(replyBuffer) {
				while(replyBuffer.isEmpty())
					try {
						replyBuffer.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				return replyBuffer.removeFirst();
			}
		}
	}

	public void request(String requestMessage) {
		synchronized (requestBuffer) {
			if(requestMessage!=null) 
				requestBuffer.addFirst(requestMessage);
			requestBuffer.notifyAll();
		}
	}
}
