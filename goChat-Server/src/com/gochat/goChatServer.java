/**
 * File Name				: goChatServer.java
 * 
 * Author					: Gowtham Sathiyanarayanan
 * 
 * UTA ID					: 1000991932
 * 
 * Subject					: Distributed System
 * 
 * Input					: No dependent input to start this class
 * 	
 * Supported requirements	: java to be installed
 * 
 * Class Name				: goChatServer
 * 
 * Functional description	: This class runs on port 80 and act as an server to the process incoming request from clients.
 * 						   	  It keeps listening for any incoming request and establish a socket connection for each request to facilitate chat between clients.
 * 						  	  When the request comes in, they establish the connection and process each individuals parallel in a separate thread using goChatServerThread class.
 * 						  	  They instantiate goChatServerGUI class when they get started. 
 * 
 * Assumptions				: Port 80 is free and available to instantiate server up on it
 */

package com.gochat;
import java.io.IOException;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Purpose	:This class is used to start the server in port 80 to accept the incoming request and establish connection
 * 
 * @author Gowtham Sathiyanaryanan
 * @version	Original
 * @see http://download.java.net/jdk7/archive/b123/docs/api/java/net/ServerSocket.html
 *
 */
public class goChatServer {

	public static ArrayList<Socket> connectedSockets = new ArrayList<Socket>();
	public static ArrayList<String> connectedUsers = new ArrayList<String>();
	public static ArrayList<String> visibleUsers = new ArrayList<String>();
	public static ArrayList<String> connectedListPair = new ArrayList<String>();
	public static Map<String,Socket> connectedUserandSocket = new HashMap<String,Socket>();
	public static Map<String,String> connectedPair = new HashMap<String,String>();
	public static goChatServerGUI serverGUI;
	
	/**
	 * Purpose	:	Starts when program is instantiated and keep listening the port for incoming connection. If any, establish connection and process them in a separate thread by calling goChatServerThread class instance.
	 * 				Instantiate goChatServerGUI which displays server GUI of message traffic
	 * @param args
	 * @throws IOException
	 */
	public static void main(String args[]) throws IOException{
		try{
			final int Port = 80;
			ServerSocket Server = new ServerSocket(Port);
			System.out.println("Connection open. Waiting for client");
			serverGUI = new goChatServerGUI();
			serverGUI.runGUI();
			
			while(true){
				Socket Sock = Server.accept();
				connectedSockets.add(Sock);
				
				System.out.println("Client connected from :"+Sock.getLocalAddress().getHostName()+"-"+Sock.getLocalSocketAddress());
				
				addUser(Sock);
				
				goChatServerThread chat = new goChatServerThread(Sock);
				Thread t = new Thread(chat);
				t.start();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Method Name	:	addUser
	 * Purpose		:	From the incoming connection, it get the name of client from the connected stream and add them to list of user with their corresponding socket.
	 * 					It Checks the visibility set from user and if user request contains value 1 on it then add them to visibleUser list to show others list of visible users.
	 * 
	 * @param Sock
	 * @throws IOException
	 */
	public static void addUser(Socket Sock) throws IOException{
		Scanner input = new Scanner(Sock.getInputStream());
		String username = input.nextLine();
		if(username.charAt(0)=='1')
			visibleUsers.add(username.substring(1));
		connectedUsers.add(username.substring(1));
		connectedUserandSocket.put(username.substring(1), Sock);
		
		for(int i=0; i<connectedUsers.size();i++){
			Socket userSocket = (Socket) connectedSockets.get(i);
			PrintWriter Out = new PrintWriter(userSocket.getOutputStream());
			Out.println("VIS#$"+visibleUsers);
			Out.flush();
		}
	}
	
	
}
