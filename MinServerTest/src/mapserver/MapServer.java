package mapserver;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;

import server.ChunkServer;

public class MapServer extends UnicastRemoteObject implements MapInterface  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected MapServer() throws RemoteException {
		//super();
		// TODO Auto-generated constructor stub
	}

	public static DatagramSocket socket = null;
	
	
	public static final int MAPSIZE = 10;
	public int myID = 0;
	
	ArrayList<ServerMain> allMapServers = new ArrayList<ServerMain>();
	
	static ChunkMap map = new ChunkMap();
	ArrayList<ServerMain> serverList = new ArrayList<ServerMain>();
	static Queue<ServerMain> eligibleList = new PriorityQueue<ServerMain>();
	
	public static void main(String[] args) throws SocketException {

		ChunkMap map = new ChunkMap();
		/*
		while (Math.random()<.95) {
		map.setChunk((int)(Math.random()*10), (int)(Math.random()*10), new Chunk(null, 0));
		}*/
		
		
		map.drawChunkMap();
		
		try 
        { 
            MapServer obj = new MapServer(); 
            // Bind this object instance to the name "HelloServer" 
            Registry r = LocateRegistry.getRegistry();
            r.bind("MapServer",  new MapServer());

        } 
        catch (Exception e) 
        { 
            System.out.println("MapServer error: " + e.getMessage()); 
            e.printStackTrace(); 
        } 
		
		// I download server's stubs ==> must set a SecurityManager 
	    System.setSecurityManager(new RMISecurityManager());

	}
    
	@Override
	public String giveMe(int x, int y) throws RemoteException {
		long start = System.nanoTime();
		if (map.getChunk(x, y) != null) {
			System.out.println("ReadDelay:"+(System.nanoTime()-start));
			return map.getChunk(x, y).getServerAddress().toString();
		} else if (myID == 0) { //im a primary
			boolean accept = false;
			while (!accept) {
				ServerMain contact = eligibleList.remove();
				//ask contact to host chunk x, y
				try 
			    { 
			       ChunkServer obj = (ChunkServer) Naming.lookup( "//" + 
			            contact.getServerAddress().toString() + 
			            "/ChunkServer");         //objectname in registry 
			       String result = obj.host(x, y); 
			       int usePort = 8903;
			       map.setChunk(x, y, new Chunk(InetAddress.getByName(result), usePort));
			       contact.hosting++;
			       eligibleList.add(contact);
			       
			       for (ServerMain s : serverList) {
			    	   MapInterface secM = (MapInterface) Naming.lookup( "//" + 
				            s.getServerAddress().toString() + 
				            "/MapServer");         //objectname in registry 
				       secM.setMe(x,y,result); 
				       map.setChunk(x, y, new Chunk(InetAddress.getByName(result), usePort));
			       }
			       
			    } 
			    catch (Exception e) 
			    { 
			       System.out.println("MapClient exception: " + e.getMessage()); 
			       e.printStackTrace(); 
			    }
				
					
			}
			System.out.println("PrimaryWriteDelay:"+(System.nanoTime()-start));
			return map.getChunk(x,y).getServerAddress().toString();
		} else {
		    try 
		    { 
		       MapServer obj = (MapServer) Naming.lookup( "//" + 
		            "localhost" + 
		            "/MapServer");         //objectname in registry 
		       System.out.println("SecondWriteDelay:"+(System.nanoTime()-start));
		       return obj.giveMe(x, y); 
		    } 
		    catch (Exception e) 
		    { 
		       System.out.println("MapClient exception: " + e.getMessage()); 
		       e.printStackTrace(); 
		    } 
		}
		System.out.println("FailedDelay:"+(System.nanoTime()-start));
		return null;
		
	}


	@Override
	public void setMe(int x, int y, String c) throws RemoteException {
		long start = System.nanoTime();
		try {
			map.setChunk(x, y, new Chunk(InetAddress.getByName(c),0));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("SetDelay:"+(System.nanoTime()-start));
	}

	@Override
	public void iExist(ServerMain cs) throws RemoteException {
		if (!serverList.contains(cs)) {
			serverList.add(cs);
			eligibleList.add(cs);
		}
		
	}

	private int highestN = 0;
	private String myV = "";
	
	@Override
	public String prepare(int N, String V) throws RemoteException {
		if (N > highestN) {
			if (!myV.equals("")) {
				return myV;
			} else {
				return "";
			}
		}
		return null;
	}

	@Override
	public void accept(int N, String V) throws RemoteException {
		if (N == highestN && myV.equals(V)) {
			myV = V;
		}
	}

}
