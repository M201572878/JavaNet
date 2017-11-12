package Client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
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
	public ArrayList m_sendSuccessFileIndex = new ArrayList<Integer>();
	FileServerThread m_fileServerThread = null;
	public static final int DATA_SIZE = 1024 * 50;
	public static final int SEND_SIZE = DATA_SIZE + 4;
	public long m_startSendTime = 0;
	public long m_finishSendTime = 0;
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
			System.exit(0);
			return false;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "连接失败", "tips", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
			e.printStackTrace();
			return false;
		}finally {
//			JOptionPane.showMessageDialog(null, "hello.");
//			System.exit(0);
		}
		
		//接收文件的线程
		m_fileServerThread = new FileServerThread();
		m_fileServerThread.start();
		
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
		if(m_otherClientSocketMap.containsKey(otherCientUserName))
		{
			return;
		}
		
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
	
	//文件接收
	private class FileServerThread extends Thread{
		String m_fileSender = null;
		
		public void setM_fileSender(String m_fileSender) {
			this.m_fileSender = m_fileSender;
		}
		
		@Override
		public void run(){
			while(true)
			{
				byte[] buf=new byte[SEND_SIZE];
		        DatagramPacket packet = new DatagramPacket(buf, buf.length);
//		        System.out.println("I am waiting.");
		        try {
					m_fileServer.receive(packet);
					FileSplitSaveThread fileSplitSaveThread = new FileSplitSaveThread(packet);
					fileSplitSaveThread.start();
					
					int fileIndex = bytesToInt(packet.getData(), 0);
					Operation operation = new Operation();
					operation.m_operationName = "confirmFileSplit";
					operation.m_fileIndex = fileIndex;
					SendMessageToOtherClient(m_fileSender, operation);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	//文件保存线程
	private class FileSplitSaveThread extends Thread{
		DatagramPacket m_packet = null;
		FileSplitSaveThread(DatagramPacket packet){
			m_packet = packet;
		}
		
		@Override
		public void run(){
			byte[] data = m_packet.getData();
			int length = m_packet.getLength();
			int fileIndex = bytesToInt(data, 0);
			String strPath = "./temp/"+m_receiveFileName + Integer.toString(fileIndex);
			File file = new File(strPath);  
			if(!file.getParentFile().exists()){  
				file.getParentFile().mkdirs(); 
			} 
			FileOutputStream fos = null;
			try {
				file.createNewFile();
				fos = new FileOutputStream(file);
				fos.write(data, 4, length - 4);
		        fos.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
	   
	public byte[] intToBytes( int value )   
	{   
	    byte[] src = new byte[4];  
	    src[3] =  (byte) ((value>>24) & 0xFF);  
	    src[2] =  (byte) ((value>>16) & 0xFF);  
	    src[1] =  (byte) ((value>>8) & 0xFF);    
	    src[0] =  (byte) (value & 0xFF);                  
	    return src;   
	}   
	
	public int bytesToInt(byte[] src, int offset) {  
	    int value;    
	    value = (int) ((src[offset] & 0xFF)   
	            | ((src[offset+1] & 0xFF)<<8)   
	            | ((src[offset+2] & 0xFF)<<16)   
	            | ((src[offset+3] & 0xFF)<<24));  
	    return value;  
	} 
	
	//发送文件的线程
	private class SendFileThread extends Thread{
		String m_targeIp = null;
		int m_targetPort;
		
		String m_fileReceiver = null;
		SendFileThread(String ip, int port, String fileReceiver)
		{
			m_targeIp = ip;
			m_targetPort = port;
			m_fileReceiver = fileReceiver;
		}
		@Override
		public void run(){
			RandomAccessFile  randomAccessFile = null;
			DatagramSocket sendSocket = null;
			System.out.println(m_targeIp);
			System.out.println(m_targetPort);
			InetSocketAddress inetSocketAddress = new InetSocketAddress(m_targeIp,  m_targetPort);
			try {
				randomAccessFile = new RandomAccessFile (m_sendFile, "r");
				sendSocket = new DatagramSocket();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			int fileIndex = 0;
			byte[] tempbytes = new byte[SEND_SIZE];
			System.out.println("attemp send file");
			int maxIndex = (int) (m_sendFile.length() / DATA_SIZE);
			if(m_sendFile.length() % DATA_SIZE != 0)
			{
				maxIndex++;
			}
			System.out.println(maxIndex);
			m_startSendTime = System.currentTimeMillis();
			while(true)
			{
				try {
					sleep(1);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					if(m_sendSuccessFileIndex.size() < maxIndex)
					{
						for(int i = fileIndex; i < maxIndex; ++i)
						{
							if(!m_sendSuccessFileIndex.contains(i))
							{
								fileIndex = i;
								break;
							}
						}
						byte[] indexBytes = intToBytes(fileIndex);
						randomAccessFile.seek(fileIndex * DATA_SIZE);
						int byteRead = randomAccessFile.read(tempbytes, 4, DATA_SIZE);
				        System.arraycopy(indexBytes, 0, tempbytes, 0, indexBytes.length); 
						DatagramPacket sendPacket =new DatagramPacket(tempbytes, byteRead + 4, inetSocketAddress);
		                sendSocket.send(sendPacket);
		                fileIndex++;
		                if(fileIndex == maxIndex)
		                {
		                	fileIndex = 0;
		                }
					}
					else
					{
						m_finishSendTime = System.currentTimeMillis();
						System.out.println(m_sendSuccessFileIndex.size());
						m_contaContactWindow.m_fileTransState = false;
						Operation operation = new Operation();
						operation.m_fileIndex = maxIndex;
						operation.m_operationName = "sendFileFinish";
						long transSecond = ((m_finishSendTime - m_startSendTime) / 1000);
						if(transSecond == 0)
						{
							transSecond = 1;
						}
						operation.m_transSpeed = (int) (m_sendFile.length() / transSecond);
						SendMessageToOtherClient(m_fileReceiver, operation);
						DecimalFormat df = new DecimalFormat("#,###");
						JOptionPane.showConfirmDialog(null, "file send finish, avg speed is " + df.format(operation.m_transSpeed) + "B/S", "tips", JOptionPane.INFORMATION_MESSAGE);
						m_sendSuccessFileIndex.clear();
						break;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
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
//					System.out.println("from other client:"+operation.m_operationName + operation.m_msg);
					if(operation.m_operationName.equals("onlineChatWithOtherClient"))
					{
						m_contaContactWindow.AddMsg(operation.m_user, operation.m_msg);
					}
					else if(operation.m_operationName.equals("sendFileReq"))
					{
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
							operation2.m_operationName = "sendFileRsp";
							operation2.m_user = m_contaContactWindow.m_user;
							int ret = JOptionPane.showConfirmDialog(null, operation.m_user + "want to send file " + operation.m_fileName + ", are you sure to receive", "tips", JOptionPane.YES_NO_OPTION);
				            ConnectToOtherClient(operation.m_user, m_otherClientSocket.getInetAddress().getHostAddress(), operation.m_port);
							if(ret == 0)
				            {
				            	m_contaContactWindow.m_fileTransState = true;
				            	m_fileServerThread.setM_fileSender(operation.m_user);
				            	m_receiveFileName = operation.m_fileName;
				            	operation2.m_msg = "agreeFileTrans";
				            	operation2.m_udpPort = m_fileServer.getLocalPort();
				            }
				            else
				            {
				            	operation2.m_msg = m_contaContactWindow.m_user + " refused to accept the file";
				            }
				            System.out.println(operation.m_user);
							SendMessageToOtherClient(operation.m_user, operation2);
						}
					}
					else if(operation.m_operationName.equals("sendFileRsp"))
					{
						if(operation.m_msg.equals("agreeFileTrans"))
						{
							SendFileThread sendFileThread = new SendFileThread(m_otherClientSocket.getInetAddress().getHostAddress(), operation.m_udpPort, operation.m_user);
							sendFileThread.start();
						}
						else
						{
							JOptionPane.showConfirmDialog(null, operation.m_msg, "tips", JOptionPane.INFORMATION_MESSAGE);
						}
					}
					//对方确认收到某一部分数据
					else if(operation.m_operationName.equals("confirmFileSplit"))
					{
						if(!m_sendSuccessFileIndex.contains(operation.m_fileIndex))
						{
							m_sendSuccessFileIndex.add(operation.m_fileIndex);
						}
					}
					//对方发送文件完毕
					else if(operation.m_operationName.equals("sendFileFinish"))
					{
						System.out.println("sendFileFinish++++++");
						m_contaContactWindow.m_fileTransState = false;
						String receivePath = "./download/" + m_receiveFileName;
						File file = new File(receivePath);  
						if(!file.getParentFile().exists()){  
							file.getParentFile().mkdirs(); 
						} 
						FileOutputStream fileOutputStream = new FileOutputStream(receivePath);
						byte[] data = new byte[DATA_SIZE];
						for(int i = 0 ; i < operation.m_fileIndex; ++i)
						{
							String strPath = "./temp/"+m_receiveFileName + Integer.toString(i);
							System.out.println(strPath);
							FileInputStream fileInputStream = new FileInputStream(strPath);
							while(true)
							{
								int ret = fileInputStream.read(data, 0, DATA_SIZE);
								if(ret != -1)
								{
									fileOutputStream.write(data, 0, ret);
								}
								else
								{
									break;
								}
							}
							fileInputStream.close();
						}
						fileOutputStream.close();
						DecimalFormat df = new DecimalFormat("#,###");
						JOptionPane.showConfirmDialog(null, "file receive finish, avg speed is " + df.format(operation.m_transSpeed) + "B/S", "tips", JOptionPane.INFORMATION_MESSAGE);
						//m_sendSuccessFileIndex.clear();
						deleteAll(new File("./temp"));
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void deleteAll(File file)
	 {
	   if(file.isFile() || file.list().length ==0)
	   {
		   file.delete();     
	   }
	   else
	   {    
		   File[] files = file.listFiles();
		   for (int i = 0; i < files.length; i++) 
		   {
	    	 deleteAll(files[i]);
	    	 files[i].delete();    
		   }
		   if(file.exists())
	    	  file.delete();
	   }
	 }
}
