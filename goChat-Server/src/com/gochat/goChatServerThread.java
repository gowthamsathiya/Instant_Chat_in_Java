/**
 * File Name				: goChatServerThread.java
 * 
 * Author					: Gowtham Sathiyanarayanan
 * 
 * UTA ID					: 1000991932
 * 
 * Subject					: Distributed System
 * 
 * Input					: goChatServer class instantiate instance of this class which implements runnable to run as a thread
 * 	
 * Supported requirements	: java to be installed
 * 
 * Class Name				: goChatServerThread
 * 
 * Functional description	: Once connection between client and server is established using goChatServer class, this class monitor for and service request from client like,
 * 							  connecting to a user, send chat message, disconnect, logout from chat, send visible user list to client and respond to them by implementing Runnable interface
 * 
 * Design implementation	: RPC message format various services they implement are as follows
 * 								DIS#$ - Request to disconnect the chat pair
 * 								REC#$ - Connect to particular recipient
 * 								ERR#$ - Error in connection
 * 								SCX#$ - Successfully connected
 * 								VIS#$ - Visible user list
 * 								RON#$ - Recipient disconnected 
 */

package com.gochat;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;

/**
 * Class Name : goChatServerThread
 * 
 * @author Gowtham Sathiyanarayanan
 * @version Original
 */
public class goChatServerThread implements Runnable {
	Socket ClientSocket;
	private Scanner In;
	private PrintWriter Out;
	private String clientMessage;
	private String[] arrayPairs;
	
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
							goChatServer.connectedListPair.add(clientName+" < > "+recepientName);
							
							goChatServer.serverGUI.conversationTextArea.append(clientName+" < > "+recepientName+" got connected \n");
							arrayPairs = goChatServer.connectedListPair.toArray(new String[goChatServer.connectedListPair.size()]);
							goChatServer.serverGUI.chatPairList.setListData(arrayPairs);
							
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
					if(goChatServer.connectedListPair.contains(senderName+" < > "+recepientName)){
						goChatServer.connectedListPair.remove(senderName+" < > "+recepientName);
						goChatServer.serverGUI.conversationTextArea.append(senderName+" < > "+recepientName+" got disconnected");
						
					}
					else if(goChatServer.connectedListPair.contains(recepientName+" < > "+senderName)){
						goChatServer.connectedListPair.remove(recepientName+" < > "+senderName);
						goChatServer.serverGUI.conversationTextArea.append(recepientName+" < > "+senderName+" got disconnected");
					}
					arrayPairs = goChatServer.connectedListPair.toArray(new String[goChatServer.connectedListPair.size()]);
					goChatServer.serverGUI.chatPairList.setListData(arrayPairs);
				}
				else {
					String[] splitMessage = clientMessage.split(" > ");
					String senderName = splitMessage[0];
					Socket recepientSocket = getRecepientSocket(senderName);
					if(recepientSocket!=null){
						try{
							PrintWriter Out = new PrintWriter(recepientSocket.getOutputStream());
							Out.println(senderName+" > "+splitMessage[2]);
							Out.flush();
							String header[] = splitMessage[1].split("##");
							for(String h:header)
								goChatServer.serverGUI.conversationTextArea.append(h+"\n");
							goChatServer.serverGUI.conversationTextArea.append("Data : "+splitMessage[2]+"\n\n");
						}catch(Exception e){
							Out.println("Recepient can no longer receive your chat");
							Out.flush();
						}
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
	
	/**
	 * Method Name	:	getRecepientSocket
	 * Purpose		:	return the get corresponding recipient socket
	 * @param senderName
	 * @return
	 */
	private Socket getRecepientSocket(String senderName){
		Socket recepientSocket = null;
		if(goChatServer.connectedPair.containsKey(senderName)){
			String recepientName = goChatServer.connectedPair.get(senderName);
			recepientSocket = goChatServer.connectedUserandSocket.get(recepientName);
		}
		return recepientSocket;
	}

	/**
	 * Method Name	:	CheckConnection
	 * Purpose		:	To check for connection of client. If client got disconnected, remove from connectedUserList and ConnectedPairs
	 * 
	 * @throws IOException
	 */
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
					goChatServer.serverGUI.conversationTextArea.append(username+" disconnected \n");
				}
			}
			
			goChatServer.connectedUsers.remove(username);
			
			for(Socket connectedSocket : goChatServer.connectedSockets){
				PrintWriter Out = new PrintWriter(connectedSocket.getOutputStream());
				Out.println("VIS#$"+goChatServer.visibleUsers);
				Out.flush();
			}
			
			for(String pair:goChatServer.connectedListPair){
				if(pair.contains(username)){
					goChatServer.connectedListPair.remove(pair);
					break;
				}
			}
			arrayPairs = goChatServer.connectedListPair.toArray(new String[goChatServer.connectedListPair.size()]);
			goChatServer.serverGUI.chatPairList.setListData(arrayPairs);			
			
		}
		
		
	}
	
	/**
	 * Method name	:	notifyDisconnect
	 * Purpose		:	notify to recipient and to other users when client got disconnected
	 * @param username
	 * @throws IOException
	 */
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
