package Client;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

import javax.swing.JOptionPane;

import TCP.Operation;

public class ClientSocket {
	public Socket m_SocketToServer = null;
	public ServerSocket m_serverSocket = null;
	public DatagramSocket m_fileServer = null;
	public ObjectOutputStream m_objOutputStream = null;
	public ObjectInputStream m_objInputStream = null;
	public Operation m_operationObj = null;
	public HashMap m_otherClientSocketMap = new HashMap<String, OtherClientSocketInfo>();
	public LoginWindow m_loginWindow = null;
	public ContactWindow m_contaContactWindow = null;
	public File m_sendFile = null;
	public String m_receiveFileName = null;
	public boolean Init() {
		try {
			m_SocketToServer = new Socket("localhost", 8088);
			m_serverSocket = new ServerSocket(0);
			m_fileServer = new DatagramSocket(0);
			m_objOutputStream = new ObjectOutputStream(m_SocketToServer.getOutputStream());
			m_objInputStream = new ObjectInputStream(m_SocketToServer.getInputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "连接失败", "tips", JOptionPane.ERROR_MESSAGE);
			return false;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "连接失败", "tips", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			return false;
		}finally {
//			JOptionPane.showMessageDialog(null, "hello.");
//			System.exit(0);
		}
		
		//接收服务器消息的线程
		ReceiveServerThread receiveServerThread = new ReceiveServerThread(this);
		receiveServerThread.start();
		
		//监听其他客户端连接的线程
		ListenOtherClientThread listenOtherClientThread = new ListenOtherClientThread();
		listenOtherClientThread.start();
		return true;
	}
	
