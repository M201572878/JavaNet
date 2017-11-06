package Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

import javax.swing.JOptionPane;

import TCP.Operation;

public class ClientSocket {
	public Socket m_SocketToServer = null;
	public ServerSocket m_serverSocket = null;
	public ObjectOutputStream m_objOutputStream = null;
	public ObjectInputStream m_objInputStream = null;
	public Operation m_operationObj = null;
	public HashMap m_otherClientSocketMap = new HashMap<String, OtherClientSocketInfo>();
	public ClientSocket() {
		try {
			m_SocketToServer = new Socket("localhost", 8088);
			m_objOutputStream = new ObjectOutputStream(m_SocketToServer.getOutputStream());
			m_objInputStream = new ObjectInputStream(m_SocketToServer.getInputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
//			JOptionPane.showMessageDialog(null, "hello.");
//			System.exit(0);
		}
		
		//接收服务器消息的线程
		ReceiveServerThread receiveServerThread = new ReceiveServerThread();
		receiveServerThread.start();
		
		//监听其他客户端连接的线程
		ListenOtherClientThread listenOtherClientThread = new ListenOtherClientThread();
		listenOtherClientThread.start();
		
	}
	
	public void SendToServer(Operation operation)
	{
		try {
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
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
