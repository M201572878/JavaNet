package TCP;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;

public class ServerThread extends Thread
{
	public Socket m_socket = null;
	public BufferedWriter m_bufferWriter = null;
	public Operation m_operationObj = null;
	public ObjectInputStream m_objInputStream = null;
	public boolean m_continue = true;
	public static HashMap m_userInfoMap = new HashMap();
	
	
	public ServerThread(Socket socket)
	{
		m_socket = socket;
	}
	
	public void ResponseToClient(String responseStr)
	{
		try {
			m_bufferWriter.write(responseStr);
			m_bufferWriter.newLine();
			m_bufferWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void DealRegister()
	{
		System.out.println("register回复");
		if(m_userInfoMap.containsKey(m_operationObj.m_user))
		{
			System.out.println("register回复");
			ResponseToClient("注册失败，已存在的账号");
		}
		else
		{
			System.out.println("register回复2");
			m_userInfoMap.put(m_operationObj.m_user, m_operationObj.m_userInfo);
//			m_userInfoMap.put(m_operationObj.m_user, m_operationObj.m_password);
			ResponseToClient("注册成功，请登陆");
		}
	}
	
	public void DealLogin()
	{
		System.out.println("login回复");
		UserInfo correctUserInfo = (UserInfo) m_userInfoMap.get(m_operationObj.m_user);
		if(m_userInfoMap.containsKey(m_operationObj.m_user) &&
				correctUserInfo.m_password.equals(m_operationObj.m_password))
		{
			ResponseToClient("登陆成功");
		}
		else
		{
			ResponseToClient("错误的用户名或密码");
		}
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
				ResponseToClient("密码找回成功,请登陆");
				m_userInfoMap.replace(m_operationObj.m_user, newUserInfo);
			}
			else
			{
				ResponseToClient("问题答案错误，密码找回失败");
			}
			
		}
		else
		{
			ResponseToClient("不存在的用户，密码找回失败");
		}
	}
	
	public void Init()
	{
		try {
			m_objInputStream = new ObjectInputStream(m_socket.getInputStream());
			m_bufferWriter = new BufferedWriter (new OutputStreamWriter(m_socket.getOutputStream(), "UTF-8"));
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
		}
	}
	
}