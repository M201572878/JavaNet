package TCP;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.SwingUtilities;

public class ServerThread extends Thread
{
	public Socket m_socket = null;
	public BufferedWriter m_bufferWriter = null;
	public Operation m_operationObj = null;
	public String m_user = null;
	public ObjectInputStream m_objInputStream = null;
	public ObjectOutputStream m_objOutputStream = null;
	public boolean m_continue = true;
	public static HashMap<String, UserInfo> m_userInfoMap = new HashMap<String, UserInfo>();
	public static HashMap m_userOnlineMap = new HashMap<String, OnlineUserInfo>();
	public static HashMap<String, ArrayList<OfflineMsg>> m_offlineMsgMap = new HashMap<String, ArrayList<OfflineMsg>>();
	public static ArrayList<ObjectOutputStream> m_objOutputList = new ArrayList<ObjectOutputStream>();
	
	private class OfflineMsg{
		String m_msg = null;
		String m_sender = null;
		OfflineMsg(String sender, String msg)
		{
			m_sender = sender;
			m_msg = msg;
		}
	}
	
	private class OnlineUserInfo{
		public String m_ip = null;
		public String m_udpIp = null;
		public int m_port = 0;
		public int m_udpPort = 0;
		
		OnlineUserInfo(String ip, int port, String udpIp, int udpPort)
		{
			m_ip = ip;
			m_port = port;
			m_udpIp = udpIp;
			m_udpPort = udpPort;
		}
	}
	
	public ServerThread(Socket socket)
	{
		m_socket = socket;
	}
	

