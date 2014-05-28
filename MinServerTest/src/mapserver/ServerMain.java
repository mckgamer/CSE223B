package mapserver;

import java.net.InetAddress;

public class ServerMain {

	private InetAddress serverAddress;
	private int serverPort;
	public int hosting = 0;
	
	public ServerMain(InetAddress address, int port) {
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
