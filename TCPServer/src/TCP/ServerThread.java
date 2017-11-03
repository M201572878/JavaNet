package TCP;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServerThread extends Thread
{
	public Socket m_socket = null;
//	public BufferedWriter m_bufferWriter = null;
	public Operation m_operationObj = null;
	public ObjectInputStream m_objInputStream = null;
	public ObjectOutputStream m_objOutputStream = null;
	public ServerReply m_ServerReplyObj = null; 
	public DataOutputStream m_outStr = null;
	
	public boolean m_continue = true;
	public static HashMap<String, UserInfo> m_userInfoMap = new HashMap<String, UserInfo>();
	public static List<String> m_userOnline = new ArrayList<String>();
	public static HashMap<String, ArrayList<String>> m_offlineMessage = new HashMap<String, ArrayList<String>>();
	public  ArrayList<String> m_offlineContent = null;
	public ServerThread(Socket socket)
	{
		m_socket = socket;
	}
	
//	public void ResponseToClient(String responseStr)
//	{
//		try {
//			m_bufferWriter.write(responseStr);
//			m_bufferWriter.newLine();
//			m_bufferWriter.flush();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	public void DealRegister() throws IOException 
	{
		System.out.println("register回复");
		if(m_userInfoMap.containsKey(m_operationObj.m_user))
		{
			System.out.println("register回复");
//			ResponseToClient("注册失败，已存在的账号");
			m_ServerReplyObj.m_responseStr = "注册失败，已存在的账号";
			
			
		}
		else
		{
			System.out.println("register回复2");
			m_userInfoMap.put(m_operationObj.m_user, m_operationObj.m_userInfo);
//			m_userInfoMap.put(m_operationObj.m_user, m_operationObj.m_password);
//			ResponseToClient("注册成功，请登陆");
			m_ServerReplyObj.m_responseStr = "注册成功，请登陆";
		}
			m_objOutputStream.writeUnshared(m_ServerReplyObj);
		
	}
	
	
	public void DealLogin() throws IOException 
	{
		System.out.println("login回复");
		UserInfo correctUserInfo = (UserInfo) m_userInfoMap.get(m_operationObj.m_user);
		if(m_userInfoMap.containsKey(m_operationObj.m_user) &&
				correctUserInfo.m_password.equals(m_operationObj.m_password))
		{
			m_userOnline.add(m_operationObj.m_user);
//			ResponseToClient("登陆成功");
			m_ServerReplyObj.m_responseStr = "登陆成功";
			m_objOutputStream.writeUnshared(m_ServerReplyObj);
			if(m_offlineMessage.containsKey(m_operationObj.m_user)) {
				m_offlineContent = new ArrayList<String>();
				m_offlineContent = m_offlineMessage.get(m_operationObj.m_user);
				m_offlineMessage.remove(m_operationObj.m_user);
				int i = 0;
				while(true) {
					            String[] MessageStr = m_offlineContent.toArray(new String[m_offlineContent.size()]);
								m_outStr.writeUTF(MessageStr[i++]);
								if(i == m_offlineContent.size()) {
									break;
								}
									
								
				}
			}
		}
		else
		{
//			ResponseToClient("错误的用户名或密码");
			m_ServerReplyObj.m_responseStr = "错误的用户名或密码";
			m_objOutputStream.writeUnshared(m_ServerReplyObj);
		}
		
	}
	
	public void DealFindPassWord() throws IOException
	{
		System.out.println("findpassword回复");
		if(m_userInfoMap.containsKey(m_operationObj.m_user))
		{
			UserInfo correctUserInfo = (UserInfo) m_userInfoMap.get(m_operationObj.m_user);
			UserInfo newUserInfo = m_operationObj.m_userInfo;
			if(correctUserInfo.m_quessionAnswer.equals(newUserInfo.m_quessionAnswer))
			{
//				ResponseToClient("密码找回成功,请登陆");
				m_ServerReplyObj.m_responseStr = "密码找回成功,请登陆";
				m_userInfoMap.replace(m_operationObj.m_user, newUserInfo);
			}
			else
			{
//				ResponseToClient("问题答案错误，密码找回失败");
				m_ServerReplyObj.m_responseStr = "问题答案错误，密码找回失败";
			}
			
		}
		else
		{
//			ResponseToClient("不存在的用户，密码找回失败");
			m_ServerReplyObj.m_responseStr = "不存在的用户，密码找回失败";
		}
		m_objOutputStream.writeUnshared(m_ServerReplyObj);
	}
	
	public void DealOfflineMessage() throws IOException
	{
		System.out.println("OfflineMessage回复");
		if(!m_userInfoMap.containsKey(m_operationObj.m_user)) {
			m_ServerReplyObj.m_responseStr = "该用户未注册";
			
		}
		else if(!m_userOnline.contains(m_operationObj.m_wantSendUser))
		{
			m_ServerReplyObj.m_responseStr = "用户未上线，发送离线消息";		
		}
		else
		{
//			ResponseToClient("不存在的用户，密码找回失败");
			m_ServerReplyObj.m_responseStr = "当前用户在线，发送即时消息";
		}
		m_objOutputStream.writeUnshared(m_ServerReplyObj);
			try {
				m_operationObj = (Operation) m_objInputStream.readObject();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if(!m_offlineMessage.containsKey(m_operationObj.m_wantSendUser)) {
				m_offlineContent = new ArrayList<String>();
				m_offlineContent.add(m_operationObj.m_MessageFrom);
				m_offlineContent.add(m_operationObj.m_wantSendMessage);
				m_offlineMessage.put(m_operationObj.m_wantSendUser,(ArrayList<String>) m_offlineContent);
				m_offlineContent.clear();
			}
			else{
				m_offlineContent = new ArrayList<String>();
				m_offlineContent = m_offlineMessage.get(m_operationObj.m_wantSendUser);
				m_offlineContent.add(m_operationObj.m_MessageFrom);
				m_offlineContent.add(m_operationObj.m_wantSendMessage);
				m_offlineMessage.put(m_operationObj.m_wantSendUser,(ArrayList<String>) m_offlineContent);
				m_offlineContent.clear();
			}
	}
	
	public void Init()
	{
		try {
			m_objInputStream = new ObjectInputStream(m_socket.getInputStream());
//			m_bufferWriter = new BufferedWriter (new OutputStreamWriter(m_socket.getOutputStream(), "UTF-8"));
			m_objOutputStream = new ObjectOutputStream(m_socket.getOutputStream());
			m_outStr = new DataOutputStream(m_socket.getOutputStream());
			m_ServerReplyObj = new ServerReply();
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
				e.printStackTrace();
				continue;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
			System.out.println(m_operationObj.m_operationName);
			
			if(m_operationObj.m_operationName.equals("register"))
			{
				try {
					DealRegister();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if(m_operationObj.m_operationName.equals("login"))
			{
				try {
					DealLogin();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if(m_operationObj.m_operationName.equals("findpassword"))
			{
				try {
					DealFindPassWord();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if(m_operationObj.m_operationName.equals("logoff"))
			{
				if(m_userOnline.contains(m_operationObj.m_user))
					m_userOnline.remove(m_operationObj.m_user);
				try {
					m_socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
				
			}
		}
	}
	
}