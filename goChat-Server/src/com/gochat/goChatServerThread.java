package com.gochat;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;

public class goChatServerThread implements Runnable {
	Socket ClientSocket;
	private Scanner In;
	private PrintWriter Out;
	private String clientMessage;
	
	public goChatServerThread(Socket sock) {
		this.ClientSocket = sock;
	}

	@Override
	public void run() {
		try{
		try{
			In = new Scanner(ClientSocket.getInputStream());
			Out = new PrintWriter(ClientSocket.getOutputStream());
			
			while(true){
				CheckConnection();
				
				if(!In.hasNext())
					return;
				
				clientMessage = In.nextLine();
				
				System.out.println(clientMessage);
				
				if(clientMessage.substring(0, 5).contains("REC#$")){
					String clientName = clientMessage.split(" > ")[1];
					String recepientName = clientMessage.split(" > ")[2];
					if(goChatServer.connectedUsers.contains(recepientName)){
						if(goChatServer.connectedPair.containsKey(recepientName)){
							Out.println("ERR#$ > Recepient is busy");
							Out.flush();
						}
						else{
							goChatServer.connectedPair.put(clientName, recepientName);
							goChatServer.connectedPair.put(recepientName, clientName);
							
							Out.println("SCX#$");
							Out.flush();
							Out.println("Connected to "+recepientName);
							Out.flush();
							
							PrintWriter ROut = new PrintWriter(getRecepientSocket(clientName).getOutputStream());
							ROut.println("SCX#$");
							ROut.flush();
							ROut.println("Connected to "+clientName);
							ROut.flush();
						}
					}
					else{
						Out.println("ERR#$ > No such recepient found");
						Out.flush();
					}
				}
				else if(clientMessage.substring(0, 5).contains("DIS#$")){
					String senderName = clientMessage.split(" > ")[1];
					String recepientName = goChatServer.connectedPair.get(senderName);
					Socket recepientSocket = goChatServer.connectedUserandSocket.get(recepientName);
					//Socket recepientSocket = getRecepientSocket(senderName);
					PrintWriter ROut = new PrintWriter(recepientSocket.getOutputStream());
					ROut.println("RON#$");
					ROut.flush();
					Out.println("RON#$");
					Out.flush();
					goChatServer.connectedPair.remove(senderName);
					goChatServer.connectedPair.remove(recepientName);
				}
				else {
					String senderName = clientMessage.split(" > ")[0];
					Socket recepientSocket = getRecepientSocket(senderName);
					if(recepientSocket!=null){
						PrintWriter Out = new PrintWriter(recepientSocket.getOutputStream());
						Out.println(clientMessage);
						Out.flush();
					}
					else{
						Out.println("Recepient can no longer receive your chat");
						Out.flush();
					}
				}
			}
		}
		finally{
			ClientSocket.close();
		}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private Socket getRecepientSocket(String senderName){
		Socket recepientSocket = null;
		if(goChatServer.connectedPair.containsKey(senderName)){
			String recepientName = goChatServer.connectedPair.get(senderName);
			recepientSocket = goChatServer.connectedUserandSocket.get(recepientName);
		}
		return recepientSocket;
	}

	private void CheckConnection() throws IOException {
		if(!ClientSocket.isConnected()){
			String username = null;
			for(int i=0 ; i<goChatServer.connectedSockets.size();i++){
				if (goChatServer.connectedSockets.get(i) == ClientSocket){
					goChatServer.connectedSockets.remove(i);
				}
			}
			
			for(Map.Entry<String, Socket> user : goChatServer.connectedUserandSocket.entrySet()){
				if(user.getValue() == ClientSocket){
					username = user.getKey();
					notifyDisconnect(username);
				}
			}
			
			goChatServer.connectedUsers.remove(username);
			
			for(Socket connectedSocket : goChatServer.connectedSockets){
				PrintWriter Out = new PrintWriter(connectedSocket.getOutputStream());
				Out.println("VIS#$"+goChatServer.visibleUsers);
				Out.flush();
			}
		}
		
		
	}
	
	private void notifyDisconnect(String username) throws IOException{
		String recepientName = null;
		for(Map.Entry<String, String> recepient : goChatServer.connectedPair.entrySet()){
			if(recepient.getKey().equals(username)){
				recepientName = recepient.getValue();
			}
		}
		goChatServer.connectedPair.remove(username);
		
		if(recepientName!=null){
			for(Map.Entry<String, Socket> user : goChatServer.connectedUserandSocket.entrySet()){
				if(user.getValue().equals(recepientName)){
					Socket recepientSocket = user.getValue();
					PrintWriter Out = new PrintWriter(recepientSocket.getOutputStream());
					Out.println(username+" disconnected");
					Out.flush();
				}
			}
		}
	}

}
