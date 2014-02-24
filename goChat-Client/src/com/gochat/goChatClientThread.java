package com.gochat;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class goChatClientThread implements Runnable{
	
	Socket ClientSocket;
	Scanner In;
	PrintWriter Out;

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
	
	public void checkForMessage(){
		while(true){
			if(In.hasNext()){
				String chatMessage = In.nextLine();
				
				if(chatMessage.contains("VIS#$")){
					String[] visibleUsers = chatMessage.substring(5).replace("[", "").replace("]", "").split(", ");
					goChatClient.visibleUserList.setListData(visibleUsers);
				}
				else if(chatMessage.contains("SCX#$")){
					goChatClient.configurationAfterConnect();
				}
				else if(chatMessage.contains("ERR#$")){
					goChatClient.connectionErrorPrompt(chatMessage.split(" > ")[1]);
				}
				else if(chatMessage.contains("RON#$")){
					goChatClient.conversationTextArea.append("Recepient disconected \n");
					goChatClient.initConfiguration();
				}
				else{
					goChatClient.conversationTextArea.append(chatMessage+"\n");
				}
			}
		}
	}
	
	public void sendMessage(String message){
		Out.println(goChatClient.username+" > "+message);
		Out.flush();
	}

	public void logOff() throws IOException{
		Out.println(goChatClient.username+" > has disconnected");
		Out.flush();
		ClientSocket.close();
	}
	
	public void disconnect() throws IOException{
		Out.println("DIS#$ > "+goChatClient.username);
		Out.flush();
	}
	
	/*
	public static void connectToRecepient(String recepientName){
		Out.println("REC#$ > "+goChatClient.username+" > "+recepientName);
		Out.flush();
	}
	*/
	
	
}