	public void SendBroadcast(Operation operation)
	{
		for(ObjectOutputStream objectOutputStream: m_objOutputList)
		{
			try {
				objectOutputStream.writeObject(operation);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	

	public void DealRegister()
	{
		System.out.println("register�ظ�");
		if(m_userInfoMap.containsKey(m_operationObj.m_user))
		{
			System.out.println("register�ظ�");
			Operation operation = new Operation();
			operation.m_operationName = "registerFail";
			operation.m_detail = "�Ѵ��ڵ��û�";
			try {
				m_objOutputStream.writeObject(operation);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("register�ظ�2");
			m_userInfoMap.put(m_operationObj.m_user, m_operationObj.m_userInfo);
//			m_userInfoMap.put(m_operationObj.m_user, m_operationObj.m_password);
			Operation operation = new Operation();
			operation.m_operationName = "registerSuccess";
			try {
				m_objOutputStream.writeObject(operation);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void DealConfirmOfflineMsg()
	{
		m_offlineMsgMap.remove(m_user);
	}
	
	public void DealLogin()
	{
		System.out.println("login�ظ�");
		UserInfo correctUserInfo = (UserInfo) m_userInfoMap.get(m_operationObj.m_user);
		if(m_userInfoMap.containsKey(m_operationObj.m_user) &&
				correctUserInfo.m_password.equals(m_operationObj.m_password))
		{
			m_user = m_operationObj.m_user;
			String ipString = m_socket.getInetAddress().getHostAddress();
			m_userOnlineMap.put(m_operationObj.m_user, new OnlineUserInfo(ipString, m_operationObj.m_port,
					ipString, m_operationObj.m_udpPort));
			SwingUtilities.invokeLater(new Runnable() {
	            @Override
	            public void run() {
	            		String time = new String();
	            		Calendar now = Calendar.getInstance();
	            		String temp = new String();
	            		time = now.get(Calendar.YEAR)+"/"+now.get(Calendar.MONTH)+"/"+now.get(Calendar.DATE)+" ";
	            		temp = now.get(Calendar.HOUR_OF_DAY)+":"+now.get(Calendar.MINUTE)+":"+now.get(Calendar.SECOND);
	            		time = time.concat(temp);		
	            	System.out.println(time);
	            	ServerWindow.m_onlineListTextArea.append(time);
					ServerWindow.m_onlineListTextArea.append("\nuser:  "+ m_operationObj.m_user + "  logged  in.\n");
	            }
	        });
			Operation operation = new Operation();
			operation.m_operationName = "loginSuccess";
			operation.m_user = m_user;
			try {
				m_objOutputStream.writeObject(operation);
				//�û��б�
				Operation operation2 = new Operation();
				operation2.m_operationName = "userListRsp";
				operation2.m_users = "";
				operation2.m_userStates = "";
				for (String key: m_userInfoMap.keySet()) { 
					operation2.m_users += key;
					operation2.m_users += "\n";
					if(m_userOnlineMap.containsKey(key))
					{
						operation2.m_userStates += "online";
					}
					else
					{
						operation2.m_userStates += "offline";
					}
					operation2.m_userStates += "\n";
				}  
				m_objOutputStream.writeObject(operation2);
				//������Ϣ
				Operation operation3 = new Operation();
				operation3.m_operationName = "offlineMsgRsp";
				if(m_offlineMsgMap.containsKey(m_operationObj.m_user))
				{
					for(OfflineMsg msg: (ArrayList<OfflineMsg>)m_offlineMsgMap.get(m_operationObj.m_user))
					{
						operation3.m_msg = msg.m_msg;
						operation3.m_user = msg.m_sender;
						m_objOutputStream.writeObject(operation3);
					}
				}
				
				//�㲥�û�����
				Operation broadcastOperation = new Operation();
				broadcastOperation.m_operationName = "userLoginNotify";
				broadcastOperation.m_user = m_user;
				SendBroadcast(broadcastOperation);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			Operation operation = new Operation();
			operation.m_operationName = "loginFail";
			operation.m_detail = "����ȷ���˺Ż�����";
			try {
				m_objOutputStream.writeObject(operation);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void DealLogoff()
	{
		if(m_userOnlineMap.containsKey(m_operationObj.m_user)){
			m_userOnlineMap.remove(m_operationObj.m_user);
			SwingUtilities.invokeLater(new Runnable() {
	            @Override
	            public void run() {
	            		String time = new String();
	            		Calendar now = Calendar.getInstance();
	            		String temp = new String();
	            		time = now.get(Calendar.YEAR)+"/"+now.get(Calendar.MONTH)+"/"+now.get(Calendar.DATE)+" ";
	            		temp = now.get(Calendar.HOUR_OF_DAY)+":"+now.get(Calendar.MINUTE)+":"+now.get(Calendar.SECOND);
	            		time = time.concat(temp);		
	            	System.out.println(time);
	            	ServerWindow.m_onlineListTextArea.append(time);
					ServerWindow.m_onlineListTextArea.append("\nuser:  "+ m_operationObj.m_user + "  logged  out.\n");
	            }
	        });
		}
			
		try {
			m_socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void DealOnlineMsg()
	{
		Operation operation = new Operation();
		operation.m_operationName = "onlineMsgRsp";
		OnlineUserInfo onlineUserInfo = (OnlineUserInfo) m_userOnlineMap.get(m_operationObj.m_targetUser);
		operation.m_ip = onlineUserInfo.m_ip;
		operation.m_port = onlineUserInfo.m_port;
		operation.m_targetUser = m_operationObj.m_targetUser;
		try {
			m_objOutputStream.writeObject(operation);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void DealOfflineMsg()
	{
		if(!m_offlineMsgMap.containsKey(m_operationObj.m_targetUser))
		{
			m_offlineMsgMap.put(m_operationObj.m_targetUser, new ArrayList<OfflineMsg>());
		}
		ArrayList<OfflineMsg> list = (ArrayList<OfflineMsg>) m_offlineMsgMap.get(m_operationObj.m_targetUser);
		list.add(new OfflineMsg(m_operationObj.m_user, m_operationObj.m_msg));
	}
	
	public void DealFindPassWord()
	{
		System.out.println("findpassword�ظ�");
		if(m_userInfoMap.containsKey(m_operationObj.m_user))
		{
			UserInfo correctUserInfo = (UserInfo) m_userInfoMap.get(m_operationObj.m_user);
			UserInfo newUserInfo = m_operationObj.m_userInfo;
			if(correctUserInfo.m_quessionAnswer.equals(newUserInfo.m_quessionAnswer))
			{
				m_userInfoMap.replace(m_operationObj.m_user, newUserInfo);
				Operation operation = new Operation();
				operation.m_operationName = "findpasswordSuccess";
				operation.m_detail = "����ȷ���˺Ż�����";
				try {
					m_objOutputStream.writeObject(operation);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			{
				Operation operation = new Operation();
				operation.m_operationName = "findpasswordFail";
				operation.m_detail = "�ܱ��𰸴���";
				try {
					m_objOutputStream.writeObject(operation);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		else
		{
			Operation operation = new Operation();
			operation.m_operationName = "findpasswordFail";
			operation.m_detail = "�����ڵ��û�";
			try {
				m_objOutputStream.writeObject(operation);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void Init()
	{
		try {
			m_objInputStream = new ObjectInputStream(m_socket.getInputStream());
			m_objOutputStream = new ObjectOutputStream(m_socket.getOutputStream());
			m_objOutputList.add(m_objOutputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run()
	{
		Init();
		System.out.println("�ͻ��������ӣ�");
		while(m_continue)
		{
			try {
				m_operationObj = (Operation) m_objInputStream.readObject();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
				m_userOnlineMap.remove(m_user);
				m_objOutputList.remove(m_objOutputStream);
				
				Operation broadcastOperation = new Operation();
				broadcastOperation.m_operationName = "userLogoutNotify";
				broadcastOperation.m_user = m_user;
				SendBroadcast(broadcastOperation);
				break;
			} catch (IOException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
				m_userOnlineMap.remove(m_user);
				m_objOutputList.remove(m_objOutputStream);
				
				Operation broadcastOperation = new Operation();
				broadcastOperation.m_operationName = "userLogoutNotify";
				broadcastOperation.m_user = m_user;
				SendBroadcast(broadcastOperation);
				break;
			}
			System.out.println(m_operationObj.m_operationName);
			MultiTreadServer.ShowAllUser();
			if(m_operationObj.m_operationName.equals("register"))
			{
				DealRegister();
			}
			else if(m_operationObj.m_operationName.equals("login"))
			{
				DealLogin();
			}
			else if(m_operationObj.m_operationName.equals("findpassword"))
			{
				DealFindPassWord();
			}
			else if(m_operationObj.m_operationName.equals("onlineMsgReq"))
			{
				DealOnlineMsg();
			}
			else if(m_operationObj.m_operationName.equals("offlineMsgReq"))
			{
				DealOfflineMsg();
			}
			else if(m_operationObj.m_operationName.equals("logoff"))
			{
				DealLogoff();
				break;
			}
			else if(m_operationObj.m_operationName.equals("confirmOfflineMsg"))
			{
				DealConfirmOfflineMsg();
			}
		}
	}
	
}