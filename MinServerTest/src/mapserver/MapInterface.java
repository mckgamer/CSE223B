package mapserver;

import java.rmi.RemoteException;

public interface MapInterface extends java.rmi.Remote {

	public String giveMe(int x, int y) throws RemoteException;
	
	public void setMe(int x, int y, String c) throws RemoteException;
	
	public void iExist(ServerMain cs) throws RemoteException;
	
	public String prepare(int N, String V) throws RemoteException;
	
	public void accept(int N, String V) throws RemoteException;
}
