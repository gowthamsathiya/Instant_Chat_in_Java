package com.gochat;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class goChatServer {

	public static ArrayList<Socket> connectedSockets = new ArrayList<Socket>();
	public static ArrayList<String> connectedUsers = new ArrayList<String>();
	public static ArrayList<String> visibleUsers = new ArrayList<String>();
	public static Map<String,Socket> connectedUserandSocket = new HashMap<String,Socket>();
	public static Map<String,String> connectedPair = new HashMap<String,String>();
	
	
	public static void main(String args[]) throws IOException{
		try{
			final int Port = 80;
			ServerSocket Server = new ServerSocket(Port);
			System.out.println("Connection open. Waiting for client");
			
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
