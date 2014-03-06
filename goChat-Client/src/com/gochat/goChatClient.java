/**
 * File Name				: goChatClient.java
 * 
 * Author					: Gowtham Sathiyanarayanan
 * 
 * UTA ID					: 1000991932
 * 
 * Subject					: Distributed System
 * 	
 * Supported requirements	: java to be installed
 * 
 * Class Name				: goChatClient
 * 
 * Functional description	: This class provides GUI interface for client to perform instant chat. They instantiate connection and process message passing using goChatClientThread class
 * 
 * Assumption				: Server running on port 80
 */

package com.gochat;

import java.awt.CardLayout;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Class Name : goChatClient
 * 
 * @author Gowtham Sathiyanarayanan
 * @version Original
 */
public class goChatClient{

	private JFrame frame;
	private JPanel panel;
	private static JTextField nameTextField;
	private JTextField recepientNameTextField;
	private JTextField chatMessageTextField;
	private static JButton connectButton;
	private static JButton sendButton;
	private static JButton disconnectButton;
	private JButton logOffButton;
	private static JLabel chattingAsLabel;
	public static JTextArea visibleUserTextArea;
	public static JList visibleUserList = new JList();
	public static JScrollPane visibleUserScrollPane = new JScrollPane();
	public static JTextArea conversationTextArea;
	public static JScrollPane conversationScrollPane = new JScrollPane();
	private JCheckBox visibleCheckBox;
	private JButton getConnectedButton;
	private goChatClientThread chatclient;
	public static String username;
	private Socket clientsocket;
	DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					goChatClient window = new goChatClient();
					window.frame.setVisible(true);
					window.frame.setTitle("goChat");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public goChatClient() {
		initialize();
		initConfiguration();
		setUpListeners();
	}

	/**
	 * Purpose : To perform initial configuration to client
	 */
	public static void initConfiguration() {
		disconnectButton.setVisible(false);
		sendButton.setVisible(false);
		connectButton.setVisible(true);
		connectButton.setFocusable(true);
		chattingAsLabel.setText("Connected as "+nameTextField.getText());
	}
	
	/**
	 * Purpose : To set up corresponding listener to ever action buttons perform
	 */
	
	private void setUpListeners() {
		getConnectedButton.addActionListener(getconnectedListener);
		connectButton.addActionListener(connectButtonListener);
		sendButton.addActionListener(sendButtonListener);
		disconnectButton.addActionListener(disconnectListener);
		logOffButton.addActionListener(logOffListener);
		
	}
	
