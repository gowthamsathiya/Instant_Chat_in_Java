/**
 * File Name				: goChatClientThread.java
 * 
 * Author					: Gowtham Sathiyanarayanan
 * 
 * UTA ID					: 1000991932
 * 
 * Subject					: Distributed System
 * 
 * Input					: goChatClient class instantiate instance of this class which implements runnable to run as a thread
 * 	
 * Supported requirements	: java to be installed
 * 
 * Class Name				: goChatClientThread
 * 
 * Functional description	: Once connection between client and server is established using goChatClient class, this class process the message passing and service request like,
 * 							  connecting to a user, send chat message, disconnect, logout from chat, show visible user list to client by implementing Runnable interface
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * Class Name :	goChatClientThread
 * 
 * @author Gowtham Sathiyanarayanan
 * @version Original
 */
public class goChatClientThread implements Runnable{
	
	Socket ClientSocket;
	Scanner In;
	PrintWriter Out;
	DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss"); //date format for chat message

	/**
	 * Purpose : Create instance of class
	 * @param s
	 */
	public goChatClientThread(Socket s){
		ClientSocket = s;
	}
	
	@Override
	public void run() {
		try{
			try{
				In = new Scanner(ClientSocket.getInputStream());
				Out = new PrintWriter(ClientSocket.getOutputStream());
				Out.flush();
				checkForMessage();
			}
			finally{
				ClientSocket.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Purpose : Check for incoming message from server and process them if it is an RPC call
	 */
	public void checkForMessage(){
		while(true){
			if(In.hasNext()){
				String chatMessage = In.nextLine();
				
				if(chatMessage.contains("VIS#$")){ //check for VIS#$ - Visible user list from server
					String[] visibleUsers = chatMessage.substring(5).replace("[", "").replace("]", "").split(", ");//convert to arraylist of users
					goChatClient.visibleUserList.setListData(visibleUsers);
				}
				else if(chatMessage.contains("SCX#$")){ //check for SCX#$ - Successfully connected from server on connection request
					goChatClient.configurationAfterConnect();
				}
				else if(chatMessage.contains("ERR#$")){ // check for ERR#$ - Error message from server
					goChatClient.connectionErrorPrompt(chatMessage.split(" > ")[1]);
				}
				else if(chatMessage.contains("RON#$")){ //check for RON#$ - Recipient disconnected fork from server
					goChatClient.conversationTextArea.append("Recepient disconected \n");
					goChatClient.initConfiguration(); //chat chat window to initial configuration when recipient disconnected
				}
				else{
					Date now = new Date(); //to get the message time
					goChatClient.conversationTextArea.append(dateFormat.format(now)+":  "+chatMessage+"\n");
				}
			}
		}
	}
	
	/**
	 * Purpose : Send chat message to server
	 * @param message
	 */
	public void sendMessage(String message){
		Out.println(goChatClient.username+" > "+message); //append user name and send message
		Out.flush();
	}

	/**
	 * Purpose : Send log off request to server
	 * @throws IOException
	 */
	public void logOff() throws IOException{
		Out.println(goChatClient.username+" > has disconnected"); //append user name and disconnect messsage
		Out.flush();
		ClientSocket.close();
	}
	
	/**
	 * Purpose : Send disconnect request to server
	 * @throws IOException
	 */
	public void disconnect() throws IOException{
		Out.println("DIS#$ > "+goChatClient.username); //send DIS#$ - Request to disconnect the chat pair
		Out.flush();
	}
		
}
