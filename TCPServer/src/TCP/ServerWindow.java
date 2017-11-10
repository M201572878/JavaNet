package TCP;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;

public class ServerWindow extends JFrame {

	public static JTextArea m_allUserListTextArea = new JTextArea();
	public static JTextArea m_onlineListTextArea = new JTextArea();
	public static JTextArea m_serverIPTextArea = new JTextArea();
	
	private JPanel mainPane;;

	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					ServerWindow frame = new ServerWindow();
//					frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	/**
	 * Create the frame.
	 */
	public ServerWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setBounds(100, 100, 590, 542);
		mainPane = new JPanel();
		mainPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(mainPane);
		
		JScrollPane onlineUserScrollPane = new JScrollPane();
		onlineUserScrollPane.setBounds(19, 176, 231, 286);
		
		JScrollPane allUserScrollPane = new JScrollPane();
		allUserScrollPane.setBounds(302, 176, 240, 286);
		

		JScrollPane serverIPScrollPane = new JScrollPane();
		serverIPScrollPane.setBounds(19, 48, 231, 57);
		
		JLabel ServerInfoLabel = new JLabel("Server:");
		ServerInfoLabel.setFont(new Font("Server:",Font.BOLD,16)); 
		ServerInfoLabel.setBounds(19, 13, 155, 28);
		
		JLabel onlineUserLabel = new JLabel("OnlineList");
		onlineUserLabel.setFont(new Font("OnlineList",Font.BOLD,16)); 
		onlineUserLabel.setBounds(70, 139, 127, 18);
		
		JLabel allUserListtLabel = new JLabel("AllUserList");
		allUserListtLabel.setFont(new Font("FileTransportList",Font.BOLD,16)); 
		allUserListtLabel.setBounds(345, 139, 191, 18);
		
//		init();
		
//		三个文本框设置
		
		m_allUserListTextArea = new JTextArea();
		m_allUserListTextArea.setEditable(false);	
		m_allUserListTextArea.setLineWrap(true);
		m_allUserListTextArea.setFont(new Font("",Font.BOLD,16));
		allUserScrollPane.setViewportView(m_allUserListTextArea);
		
//		fileTranspotTextArea.setText("");
//		fileTranspotTextArea.append("hello");
		
		m_onlineListTextArea = new JTextArea();
		m_onlineListTextArea.setEditable(false);	
		m_onlineListTextArea.setLineWrap(true);
		m_onlineListTextArea.setFont(new Font("",Font.BOLD,16));
		onlineUserScrollPane.setViewportView(m_onlineListTextArea);
		
		
		m_serverIPTextArea  = new JTextArea();
		m_serverIPTextArea.setEditable(false);	
		m_serverIPTextArea.setLineWrap(true);
		m_serverIPTextArea.setFont(new Font("",Font.BOLD,16));
		serverIPScrollPane.setViewportView(m_serverIPTextArea);
		mainPane.setLayout(null);
		mainPane.add(onlineUserScrollPane);
		mainPane.add(allUserScrollPane);
		mainPane.add(ServerInfoLabel);
		mainPane.add(serverIPScrollPane);
		mainPane.add(onlineUserLabel);
		mainPane.add(allUserListtLabel);
	}
	
//	private void init()
//	{
//		m_serverInfo = new ArrayList<String>();
//		m_onlinelist = new ArrayList<String>();
//		m_fileTranspotList = new ArrayList<String>();	
//	}
}
