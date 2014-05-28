package server;
/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import mapserver.MapInterface;
import mapserver.MapServer;
import mapserver.MapServer_Stub;
import shared.LogFile;
 
public class Server extends UnicastRemoteObject implements ChunkServer{

    protected Server() throws RemoteException {
	}

	private static JFrame display;
    private static List<ServerThread> servers = new ArrayList<ServerThread>();
	static int listenPort = 4440;
	static int transferPort = 5550;
	static final int NUM_THREADS = 1;
	public static LogFile log = new LogFile("Server");
	
    public static void main(String[] args) throws IOException {
    	//Create a bunch of server threads
    	
    	try 
	    { 
    		System.setSecurityManager(new RMISecurityManager());
	       MapInterface obj = (MapInterface) Naming.lookup( "rmi://" + 
	            "localhost" + 
	            "/MapServer");         //objectname in registry 
	       System.out.println(obj.giveMe(3, 3)); 
	    } 
	    catch (Exception e) 
	    { 
	       System.out.println("MapClient exception: " + e.getMessage()); 
	       e.printStackTrace(); 
	    } 
    	
    	//This automatically finds good port numbers to use
    	int threadsStarted = 0;
    	while (threadsStarted < NUM_THREADS) {
	    	try {
	    		servers.add(new ServerThread(listenPort, transferPort));
	    		threadsStarted++;
	    	} catch (IOException e) {
	    		listenPort++;
	    		transferPort++;
	    	}
    	}
    	
    	//servers.add(new ServerThread(4797, 4446));
    	
    	display = new JFrame();
    	display.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	display.setSize(640, 480);
    	display.addWindowStateListener(
			new WindowStateListener() {
				@Override
				public void windowStateChanged(WindowEvent e) {
					if(e.getID() == WindowEvent.WINDOW_CLOSED) {
						Server.close();
					}
					
				}
				
			}
		);

    	JPanel panel = new JPanel(new GridLayout(servers.size(), 0));
    	display.add(panel);
        for(ServerThread server : servers) {
        	server.start();
        	JPanel serverPanel = new JPanel(new FlowLayout());
        	JButton pingButton = new JButton(server.getName());
        	pingButton.addActionListener(new ServerActionListener(serverPanel, server));
        	serverPanel.add(pingButton);
        	panel.add(serverPanel);
        }

    	display.setVisible(true);
    }
    
    public static void close() {
    	log.println("Killing servers");
    	for(ServerThread server : servers) {
        	server.kill();
        }
    	log.close();
    }

	@Override
	public String host(int x, int y) throws RemoteException {
		try {
			ServerThread server = new ServerThread(listenPort, transferPort);
			servers.add(server);
			server.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listenPort+"";
	}
}