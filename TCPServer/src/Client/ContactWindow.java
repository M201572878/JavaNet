package Client;

import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.List;
import java.awt.Toolkit;
import java.awt.color.ColorSpace;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.crypto.dsig.SignedInfo;

import org.omg.CORBA.PUBLIC_MEMBER;

import TCP.Operation;

//��½���������
public class ContactWindow extends JFrame {
	DefaultListModel m_listModel = new DefaultListModel();
	HashMap<String, String> m_contactStateMap = new HashMap<String, String>();
	JList m_userList = new JList(m_listModel);
	JScrollPane m_userScrollPane = new JScrollPane(m_userList);
	JLabel m_userLabel = new JLabel("", JLabel.CENTER);
	int m_contactWidth = 250;
	int m_contactHeight = 600;
	String m_user = null;
	String m_selectUser = null;
	ArrayList<String> m_msgUserList = new ArrayList<String>();
	public ClientSocket m_clientSocket = new ClientSocket();
	public HashMap<String, ArrayList<ChatHistory>> m_userChatHistory = new HashMap<String, ArrayList<ChatHistory>>();
	public HashMap<String, ArrayList<String>> m_msgMap = new HashMap<String, ArrayList<String>>();
	public HashMap<String, ChatWindow> m_openContactWindowMap = new HashMap<String, ChatWindow>();
	boolean m_fileTransState = false;
	