	/**
	 * Purpose : Create a listener for connect button, to request for connect to a recipient
	 */
	private ActionListener connectButtonListener = new ActionListener(){
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			String recepient = recepientNameTextField.getText();
			if(nameTextField.getText().equals(username)){
				JOptionPane.showMessageDialog(null, "Oops! This chat is not allowed. Enter some other name");
			}
			else if(!recepient.equals("")){
				PrintWriter COut;
				try {
					COut = new PrintWriter(clientsocket.getOutputStream());
					COut.println("REC#$ > "+goChatClient.username+" > "+recepient);
					COut.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			else{
				JOptionPane.showMessageDialog(frame,"Enter valid recepient name");
			}
		}
	};
	
	/**
	 * Purpose : Create a listener for send button to send message to recipient via server
	 */
	private ActionListener sendButtonListener = new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent arg0) {
			String chatMessage = chatMessageTextField.getText();
			if(!chatMessage.isEmpty()){
				Date now = new Date();
				goChatClient.conversationTextArea.append(dateFormat.format(now)+":  You > "+chatMessage+"\n");
				try {
					sendChatMessage(chatMessage);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			else{
				JOptionPane.showMessageDialog(frame,"Nothing to send");
			}
		}
		
	};
	
	/**
	 * Purpose : Create a listener for get connected button to establish connection with server
	 */
	private ActionListener getconnectedListener = new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent e) {
			if(!nameTextField.getText().equals("")){
				username = nameTextField.getText();
				connectToServer();
				CardLayout layout = (CardLayout) panel.getLayout();
				layout.show(panel,"chathomepanel");
				chattingAsLabel.setText("Chatting as "+username);
				frame.setTitle("goChat - "+username);
			}
			else{
				JOptionPane.showMessageDialog(null, "Enter some valid name");
			}
		}
		
	};
	
	/**
	 * Purpose : Create a listener to log off button to log off the client
	 */
	private ActionListener logOffListener = new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent arg0) {
			logOff();
		}
		
	};
	
	/**
	 * Purpose : Create a listener to disconnect button to disconnect with the recipient
	 */
	private ActionListener disconnectListener = new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				disconnect();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
	};
	
	/**
	 * Purpose : To change the configuration of GUI after the connect to recipient
	 */
	public static void configurationAfterConnect(){
		disconnectButton.setVisible(true);
		sendButton.setVisible(true);
		sendButton.setFocusable(true);
		connectButton.setVisible(false);
		connectButton.setFocusable(false);
	}

	/**
	 * PUrpose : To establish connection to server running on port 80
	 */
	protected void connectToServer() {
		try{
			//Random r= new Random();
			//int rport = r.nextInt(5000-1500)+1500;
			final int port = 80;
			clientsocket = new Socket("localhost",port);
			chatclient = new goChatClientThread(clientsocket);
			
			PrintWriter out = new PrintWriter(clientsocket.getOutputStream());
			String vcheck = "0";
			if(visibleCheckBox.isSelected())
				vcheck = "1";
			out.println(vcheck+nameTextField.getText());
			out.flush();
			
			Thread t = new Thread(chatclient);
			t.start();
		}
		catch(Exception e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Oops!. Server went down");
			System.exit(0);
		}
	}

	/**
	 * Purpose : To send chat message to server
	 * 
	 * @param chatMessage
	 * @throws UnsupportedEncodingException 
	 */
	protected void sendChatMessage(String chatMessage) throws UnsupportedEncodingException {
		Date nowdate = new Date();
		String header = "HTTP/1.1##Accept: text/plain##Accept-Charset: utf-8##Accept-Language: en-US##Accept-Datetime: "+nowdate.toString()+"##Authorization: None##Cache-Control: no-cache##Connection: keep-alive##Content-Length:"+chatMessage.getBytes("UTF-8").length +"##From: "+username+"##Host: localhost:80##User-Agent: goChatClient";
		chatclient.sendMessage(header+" > "+chatMessage);
		chatMessageTextField.setText("");
		chatMessageTextField.requestFocus();
	}
	
	/**
	 * Purpose : To call disconnect with the recipient
	 * @throws IOException
	 */
	protected void disconnect() throws IOException {
		//String disconnectKey = "DIS#$";
		chatclient.disconnect();
		initConfiguration();
	}
	
	/**
	 * Purpose : To log off from chat
	 */
	protected void logOff(){
		try{
			chatclient.logOff();
			JOptionPane.showMessageDialog(null, "Logging off. Bye");
			System.exit(0);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void getHeader(){
		Date nowdate = new Date();
		String header = "Accept: text/plain##Accept-Charset: utf-8##Accept-Language: en-US##Accept-Datetime: "+nowdate.toString()+"##Authorization: None##Cache-Control: no-cache##Connection: keep-alive##Content-Length:##From: "+username+"##Host: localhost:80##User-Agent: goChatClientH$%#H";
	}

	private AdjustmentListener scrollPaneAdjust = new AdjustmentListener(){

		@Override
		public void adjustmentValueChanged(AdjustmentEvent a) {
			a.getAdjustable().setValue(a.getAdjustable().getMaximum());
		}
		
	};
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 478, 555);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		panel = new JPanel();
		panel.setBounds(0, 0, 462, 517);
		frame.getContentPane().add(panel);
		panel.setLayout(new CardLayout());
		
		JPanel panel_1 = new JPanel();
		panel.add(panel_1, "chatinitpanel");
		panel_1.setLayout(null);
		
		JLabel lblGochat = new JLabel("goChat!");
		lblGochat.setFont(new Font("Old English Text MT", Font.BOLD, 40));
		lblGochat.setBounds(149, 107, 140, 96);
		panel_1.add(lblGochat);
		
		JLabel lblToGetConnected = new JLabel("to get connected");
		lblToGetConnected.setFont(new Font("Serif", Font.PLAIN, 14));
		lblToGetConnected.setBounds(236, 196, 126, 19);
		panel_1.add(lblToGetConnected);
		
		JLabel lblName = new JLabel("Name :");
		lblName.setBounds(55, 350, 46, 14);
		panel_1.add(lblName);
		
		nameTextField = new JTextField();
		nameTextField.setBounds(130, 347, 232, 20);
		panel_1.add(nameTextField);
		nameTextField.setColumns(10);
		
		visibleCheckBox = new JCheckBox("Visible to others");
		visibleCheckBox.setBounds(261, 381, 156, 23);
		panel_1.add(visibleCheckBox);
		
		getConnectedButton = new JButton("getConnected");
		getConnectedButton.setBounds(160, 447, 129, 23);
		panel_1.add(getConnectedButton);
		
		JPanel panel_2 = new JPanel();
		panel.add(panel_2, "chathomepanel");
		panel_2.setLayout(null);
		
		chattingAsLabel = new JLabel("Chatting as");
		chattingAsLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		chattingAsLabel.setBounds(10, 11, 223, 39);
		panel_2.add(chattingAsLabel);
		
		JLabel lblVisibleUsers = new JLabel("Visible users\r\n");
		lblVisibleUsers.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		lblVisibleUsers.setBounds(334, 10, 99, 40);
		panel_2.add(lblVisibleUsers);
		
		visibleUserScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		visibleUserScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		visibleUserScrollPane.setViewportView(visibleUserList);
		visibleUserScrollPane.setBounds(299, 58, 153, 349);
		panel_2.add(visibleUserScrollPane);
		
		JLabel connectToLabel = new JLabel("Connect to");
		connectToLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		connectToLabel.setBounds(299, 418, 125, 20);
		panel_2.add(connectToLabel);
		
		recepientNameTextField = new JTextField();
		recepientNameTextField.setBounds(299, 449, 114, 20);
		panel_2.add(recepientNameTextField);
		recepientNameTextField.setColumns(10);
		
		connectButton = new JButton("~");
		connectButton.setBounds(411, 450, 41, 19);
		panel_2.add(connectButton);
		
		chatMessageTextField = new JTextField();
		chatMessageTextField.setBounds(10, 431, 217, 54);
		panel_2.add(chatMessageTextField);
		chatMessageTextField.setColumns(10);
		
		sendButton = new JButton(">");
		sendButton.setBounds(222, 431, 59, 55);
		panel_2.add(sendButton);
		
		conversationTextArea = new JTextArea();
		conversationTextArea.setColumns(20);
		conversationTextArea.setRows(5);
		conversationTextArea.setEditable(false);
		conversationTextArea.setLineWrap(true);
		//conversationTextArea.setBounds(10, 58, 269, 349);
		//panel_2.add(conversationTextArea);
		
		conversationScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		conversationScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		conversationScrollPane.setViewportView(conversationTextArea);
		conversationScrollPane.setBounds(10, 58, 269, 349);
		conversationScrollPane.getVerticalScrollBar().addAdjustmentListener(scrollPaneAdjust);
		panel_2.add(conversationScrollPane);
		
		logOffButton = new JButton("Log off");
		logOffButton.setBounds(222, 21, 88, 23);
		panel_2.add(logOffButton);
		
		disconnectButton = new JButton("Disconnect");
		disconnectButton.setBounds(309, 483, 124, 23);
		panel_2.add(disconnectButton);
	
	}
	
	public static void connectionErrorPrompt(String errorMessage) {
		JOptionPane.showMessageDialog(null, errorMessage);		
	}
	
}
