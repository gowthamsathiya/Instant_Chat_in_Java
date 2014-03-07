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
	private static String username;
	
	/**
	 * Purpose	:	Starts when program is instantiated and keep listening the port for incoming connection. If any, establish connection and process them in a separate thread by calling goChatServerThread class instance.
	 * 				Instantiate goChatServerGUI which displays server GUI of message traffic
	 * @param args
	 * @throws IOException
	 */
	public static void main(String args[]) throws IOException{
		try{
			final int Port = 80; //port to run server
			ServerSocket Server = new ServerSocket(Port); //open server socket connection
			System.out.println("Connection open. Waiting for client");
			serverGUI = new goChatServerGUI(); //Create the server GUI instance
			serverGUI.runGUI(); //Run the server GUI
			
			while(true){ //to keep listening
				Socket Sock = Server.accept(); //accept the incoming socket
				connectedSockets.add(Sock); //add to socket list
				
				System.out.println("Client connected from :"+Sock.getLocalAddress().getHostName()+"-"+Sock.getLocalSocketAddress());
				
				addUser(Sock); //call addUser function to add the user to the list
				
				goChatServerThread chat = new goChatServerThread(Sock); //allocate separate thread for each user
				Thread t = new Thread(chat); //instantiate allocated thread for each user
				t.start(); //start the thread
				goChatServerGUI.threadMonitorList.append(t.getId()+" : "+username.substring(1)+"\n"); //add thread id and user to monitor list in GUI
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
		Scanner input = new Scanner(Sock.getInputStream()); //to get input message from client socket
		username = input.nextLine();
		if(username.charAt(0)=='1') //to check the visibility set. 1-Visible 0-InVisible
			visibleUsers.add(username.substring(1)); //add username to visibleUser list
		connectedUsers.add(username.substring(1)); //add to connectedUsers list which contains all user logged in
		connectedUserandSocket.put(username.substring(1), Sock);//Map the user with their socket
		/**
		 * indicate the visibility of user to all the other users
		 */
		for(int i=0; i<connectedUsers.size();i++){
			Socket userSocket = (Socket) connectedSockets.get(i); //get all socket one by one
			PrintWriter Out = new PrintWriter(userSocket.getOutputStream());
			Out.println("VIS#$"+visibleUsers);//show the user as visible sending updated visibleUsers list
			Out.flush();//flush the socket stream
		}
	}
	
	
}
