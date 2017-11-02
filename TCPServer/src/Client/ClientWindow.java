package Client;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

public class ClientWindow extends JFrame {
	public JFrame m_frame = new JFrame("Login Example");
	public JPanel m_mainPanel = new JPanel();   
	public CardLayout m_mainPanelLayout = new CardLayout();
	
	ClientWindow()
	{
		m_frame.setSize(350, 200);
		m_frame.setResizable(false);
		m_frame.setLocation(500, 500);
        m_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        m_frame.add(m_mainPanel);
        m_mainPanel.setLayout(m_mainPanelLayout);
        m_mainPanel.add(new LoginPanel());
        m_frame.setVisible(true);
        
        JFrame frame = new JFrame("Login Example2");
        frame.setSize(350, 300);
        frame.add(new ChatPanel());
        frame.setVisible(true);
	}

	public static void main(String[] args) {
        /* 创建面板，这个类似于 HTML 的 div 标签
         * 我们可以创建多个面板并在 JFrame 中指定位置
         * 面板中我们可以添加文本字段，按钮及其他组件。
         */
        //JPanel panel = new JPanel();    
        // 添加面板
        ClientWindow clientWindow = new ClientWindow(); 
        
        
//        frame.add(clientWindow.m_registerPanel);
//        clientWindow.m_registerPanel.setVisible(false);
//        clientWindow.m_loginPanel.setVisible(true);
        /* 
         * 调用用户定义的方法并添加组件到面板
         */
//        clientWindow.InitLoginPanel();
//        clientWindow.InitRegisterPanel();

        // 设置界面可见
	}
	
	private void Repain(String action)
	{
		System.out.println(action);
		if(action.equals("register"))
		{
			m_frame.resize(350, 300);
			m_mainPanel.removeAll();
			m_mainPanel.add(new RegisterPanel("register"));
			m_mainPanel.validate();
		}
		else if(action.equals("findpassword"))
		{
			m_frame.resize(350, 300);
			m_mainPanel.removeAll();
			m_mainPanel.add(new RegisterPanel("findpassword"));
			m_mainPanel.validate();
		}
		else if(action.equals("login"))
		{
			m_frame.resize(350, 180);
			System.out.println("return to login");
			m_mainPanel.removeAll();
			m_mainPanel.add(new LoginPanel());
			m_mainPanel.validate();
		}
	}
	
	private class ChatPanel extends JPanel implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		} 
		
		ChatPanel() {
			setLayout(null);
			String[] words= { "quick", "brown", "hungry","quick", "brown", "hungry", "wild","quick", "brown", "hungry", "wild","quick", "brown", "hungry", "wild"};
			JList<String> wordList = new JList<>(words);
			wordList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//			wordList.setPreferredSize(new Dimension(40,180));
//			wordList.setVisibleRowCount(8);
//			wordList.setBounds(250, 0, 100, 200);
			wordList.addListSelectionListener(new ListSelectionListener() {
				
				@Override
				public void valueChanged(ListSelectionEvent e) {
					// TODO Auto-generated method stub
					System.out.println(wordList.getSelectedValue());
					wordList.setSelectionBackground(new Color(0xFF0000));
				}
			});
			JScrollPane scrollPane = new JScrollPane(wordList);
//			scrollPane.setSize(100,200);
			scrollPane.setBounds(200, 0, 150, 200);
			JTextArea jTextArea = new JTextArea(10, 15);
			jTextArea.setBounds(0, 0, 200, 200);
			JTextArea inputArea = new JTextArea(10, 15);
			inputArea.setBounds(0, 205, 350, 90);
			add(scrollPane);
			add(jTextArea);
			add(inputArea);
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
        	m_userLabel.setBounds(10,20,80,25);
        	m_userInput.setBounds(100,20,165,25);
        	m_passwordLabel.setBounds(10,50,80,25);
        	m_passwordInput.setBounds(100,50,165,25);
        	m_registerButton.setBounds(10,80,80,30);
        	m_registerButton.addActionListener(this);
        	m_findPasswordButton.setBounds(110,80,80,30);
        	m_findPasswordButton.addActionListener(this);
        	m_loginButton.setBounds(210,80,80,30);
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
		JPasswordField m_firstPasswordInput = new JPasswordField(20);
		JPasswordField m_secondPasswordInput = new JPasswordField(20);
		JLabel m_securityQuestionLabel = new JLabel("Security Question: ");
		JLabel m_firstAnswerLabel = new JLabel("Answer: ");
		JLabel m_secondAnswerLabel = new JLabel("Answer: ");
		JPasswordField m_firstAnswerInput = new JPasswordField(20);
		JPasswordField m_secondAnswerInput = new JPasswordField(20);
		JButton m_returnButton = new JButton("Return");
		JButton m_funcButton = new JButton("");
		JLabel m_promptLabel = new JLabel("");
		
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
        	}
        	
            if(firstPassword.equals(secondPassword) && firstAnswer.equals(secondAnswer))  
            {
            	Operation operation = new Operation();
            	operation.m_operationName = m_operation;
            	operation.m_user = userName;
            	operation.m_userInfo = new UserInfo(userName, firstPassword, firstAnswer);
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
        	m_userLabel.setBounds(10,20,80,25);
        	m_userInput.setBounds(100,20,165,25);
        	m_firstPasswordLabel.setBounds(10,50,80,25);
        	m_firstPasswordInput.setBounds(100,50,165,25);
        	m_secondPasswordLabel.setBounds(10,80,80,25);
        	m_secondPasswordInput.setBounds(100,80,165,25);
        	m_securityQuestionLabel.setBounds(10,110,165,25);
        	m_firstAnswerLabel.setBounds(10,140,80,25);
        	m_firstAnswerInput.setBounds(100,140,165,25);
        	m_secondAnswerLabel.setBounds(10,170,80,25);
        	m_secondAnswerInput.setBounds(100,170,165,25);
        	m_returnButton.setBounds(10,200,100,30);
        	m_returnButton.addActionListener(this);
        	m_funcButton.setBounds(210,200,100,30);
        	m_funcButton.addActionListener(this);
        	m_promptLabel.setBounds(50,230,100,30);
        	m_promptLabel.setVisible(false);
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

}