	public void SendToServer(Operation operation)
	{
		try {
			operation = MD5tools.makeCipherObject(operation);
			m_objOutputStream.writeObject(operation);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//连接到其他客户端
	public void ConnectToOtherClient(String otherCientUserName, String host, int port){
		try {
			Socket socetToOtherClient = new Socket(host, port);
			m_otherClientSocketMap.put(otherCientUserName, new OtherClientSocketInfo(socetToOtherClient));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//发送到其他客户端
	public void SendMessageToOtherClient(String otherCientUserName, Operation operation)
	{
		OtherClientSocketInfo otherClientSocketInfo = (OtherClientSocketInfo) m_otherClientSocketMap.get(otherCientUserName);
		try {
			otherClientSocketInfo.m_toOtherClientObjOutputStream.writeObject(operation);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private class OtherClientSocketInfo{
		Socket m_socketToOtherClient = null;
		ObjectOutputStream m_toOtherClientObjOutputStream = null;
		ObjectInputStream m_toOtherClientObjInputStream = null;
		OtherClientSocketInfo(Socket socket){
			m_socketToOtherClient = socket;
			try {
				m_toOtherClientObjOutputStream = new ObjectOutputStream(m_socketToOtherClient.getOutputStream());
				m_toOtherClientObjInputStream = new ObjectInputStream(m_socketToOtherClient.getInputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	//接收服务器消息的线程
	private class ReceiveServerThread extends Thread{
		ClientSocket m_parent = null;
		ReceiveServerThread(ClientSocket clientSocket){
			m_parent = clientSocket;
		}
		@Override
		public void run(){
			while(true)
			{
				try {
					m_operationObj = (Operation) m_objInputStream.readObject();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(m_operationObj.m_operationName.equals("registerSuccess"))
				{
					JOptionPane.showMessageDialog(null, "注册成功", "tips", JOptionPane.INFORMATION_MESSAGE);
					m_loginWindow.Repain("login");
				}
				else if(m_operationObj.m_operationName.equals("findpasswordSuccess"))
				{
					JOptionPane.showMessageDialog(null, "找回密码成功", "tips", JOptionPane.INFORMATION_MESSAGE);
					m_loginWindow.Repain("login");
				}
				else if(m_operationObj.m_operationName.equals("loginSuccess"))
				{
					m_contaContactWindow = new ContactWindow(m_operationObj.m_user, m_parent);
					m_loginWindow.close();
				}
				else if(m_operationObj.m_operationName.equals("userListRsp"))
				{
					String[] users = m_operationObj.m_users.split("\n");
					String[] userStates = m_operationObj.m_userStates.split("\n");
					m_contaContactWindow.AddUsers(users, userStates);
				}
				else if(m_operationObj.m_operationName.equals("offlineMsgRsp"))
				{
					m_contaContactWindow.AddMsg(m_operationObj.m_user, m_operationObj.m_msg);
				}
				else if(m_operationObj.m_operationName.equals("onlineMsgRsp"))
				{
					ConnectToOtherClient(m_operationObj.m_targetUser, m_operationObj.m_ip, m_operationObj.m_port);
				}
				else if(m_operationObj.m_operationName.equals("userLoginNotify"))
				{
					String user = m_operationObj.m_user;
					m_contaContactWindow.ChangeContactState(user, "online");
					m_contaContactWindow.repaint();
				}
				else if(m_operationObj.m_operationName.equals("userLogoutNotify"))
				{
					String user = m_operationObj.m_user;
					m_contaContactWindow.ChangeContactState(user, "offline");
				}
			}
			
		}
	}
	
	
	private class FileServerThread extends Thread{
		@Override
		public void run(){
			while(true)
			{
				byte[] buf=new byte[1024];
		        DatagramPacket packet=new DatagramPacket(buf, buf.length);
		        System.out.println("I am waiting.");
		        try {
					m_fileServer.receive(packet);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        byte[] mess=packet.getData();
			}
		}
	}
	
	//监听其他客户端连接的线程
	private class ListenOtherClientThread extends Thread{
		@Override
		public void run(){
			Socket socket = null;
			while(true)
			{
				try {
					socket = m_serverSocket.accept();
					ReceiveOtherClient receiveOtherClient = new ReceiveOtherClient(socket);
					receiveOtherClient.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private class SendFileThread extends Thread{
		@Override
		public void run(){
			
		}
	}
	
	//接收其他客户端消息的线程
	private class ReceiveOtherClient extends Thread{
		Socket m_otherClientSocket = null;
		String m_otherClientUserName = null;
		ObjectOutputStream m_otherClientObjOutputStream = null;
		ObjectInputStream m_otherClientObjInputStream = null;
		ReceiveOtherClient(Socket socket){
			m_otherClientSocket = socket;
			try {
				m_otherClientObjInputStream = new ObjectInputStream(m_otherClientSocket.getInputStream());
				m_otherClientObjOutputStream = new ObjectOutputStream(m_otherClientSocket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		@Override
		public void run(){
			Socket socket = null;
			while(true)
			{
				try {
					Operation operation = (Operation) m_otherClientObjInputStream.readObject();
					System.out.println("from other client:"+operation.m_operationName + operation.m_msg);
					if(operation.m_operationName.equals("onlineChatWithOtherClient"))
					{
						m_contaContactWindow.AddMsg(operation.m_user, operation.m_msg);
					}
					else if(operation.m_operationName.equals("sendFileReq"))
					{
						m_contaContactWindow.AddMsg(operation.m_user, operation.m_msg);
						if(m_contaContactWindow.m_fileTransState)
						{
							Operation operation2 = new Operation();
							operation.m_operationName = "sendFileRsp";
							operation.m_user = m_contaContactWindow.m_user;
							operation.m_msg = "another file is transferring, please wait..";
							SendMessageToOtherClient(operation.m_user, operation2);
						}
						else
						{
							Operation operation2 = new Operation();
							operation.m_operationName = "sendFileRsp";
							operation.m_user = m_contaContactWindow.m_user;
							int ret = JOptionPane.showConfirmDialog(null, operation.m_user + "want to send file " + operation.m_fileName + ", are you sure to receive", "tips", JOptionPane.YES_NO_OPTION);
				            if(ret == 0)
				            {
				            	m_contaContactWindow.m_fileTransState = true;
				            	operation.m_msg = "agreeFileTrans";
				            	m_receiveFileName = operation.m_fileName;
				            }
				            else
				            {
				            	operation.m_msg = m_contaContactWindow.m_user + " refused to accept the file";
				            }
							SendMessageToOtherClient(operation.m_user, operation2);
						}
					}
					else if(operation.m_operationName.equals("sendFileRsp"))
					{
						if(operation.m_msg.equals("agreeFileTrans"))
						{
							
						}
						else
						{
							JOptionPane.showConfirmDialog(null, operation.m_msg, "tips", JOptionPane.INFORMATION_MESSAGE);
						}
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
