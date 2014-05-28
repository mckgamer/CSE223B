package server;

import java.rmi.RemoteException;


public interface ChunkServer extends java.rmi.Remote {

	public String host(int x, int y) throws RemoteException;
	
}