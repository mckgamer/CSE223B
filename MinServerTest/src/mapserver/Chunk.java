package mapserver;

import java.net.InetAddress;


public class Chunk {
	
	private InetAddress serverAddress;
	private int serverPort;
	
	public Chunk(InetAddress address, int port) {
		this.serverAddress = address;
		this.serverPort = port;
	}
	
	public InetAddress getServerAddress() {
		return serverAddress;
	}
	public void setServerAddress(InetAddress serverAddress) {
		this.serverAddress = serverAddress;
	}
	public int getServerPort() {
		return serverPort;
	}
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}
	
	

}
