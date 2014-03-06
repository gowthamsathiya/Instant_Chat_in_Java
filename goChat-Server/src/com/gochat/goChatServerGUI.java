/**
 * File Name				: goChatServerGUI.java
 * 
 * Author					: Gowtham Sathiyanarayanan
 * 
 * UTA ID					: 1000991932
 * 
 * Subject					: Distributed System
 * 
 * Input					: goChatServer class instantiate an instance of this class
 * 	
 * Supported requirements	: java to be installed
 * 
 * Class Name				: goChatServerGUI
 * 
 * Functional description	: Simple GUI interface to show message traffic and connected user char pairs
 * 
 */

package com.gochat;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;

/**
 * Purpose	:This class is used to construct a GUI for server to show message traffic and chat pairs
 * 
 * @author Gowtham Sathiyanaryanan
 * @version	Original
 */

public class goChatServerGUI {

	private JFrame frame;
	public static JList chatPairList = new JList();
	public static JTextArea conversationTextArea = new JTextArea();

	/**
	 * Purpose: Launch the application.
	 */
	public void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					goChatServerGUI window = new goChatServerGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Purpose: Launch the application.
	 */

	public static void runGUI(){
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					goChatServerGUI window = new goChatServerGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 *Purpose: Create the application.
	 */
	public goChatServerGUI() {
		initialize();
	}

	/**
	 *Purpose: Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("goChat Server");
		frame.setBounds(100, 100, 681, 492);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
				
		//conversationTextArea.setBounds(10, 71, 403, 372);
		
		JScrollPane conversationScrollPane = new JScrollPane();
		conversationScrollPane.setBounds(10, 71, 403, 372);
		conversationScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		conversationScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		conversationTextArea.setEditable(false);
		conversationScrollPane.setViewportView(conversationTextArea);
		frame.getContentPane().add(conversationScrollPane);
					
		JLabel lblChatMessages = new JLabel("Chat Messages");
		lblChatMessages.setBounds(153, 30, 109, 14);
		frame.getContentPane().add(lblChatMessages);
		
		JLabel lblChatPairs = new JLabel("Chat Pairs");
		lblChatPairs.setBounds(541, 30, 66, 14);
		frame.getContentPane().add(lblChatPairs);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(423, 71, 232, 372);
		scrollPane.setViewportView(chatPairList);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		frame.getContentPane().add(scrollPane);
		
	}
}
