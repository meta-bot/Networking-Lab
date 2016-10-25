package lab3;

/**
 *
 * @author student
 */
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    
    int threadID,option;
    ArrayList<server> allServers;
    Scanner sc;
    String query,retIP;
    Queue childs[];
    com a;
    net b;
    org c;
    root e;
        
    
    public Server() throws Exception {
        
        threadID=0;
        retIP="";
        
        DatagramSocket serverSocket = new DatagramSocket(9876);

        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];
        
        
        
        
        
//        for(server ab: allServers){
//            System.out.println(ab);
//        }
        
        while (true) {
            retIP="";
            option=0;
            setUpServers();

        //    DatagramPacket receivePacket= new DatagramPacket(receiveData, receiveData.length);
            
       //     serverSocket.receive(receivePacket);
       //     query = new String(receivePacket.getData());

       //     InetAddress IPAddress = receivePacket.getAddress();
           

        //    int port = receivePacket.getPort();
            
            query="www.slideshare.net";
            System.out.println(query);
            
            //here should start thread
            allServers.get(4).start();
            
            Thread.sleep(2000);
            System.out.println("main "+retIP);
        //    sendData = retIP.getBytes();

        //    DatagramPacket sendPacket= new DatagramPacket(sendData, sendData.length, IPAddress, port);

        //    serverSocket.send(sendPacket);
        }
        
        
    }
    
    public void setUpServers(){
        try {
            sc= new Scanner(new File("server.txt"));
        } catch (FileNotFoundException ex) {
            
        }
        allServers= new ArrayList<server>();
        
        childs= new Queue[10];
        
        childs[0]= new LinkedList();
        childs[0].add(1);
        childs[0].add(2);
        childs[0].add(3);
        allServers.add(new server(-1,childs[0],"host"));
        
        
     //   System.out.println(childs.peek()+" ");
              
          
       // childs[].clear();
        childs[1]= new LinkedList();
        childs[1].add(4);
        childs[1].add(5);
        allServers.add(new server(0,childs[1],".com"));
     //   childs.clear();
        
        childs[2]= new LinkedList();
        allServers.add(new server(0,childs[2],".org"));
        
        childs[3]= new LinkedList();
        childs[3].add(6);
        allServers.add(new server(0,childs[3],".net"));
      //  childs.clear();
        
        childs[4]= new LinkedList();
        allServers.add(new server(1,childs[4],".x.com"));
        
        childs[5]= new LinkedList();
        allServers.add(new server(1,childs[5],".y.com"));
        
        childs[6]= new LinkedList();
        allServers.add(new server(3,childs[6],".z.net"));
        
        while(sc.hasNext()){
          //  int idx= sc.nextInt();
            String tmp=sc.nextLine();
            int idx= Integer.parseInt(tmp);
            String host= sc.nextLine();
            String ip= sc.nextLine();
            
            allServers.get(idx).addHost(host,ip);
        }
    }
    
    class address {
        String host,ip;

        public address(String host,String ip) {
            this.host=host;
            this.ip=ip;
        }
        
        
    }
    
    class server extends Thread{
        ArrayList <address> allHosts;
        ArrayList <Integer> dupChild;
        Queue child;
        int parent;
        String name;
        
        public server(int parent, Queue child, String name){
            this.parent=parent;
            this.child=child;
            this.name=name;
            
            //System.out.println("cons "+this.child.peek());
            
            allHosts= new ArrayList<address>();
            dupChild= new ArrayList<Integer>();
            
            while(!child.isEmpty()){
                //System.out.println("here");
                
                dupChild.add((Integer) child.peek());
                child.remove();
            }
        }
        
        @Override
        public void run(){
            
            System.out.println("in "+name+ " ip: "+retIP+" option: "+option);
            
            for (address hosts : allHosts) {
                if(query.contains(hosts.host)){
                    System.out.println("ip found");
                    retIP= hosts.ip;
                }
            }
            
            try {
                sleep(100);
            } catch (InterruptedException ex) {
                
            }
            
            if(retIP.length()==0){
                System.out.println("ip length 0");
                if(parent!=-1){
                    for (Integer node: dupChild) {
                        if(query.contains(allServers.get(node).name)){
                            allServers.get(node).start();
                            recreate();
                            return;
                        }
                    }
                    
                    if(option==1)
                        option=2;
                    allServers.get(parent).start();
                    recreate();
                    return;
                }
                else{
                    if(option==0){
                        for(int i=1;i<dupChild.size();i++){
                            int node= dupChild.get(i);
                            
                            if(query.contains(allServers.get(node).name)){
                                option=1;
                                allServers.get(node).start();
                                recreate();
                                return;
                            }
                        }
                        retIP="not found";
                        option=3;
                        allServers.get(1).start();
                        recreate();
                        return;
                    }
                    else if(option==2){
                        retIP="not found";
                        option=3;
                        allServers.get(1).start();
                        recreate();
                        return;
                    }
                }
            }
            else{
                System.out.println("here ip got");
                if(parent==-1){
                    option=3;
                    allServers.get(1).start();
                    recreate();
                    return;
                }
                else if(option==0){
                    if(allServers.get(4).name.equals(name)){
                        recreate();
                        return;
                    }
                    else if(parent==1){
                        option=3;
                        allServers.get(4).start();
                        recreate();
                        return;
                    }
                }
                else if(option==1 || option==2){
                    option=2;
                    System.out.println("root restarting");
                    allServers.get(parent).start();
                    recreate();
                    return;
                }
                else if(option==3){
                    if(allServers.get(4).name.equals(name)){
                        recreate();
                        return;
                    }
                    else{
                        allServers.get(4).start();
                        recreate();
                        return;
                    }
                }
            }
            
            //re-create thread
            recreate();
        }
        
        void recreate(){
            for(int i=0;i<allServers.size();i++){
                if(allServers.get(i).name.equals(name)){
                    allServers.set(i, new server(parent,childs[i],name));
                }
            }
        }
        
        public void addHost(String hostString, String ipString){
            allHosts.add(new address(hostString,ipString));
        }

        @Override
        public String toString() {
            
            String str ="";
            str+="Name: "+name+" parent: "+parent+" child: ";
            
            if(child.isEmpty())
                str+="empty";
            
            while(!child.isEmpty()){
                //System.out.println("here");
                
                str+=(child.peek()+" ");
                child.remove();
            }
            str+="\n";
            
            for (address hosts : allHosts) {
                str+=hosts.host+" "+hosts.ip+"\n";
            }
            return str;
        }
        
        
    }
    
    class com extends Thread{
        int id;
        String identifier=".com";
        String parent= "root";
        
        String child[]={"none"};
        
        String host[]={"www.real-timenews.com","www.prothom-alo.com","www.dailyjanakantha.com"};
        String ip[]={"104.28.19.196","45.64.64.216","45.63.122.154"};
        
        @Override
        public void run(){
            
            System.out.println("in com run");
            
            System.out.println("in com "+retIP);
            if(retIP.length() > 0)return;
            
            
            for(int i=0;i<3;i++){
             //   System.out.println(query+" "+host[i]+" "+"done");
                if(query.contains(host[i])){
                  //  System.out.println("true");
                    retIP= ip[i];
                }
            }
            
            if(retIP.length() == 0)e.start();
                /*
                    try {
                        notifyAll();
                        b.wait();
                        c.wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                */
            
        }
    }
    
    class root extends Thread{
        int id;
        String identifier="root";
        String parent= "none";
        
        String child[]={".com",".net",".org"};
        
     //   String host[]={"www.real-timenews.com","www.prothom-alo.com","www.dailyjanakantha.com"};
     //   String ip[]={"104.28.19.196","45.64.64.216","45.63.122.154"};
        
        @Override
        public void run(){
            
            System.out.println("in root run");
            
            System.out.println(retIP);
            if(retIP.length()>0){
                com aa = new com();
                aa.start();
                return;
            }
            for(int i=0;i<3;i++){
                if(query.contains(child[i])){
//                    notifyAll();
                    if(i==0){
                        new com().start();
                    }
                    else if(i==1){
                        
                        new net().start();
                    }
                    else if(i==2){
                        
                        new org().start();
                    }
                }
            }
            
        }
    }
    
    class net extends Thread{
        int id;
        String identifier=".net";
        String parent= "root";
        
        String child[]={"none"};
        
        String host[]={"www.slideshare.net","www.bdlan.net"};
        String ip[]={"144.2.1.10","103.204.208.202"};
        
        @Override
        public void run(){
            
            System.out.println("in net run");
            
            for(int i=0;i<2;i++){
             //   System.out.println(query+" "+host[i]+" "+"done");
                if(query.contains(host[i])){
                //    System.out.println("true in net");
                    retIP= ip[i];
                }
            }
            if(retIP.length() == 0)retIP = "SORRY NOT FOUND";
            
            new root().start();
            
            /*
            try {
                        notifyAll();
                        a.wait();
                    
                        c.wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    */
        }
    }
    
    class org extends Thread{
        int id;
        String identifier=".org";
        String parent= "root";
        
        String child[]={"none"};
        
        String host[]={"uva.onlinejudge.org","sportprogramming.blogspot.org"};
        String ip[]={"92.222.246.128","74.125.200.132"};
        
        @Override
        public void run(){
            System.out.println("in org run");
            for(int i=0;i<2;i++){
                if(query.contains(host[i])){
                    retIP= ip[i];
                }
            }
            
            if(retIP.length() == 0)retIP = "SORRY NOT FOUND";
            
            new root().start();
            
            /*
            try {
                        notifyAll();
                        b.wait();
                        a.wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    */
        }
    }
    
}