	ContactWindow(String user, ClientSocket clientSocket)
	{
		m_user = user;
		
		m_clientSocket = clientSocket;
		m_clientSocket.m_contaContactWindow = this;
		
		Font font=new Font("����", Font.PLAIN, 20);
		m_userLabel.setFont(font);
		m_userLabel.setText(user);
		m_userLabel.setBackground(Color.WHITE);
		m_userLabel.setBounds(0, 0, m_contactWidth, 50);
		
		setLayout(null);
		
		setSize(m_contactWidth, m_contactHeight);
		setResizable(false);
		setVisible(true);
		
		WindowUtil.SetLocationRight(this);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		m_userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		m_userList.setFixedCellHeight(40);
		m_userList.setCellRenderer(new ContactListCellRender());
		m_userScrollPane.setBounds(0, 50, m_contactWidth, 550);
		
		m_userList.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// TODO Auto-generated method stub
//				m_userList.setSelectionBackground(Color.LIGHT_GRAY);
				m_selectUser = (String) m_userList.getSelectedValue();
			}
		});
		
		
		
		//˫���û��������촰��
		m_userList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseEvent) {
                JList theList = (JList) mouseEvent.getSource();
                if (mouseEvent.getClickCount() == 2) {
                    int index = theList.locationToIndex(mouseEvent.getPoint());
                    if (index >= 0) {
                    	if(m_msgMap.containsKey(m_selectUser))
                    	{
                    		m_msgMap.remove(m_selectUser);
                    		if(m_contactStateMap.get(m_selectUser).equals("offline"))
                    		{
                    			Operation operation = new Operation();
                        		operation.m_operationName = "confirmOfflineMsg";
                        		m_clientSocket.SendToServer(operation);
                    		}
                    	}
                    	if(!m_openContactWindowMap.containsKey(m_selectUser))
                    	{
                    		OnSendMsgToOther(m_selectUser);
                    	}
                    	else
                    	{
                    		m_openContactWindowMap.get(m_selectUser).setVisible(true);
                    		m_openContactWindowMap.get(m_selectUser).Show();
                    	}
                    }
                }
            }
		});
		
		add(m_userLabel);
		add(m_userScrollPane);
	}
	
	public void OnSendMsgToOther(String user){
		ChatWindow chatWindow = new ChatWindow(user);
		m_openContactWindowMap.put(user, chatWindow);
		Operation operation = new Operation();
		operation.m_targetUser = user;
		if(m_contactStateMap.get(user).equals("online"))
		{
			operation.m_operationName = "onlineMsgReq";
		}
		else
		{
			operation.m_operationName = "offlineMsgReq";
		}
		m_clientSocket.SendToServer(operation);
	}
	
	public void AddUsers(String[] users, String[] userStates)
	{
		for(int i = 0; i < users.length; ++i)
		{
			if(!m_user.equals(users[i]))
			{
				m_listModel.addElement(users[i]);
				m_contactStateMap.put(users[i], userStates[i]);
				System.out.println(userStates[i]);
			}
		}	
	}
	
	public void AddMsg(String sender, String msg)
	{
		if(!m_msgMap.containsKey(sender)){
			m_msgMap.put(sender, new ArrayList<String>());
		}
		System.out.println(sender+msg);
//		m_msgMap.get(sender).add(msg);
		if(!m_userChatHistory.containsKey(sender))
		{
			m_userChatHistory.put(sender, new ArrayList<ChatHistory>());
		}
		m_userChatHistory.get(sender).add(new ChatHistory(sender, msg));
		repaint();
		RefreshContactWindow(sender);
	}
	
	public void RefreshContactWindow(String target)
	{
		if(m_openContactWindowMap.containsKey(target))
			m_openContactWindowMap.get(target).RefreshText();
	}
	
	public void ChangeContactState(String user, String state)
	{
		if(!user.equals(m_user))
			m_contactStateMap.replace(user, state);
	}
	
	private class ContactListCellRender extends DefaultListCellRenderer{
		public Component getListCellRendererComponent(JList list, Object value,  
	            int index, boolean isSelected, boolean cellHasFocus) {  
//	        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus); 
			if(!m_contactStateMap.containsKey(value.toString()))
			{
				return this;
			}
			String contactState = m_contactStateMap.get(value.toString());
			if(contactState.equals("online"))
			{
				setForeground(Color.BLACK);
				setBackground(Color.WHITE);
			}
			else
			{
				setForeground(Color.lightGray);
				setBackground(Color.WHITE);
			}
			if(m_selectUser != null && m_selectUser.equals(value.toString()))
				setBackground(new Color(240, 240, 240));
			if(m_msgMap.containsKey(value.toString()))
				setBackground(Color.GREEN);
	        setText(value.toString());
	        setFont(new Font("����", Font.PLAIN, 20));
	        return this;  
	    }  
	}
	
	//��һ����Ϣ
	private class ChatHistory{
		String m_sender;
		String m_message;
		ChatHistory(String message){
			m_message = message;
		}
		ChatHistory(String sender, String message){
			m_sender = sender;
			m_message = message;
		}
	}
	
	//���촰��
	private class ChatWindow extends JFrame implements ActionListener{
		int m_chatWidth = 607;
		int m_chatHeight = 400;
		JTextArea m_chatArea = new JTextArea(10, 15);
		int m_chatFontSize = 20;
		JTextArea m_inputArea = new JTextArea(10, 15);
		JButton m_closeButton = new JButton("close");
		JButton m_sendButton = new JButton("send");
		JButton m_uploadFileButton = new JButton("send file");
		JPanel m_operationPanel = new JPanel();
		JLabel m_fileUploadInfo = new JLabel("<html><u>open file direcory</u></html>");
		String m_chatTargeUser = null;
		

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			
		} 
		private void OnSend(){
			String inputContent = m_inputArea.getText();
			m_chatArea.append(GetChatRowKeepRight(inputContent));
			m_inputArea.setText("");
			
			if(m_contactStateMap.get(m_chatTargeUser).equals("online"))
			{
				Operation operation = new Operation();
				operation.m_operationName = "onlineChatWithOtherClient";
				operation.m_user = m_user;
				operation.m_msg = inputContent;
				m_clientSocket.SendMessageToOtherClient(m_chatTargeUser, operation);
			}
			else
			{
				Operation operation = new Operation();
				operation.m_targetUser = m_chatTargeUser;
				operation.m_user = m_user;
				operation.m_operationName = "offlineMsgReq";
				operation.m_msg = inputContent;
				m_clientSocket.SendToServer(operation);
			}
			m_userChatHistory.get(m_chatTargeUser).add(new ChatHistory(m_user, inputContent));
			
		}
		
		public void RefreshText()
		{
			m_chatArea.setText("");
			for(ChatHistory chatHistory : m_userChatHistory.get(m_chatTargeUser)){
				String messageDisplay = chatHistory.m_message;
				if(m_user.equals(chatHistory.m_sender))
				{
					messageDisplay = GetChatRowKeepRight(messageDisplay);
				}
				m_chatArea.append(messageDisplay + "\n");
			}
			repaint();
		}
		
		public void Show()
		{
			m_chatArea.setText("");
			for(ChatHistory chatHistory : m_userChatHistory.get(m_chatTargeUser)){
				String messageDisplay = chatHistory.m_message;
				if(m_user.equals(chatHistory.m_sender))
				{
					messageDisplay = GetChatRowKeepRight(messageDisplay);
				}
				m_chatArea.append(messageDisplay + "\n");
			}
			show();
		}
		
		public ChatWindow(String targetUser) {
			m_chatTargeUser = targetUser;
			
			setLayout(null);
			setTitle(targetUser);
			
			setSize(m_chatWidth, m_chatHeight);
			setResizable(false);
			setVisible(true);
			WindowUtil.SetLocationCenter(this);
			
			Font font=new Font("����", Font.PLAIN, m_chatFontSize);
			m_chatArea.setFont(font);
			m_chatArea.setBounds(0, 0, m_chatWidth - 7, 240);
			m_chatArea.setLineWrap(true);
			m_chatArea.setEditable(false);
			m_inputArea.setBounds(0, 242, m_chatWidth, 98);
			m_inputArea.setFont(font);
			m_inputArea.addKeyListener(new KeyAdapter() {   
                public void keyPressed(KeyEvent arg0) {   
                    //Ctrl+Enter��ϼ�����ʱ��Ӧ   
                    if ((arg0.getKeyCode() == KeyEvent.VK_ENTER) && (arg0.isControlDown())) {   
                    	OnSend();
                     }   
                 }   
             });   
			
			m_operationPanel.setLayout(null);
			m_operationPanel.setBackground(new Color(0xFFFFFF));
			m_operationPanel.setBounds(0, 330, m_chatWidth, 60);
			m_uploadFileButton.setBounds(0, 10, 100, 25);
			m_fileUploadInfo.setBounds(110, 10, 200, 25);
			m_fileUploadInfo.setBackground(Color.BLUE);
			Font font2 = new Font("����", Font.PLAIN, 15);
			m_fileUploadInfo.setFont(font2);
			m_closeButton.setBounds(m_chatWidth - 230, 10, 100, 25);
			m_sendButton.setBounds(m_chatWidth - 120, 10, 100, 25);
			m_uploadFileButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if(m_contactStateMap.get(m_chatTargeUser).equals("offline"))
					{
						JOptionPane.showConfirmDialog(null, "user is offline", "tips", JOptionPane.YES_NO_OPTION);
						return;
					}
					JFileChooser jfc=new JFileChooser();
					jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			        if(jfc.showOpenDialog(null)==JFileChooser.APPROVE_OPTION){
			            File file=jfc.getSelectedFile();
			            int ret = JOptionPane.showConfirmDialog(null, "sure to send file " + file.getName(), "tips", JOptionPane.YES_NO_OPTION);
			            if(ret == 0)
			            {
			            	m_clientSocket.m_sendFile = file;
			            	Operation operation = new Operation();
			            	operation.m_operationName = "sendFileReq";
			            	operation.m_user = m_user;
			            	operation.m_port = m_clientSocket.m_serverSocket.getLocalPort();
			            	operation.m_fileName = file.getName();
			            	m_clientSocket.SendMessageToOtherClient(m_chatTargeUser, operation);
			            }
			        }
				}
			});
			m_fileUploadInfo.addMouseListener(new MouseListener() {
				@Override
				public void mouseExited(MouseEvent e) {
					setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));  
				}
				
				@Override
				public void mouseEntered(MouseEvent e) {
					setCursor(Cursor.getDefaultCursor());  
				}
				
				@Override
				public void mouseClicked(MouseEvent e) {
					try {
						String receivePath = "./download/readme";
						File file = new File(receivePath);  
						if(!file.getParentFile().exists()){  
							file.getParentFile().mkdirs(); 
						} 
						java.awt.Desktop.getDesktop().open(new java.io.File("./download"));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

				@Override
				public void mousePressed(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
			});
			m_closeButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			m_sendButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					OnSend();
				}
			});
			m_operationPanel.add(m_uploadFileButton);
			m_operationPanel.add(m_fileUploadInfo);
			m_operationPanel.add(m_closeButton);
			m_operationPanel.add(m_sendButton);
			
			add(m_chatArea);
			add(m_inputArea);
			add(m_operationPanel);
			
			if(!m_userChatHistory.containsKey(m_chatTargeUser))
			{
				m_userChatHistory.put(m_chatTargeUser, new ArrayList<ChatHistory>());
//				m_userChatHistory.get(m_chatTargeUser).add(new ChatHistory(m_chatTargeUser, "���,����"+m_chatTargeUser));
//				m_userChatHistory.get(m_chatTargeUser).add(new ChatHistory(m_user, "���,����"+m_user));
			}
			for(ChatHistory chatHistory : m_userChatHistory.get(m_chatTargeUser)){
				String messageDisplay = chatHistory.m_message;
				if(m_user.equals(chatHistory.m_sender))
				{
					messageDisplay = GetChatRowKeepRight(messageDisplay);
				}
				m_chatArea.append(messageDisplay + "\n");
			}
		}
		//�Լ�����Ϣ���Ұݷ�
		private String GetChatRowKeepRight(String message)
		{
			int maxChatRowStrLen = m_chatArea.getSize().width * 2 / m_chatFontSize;
			String[] lines = message.split("\n");
			String messageDisplay = "";
			System.out.println(lines.length);
			for(int index = 0; index < lines.length; ++index)
			{
				String str = lines[index];
				System.out.println(str);
				for(int i = GetChineseStrLen(str); i < maxChatRowStrLen; ++i)
				{
					str = " " + str;
				}
				str += '\n';
				messageDisplay += str;
			}
			
			return messageDisplay;
		}
		//��ȡ�����ĵ��ַ���ʵ�ʳ���
		private int GetChineseStrLen(String str){
			int length = 0;  
	        for(int i = 0; i < str.length(); i++)  
	        {  
	            int ascii = Character.codePointAt(str, i);  
	            if(ascii >= 0 && ascii <=255)  
	                length++;  
	            else  
	                length += 2;  
	        }  
	        return length;
		}
	}
	
}
