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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
		System.out.println("register回复");
		if(m_userInfoMap.containsKey(m_operationObj.m_user))
		{
			System.out.println("register回复");
			Operation operation = new Operation();
			operation.m_operationName = "registerFail";
			operation.m_detail = "已存在的用户";
			try {
				m_objOutputStream.writeObject(operation);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("register回复2");
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
		System.out.println("login回复");
		UserInfo correctUserInfo = (UserInfo) m_userInfoMap.get(m_operationObj.m_user);
		if(m_userInfoMap.containsKey(m_operationObj.m_user) &&
				correctUserInfo.m_password.equals(m_operationObj.m_password))
		{
			m_user = m_operationObj.m_user;
			m_userOnlineMap.put(m_operationObj.m_user, new OnlineUserInfo(m_operationObj.m_ip, m_operationObj.m_port,
					m_operationObj.m_udpIp, m_operationObj.m_udpPort));
			
			Operation operation = new Operation();
			operation.m_operationName = "loginSuccess";
			operation.m_user = m_user;
			try {
				m_objOutputStream.writeObject(operation);
				//用户列表
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
				//离线消息
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
				
				//广播用户上线
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
			operation.m_detail = "不正确的账号或密码";
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
		if(m_userOnlineMap.containsKey(m_operationObj.m_user))
			m_userOnlineMap.remove(m_operationObj.m_user);
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
		operation.m_operationName = "getOtherClientAddrRsp";
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
		System.out.println("findpassword回复");
		if(m_userInfoMap.containsKey(m_operationObj.m_user))
		{
			UserInfo correctUserInfo = (UserInfo) m_userInfoMap.get(m_operationObj.m_user);
			UserInfo newUserInfo = m_operationObj.m_userInfo;
			if(correctUserInfo.m_quessionAnswer.equals(newUserInfo.m_quessionAnswer))
			{
				m_userInfoMap.replace(m_operationObj.m_user, newUserInfo);
				Operation operation = new Operation();
				operation.m_operationName = "findpasswordSuccess";
				operation.m_detail = "不正确的账号或密码";
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
				operation.m_detail = "密保答案错误";
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
			operation.m_detail = "不存在的用户";
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
		System.out.println("客户端已连接！");
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
			else if(m_operationObj.m_operationName.equals("getOtherClientAddr"))
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