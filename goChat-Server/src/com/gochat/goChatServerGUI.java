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
import javax.swing.ScrollPaneConstants;

/**
 * Purpose	:This class is used to construct a GUI for server to show message traffic and chat pairs
 * 
 * @author Gowtham Sathiyanaryanan
 * @version	Original
 */

public class goChatServerGUI {

	private JFrame frame; //create JFrame instance
	public static JList chatPairList = new JList(); //to store list of chat pairs
	public static JTextArea threadMonitorList = new JTextArea(); //to show active thread and user it handles
	public static JTextArea conversationTextArea = new JTextArea(); //to show HTTP message traffic

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
		frame.setBounds(100, 100, 681, 492); //allocated position to the element in the frame window
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
				
		//conversationTextArea.setBounds(10, 71, 403, 372);
		
		JScrollPane conversationScrollPane = new JScrollPane();
		conversationScrollPane.setBounds(10, 71, 403, 372); //allocated position to the element in the frame window
		conversationScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		conversationScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		conversationTextArea.setEditable(false);
		conversationScrollPane.setViewportView(conversationTextArea);
		frame.getContentPane().add(conversationScrollPane);
					
		JLabel lblChatMessages = new JLabel("Chat Messages");
		lblChatMessages.setBounds(153, 30, 109, 14); //allocated position to the element in the frame window
		frame.getContentPane().add(lblChatMessages);
		
		JLabel lblChatPairs = new JLabel("Chat Pairs");
		lblChatPairs.setBounds(502, 30, 66, 14); //allocated position to the element in the frame window
		frame.getContentPane().add(lblChatPairs);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(423, 71, 232, 168); //allocated position to the element in the frame window
		scrollPane.setViewportView(chatPairList);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		frame.getContentPane().add(scrollPane);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane_1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane_1.setViewportView(threadMonitorList);
		scrollPane_1.setBounds(423, 266, 232, 177); //allocated position to the element in the frame window
		frame.getContentPane().add(scrollPane_1);
		
		JLabel lblThreadMonitor = new JLabel("Thread monitor (Thread id : Username)");
		lblThreadMonitor.setBounds(433, 241, 232, 14); //allocated position to the element in the frame window
		frame.getContentPane().add(lblThreadMonitor);
		
	}
}
