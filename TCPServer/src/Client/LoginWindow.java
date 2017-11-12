package Client;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.management.openmbean.OpenMBeanOperationInfo;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import TCP.Operation;
import TCP.UserInfo;

public class LoginWindow extends JFrame {
	public JFrame m_frame = new JFrame("Login Example");
	public JPanel m_mainPanel = new JPanel();   
	public CardLayout m_mainPanelLayout = new CardLayout();
	public ClientSocket m_clientSocket = new ClientSocket();
	
	LoginWindow(ClientSocket clientSocket)
	{
		m_clientSocket = clientSocket;
		m_clientSocket.m_loginWindow = this;
		m_frame.setSize(350*1, 200*1);
		m_frame.setResizable(false);
		m_frame.setLocation(500, 300);
        m_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        m_frame.add(m_mainPanel);
        m_mainPanel.setLayout(m_mainPanelLayout);
        m_mainPanel.add(new LoginPanel());
        m_frame.setVisible(true);
	}
	
	public void close()
	{
		m_frame.setVisible(false);
	}
	
	public void Repain(String action)
	{
		System.out.println(action);
		if(action.equals("register"))
		{
			m_frame.resize(350*1, 300*1);
			m_mainPanel.removeAll();
			m_mainPanel.add(new RegisterPanel("register"));
			m_mainPanel.validate();
		}
		else if(action.equals("findpassword"))
		{
			m_frame.resize(350*1, 300*1);
			m_mainPanel.removeAll();
			m_mainPanel.add(new RegisterPanel("findpassword"));
			m_mainPanel.validate();
		}
		else if(action.equals("login"))
		{
			m_frame.resize(350*1, 180*1);
			System.out.println("return to login");
			m_mainPanel.removeAll();
			m_mainPanel.add(new LoginPanel());
			m_mainPanel.validate();
		}
	}
	
	private class LoginPanel extends JPanel implements ActionListener{ 
		JLabel m_userLabel = new JLabel("User:");
		JTextField m_userInput = new JTextField(20);
		JLabel m_passwordLabel = new JLabel("Password:");
		JPasswordField m_passwordInput = new JPasswordField(20);
		JButton m_loginButton = new JButton("login");
		JButton m_registerButton = new JButton("register");
		JButton m_findPasswordButton = new JButton("findpassword");
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			JButton source = (JButton) arg0.getSource();
			if(source == m_loginButton)
			{
				String userName = m_userInput.getText();
	        	String password = new String(m_passwordInput.getPassword());
	            if(!userName.equals("") && !password.equals(""))  
	            {
	            	Operation operation = new Operation();
	            	operation.m_operationName = "login";
	            	operation.m_user = userName;
	            	operation.m_password = password;
	            	try {
						operation.m_ip = InetAddress.getLocalHost().getHostAddress();
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					};//m_clientSocket.m_serverSocket.getInetAddress().getHostAddress();
	            	operation.m_port = m_clientSocket.m_serverSocket.getLocalPort();
	            	m_clientSocket.SendToServer(operation);
	            }
			}
			else if(source == m_registerButton)
			{
				Repain("register");
			}
			else if(source == m_findPasswordButton)
			{
				Repain("findpassword");
			}
		}
		
		LoginPanel()
		{
			setLayout(null);
        	m_userLabel.setBounds(10*1,20*1,80*1,25*1);
        	m_userLabel.setFont(new Font("",Font.BOLD,12));
        	m_userInput.setBounds(100*1,20*1,165*1,25*1);
        	m_passwordLabel.setBounds(10*1,50*1,80*1,25*1);
        	m_passwordLabel.setFont(new Font("",Font.BOLD,12));
        	m_passwordInput.setBounds(100*1,50*1,165*1,25*1);
        	m_registerButton.setBounds(10*1,100*1,80*1,30*1);
        	m_registerButton.setFont(new Font("",Font.BOLD,12));
        	m_registerButton.addActionListener(this);
        	m_findPasswordButton.setBounds(110*1,100*1,120*1,30*1);
        	m_findPasswordButton.setFont(new Font("",Font.BOLD,12));
        	m_findPasswordButton.addActionListener(this);
        	m_loginButton.setBounds(250*1,100*1,80*1,30*1);
        	m_loginButton.setFont(new Font("",Font.BOLD,12));
        	m_loginButton.addActionListener(this);
        	add(m_userLabel);
        	add(m_userInput);
        	add(m_passwordLabel);
        	add(m_passwordInput);
        	add(m_registerButton);
        	add(m_findPasswordButton);
        	add(m_loginButton);
		}
	}
	
	private class RegisterPanel extends JPanel implements ActionListener{  
		String m_operation = null;
		JLabel m_userLabel = new JLabel("User:");
		JTextField m_userInput = new JTextField(20);
		JLabel m_firstPasswordLabel = new JLabel("Password:");
		JLabel m_secondPasswordLabel = new JLabel("Password:");
		JPasswordField m_firstPasswordInput = new JPasswordField(20*1);
		JPasswordField m_secondPasswordInput = new JPasswordField(20*1);
		JLabel m_securityQuestionLabel = new JLabel("Security Question: Your mother's birthday", JLabel.LEFT);
		JLabel m_firstAnswerLabel = new JLabel("Answer: ");
		JLabel m_secondAnswerLabel = new JLabel("Answer: ");
		JPasswordField m_firstAnswerInput = new JPasswordField(20);
		JPasswordField m_secondAnswerInput = new JPasswordField(20);
		JButton m_returnButton = new JButton("Return");
		JButton m_funcButton = new JButton("");
		JLabel m_promptLabel = new JLabel("", JLabel.CENTER);
		
		@Override
        public void actionPerformed(ActionEvent e) { 
			JButton source = (JButton) e.getSource();
			if(source == m_returnButton)
			{
				Repain("login");
				return;
			}
        	String userName = m_userInput.getText();
        	String firstPassword = new String(m_firstPasswordInput.getPassword());
        	String secondPassword =  new String(m_secondPasswordInput.getPassword());
        	String firstAnswer = new String(m_firstAnswerInput.getPassword());
        	String secondAnswer = new String(m_secondAnswerInput.getPassword());
        	if(userName.equals("") || firstPassword.equals("") || secondPassword.equals("") || firstAnswer.equals("") || secondAnswer.equals(""))
        	{
        		m_promptLabel.setText("信息不完整");
            	m_promptLabel.setVisible(true);
            	return;
        	}
        	
            if(firstPassword.equals(secondPassword) && firstAnswer.equals(secondAnswer))  
            {
            	Operation operation = new Operation();
            	operation.m_operationName = m_operation;
            	operation.m_user = userName;
            	operation.m_userInfo = new UserInfo(userName, firstPassword, firstAnswer);
            	m_clientSocket.SendToServer(operation);
            }
            else
            {
//            	System.out.println(firstPassword);
//            	System.out.println(secondPassword);
            	m_promptLabel.setText("不一致的密码或问题答案");
            	m_promptLabel.setVisible(true);
            }
        }     
        
        RegisterPanel(String operation)
        {
        	m_funcButton.setText(operation);
        	m_operation = operation;
        	setLayout(null);
        	if(operation.equals("register")){
        	m_userLabel.setBounds(10*1,20*1,80*1,25*1);
        	m_userLabel.setFont(new Font("",Font.BOLD,12));
        	m_userInput.setBounds(100*1,20*1,165*1,25*1);
        	m_firstPasswordLabel.setBounds(10*1,50*1,80*1,25*1);
        	m_firstPasswordLabel.setFont(new Font("",Font.BOLD,12));
        	m_firstPasswordInput.setBounds(100*1,50*1,165*1,25*1);
        	m_secondPasswordLabel.setBounds(10*1,80*1,80*1,25*1);
        	m_secondPasswordLabel.setFont(new Font("",Font.BOLD,12));
        	m_secondPasswordInput.setBounds(100*1,80*1,165*1,25*1);
        	m_securityQuestionLabel.setBounds(10*1,110*1,240*1,25*1);
        	m_securityQuestionLabel.setFont(new Font("",Font.BOLD,12));
        	m_firstAnswerLabel.setBounds(10*1,140*1,80*1,25*1);
        	m_firstAnswerLabel.setFont(new Font("",Font.BOLD,12));
        	m_firstAnswerInput.setBounds(100*1,140*1,165*1,25*1);
        	m_secondAnswerLabel.setBounds(10*1,170*1,80*1,25*1);
        	m_secondAnswerLabel.setFont(new Font("",Font.BOLD,12));
        	m_secondAnswerInput.setBounds(100*1,170*1,165*1,25*1);
        	m_returnButton.setBounds(10*1,200*1,100*1,30*1);
        	m_returnButton.setFont(new Font("",Font.BOLD,12));
        	m_returnButton.addActionListener(this);
        	m_funcButton.setBounds(210*1,200*1,100*1,30*1);
        	m_funcButton.setFont(new Font("",Font.BOLD,12));
        	m_funcButton.addActionListener(this);
        	m_promptLabel.setBounds(0*1,230*1,350*1,30*1);
        	m_promptLabel.setFont(new Font("",Font.BOLD,12));
        	m_promptLabel.setVisible(false);
        	}
        	else{
        		m_userLabel.setBounds(10*1,20*1,80*1,25*1);
            	m_userLabel.setFont(new Font("",Font.BOLD,12));
            	m_userInput.setBounds(100*1,20*1,165*1,25*1);
            	m_securityQuestionLabel.setBounds(10*1,50*1,240*1,25*1);
            	m_securityQuestionLabel.setFont(new Font("",Font.BOLD,12));
            	m_firstAnswerLabel.setBounds(10*1,80*1,80*1,25*1);
            	m_firstAnswerLabel.setFont(new Font("",Font.BOLD,12));
            	m_firstAnswerInput.setBounds(100*1,80*1,165*1,25*1);
            	m_secondAnswerLabel.setBounds(10*1,110*1,80*1,25*1);
            	m_secondAnswerLabel.setFont(new Font("",Font.BOLD,12));
            	m_secondAnswerInput.setBounds(100*1,110*1,165*1,25*1);
            	m_firstPasswordLabel.setText("New Password:");
            	m_firstPasswordLabel.setBounds(10*1,140*1,90*1,25*1);
            	m_firstPasswordLabel.setFont(new Font("",Font.BOLD,12));
            	m_firstPasswordInput.setBounds(100*1,140*1,165*1,25*1);
            	m_secondPasswordLabel.setText("New Password:");
            	m_secondPasswordLabel.setBounds(10*1,170*1,90*1,25*1);
            	m_secondPasswordLabel.setFont(new Font("",Font.BOLD,12));
            	m_secondPasswordInput.setBounds(100*1,170*1,165*1,25*1);
            	m_returnButton.setBounds(10*1,200*1,100*1,30*1);
            	m_returnButton.setFont(new Font("",Font.BOLD,12));
            	m_returnButton.addActionListener(this);
            	m_funcButton.setBounds(210*1,200*1,120*1,30*1);
            	m_funcButton.setFont(new Font("",Font.BOLD,12));
            	m_funcButton.addActionListener(this);
            	m_promptLabel.setBounds(0*1,230*1,350*1,30*1);
            	m_promptLabel.setFont(new Font("",Font.BOLD,12));
            	m_promptLabel.setVisible(false);
        	}
        	add(m_userLabel);
        	add(m_userInput);
        	add(m_firstPasswordLabel);
        	add(m_firstPasswordInput);
        	add(m_secondPasswordLabel);
        	add(m_secondPasswordInput);
        	add(m_securityQuestionLabel);
        	add(m_firstAnswerLabel);
        	add(m_firstAnswerInput);
        	add(m_secondAnswerLabel);
        	add(m_secondAnswerInput);
        	add(m_returnButton);
        	add(m_funcButton);
        	add(m_promptLabel);
        	}
        	
        }
        

	 public static Font makeFont(){
		 return new Font("",Font.BOLD,16);
	 }
}
