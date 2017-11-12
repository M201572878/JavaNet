package TCP;

import TCP.Operation;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.swing.SwingUtilities;

import Client.MD5tools;

//������ ����
public class MultiTreadServer {
	public static String m_serverSocketAddress = null;
	public static int m_serverSocketPort = 0;
	public static void main(String[] args) {
		
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(8088);
			m_serverSocketAddress = InetAddress.getLocalHost().getHostAddress();//serverSocket.getInetAddress().toString();
			m_serverSocketPort = serverSocket.getLocalPort();
			SwingUtilities.invokeLater(new Runnable() {
	            @Override
	            public void run() {
	            	ServerWindow frame = new ServerWindow();
	            	frame.setTitle("Server");
					frame.setVisible(true);
					ServerWindow.m_serverIPTextArea.setText("IP: " + m_serverSocketAddress+"\n");
					ServerWindow.m_serverIPTextArea.append("port: "+ m_serverSocketPort);
	            }
	        });
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Socket socket = null;
		ServerThread.m_userInfoMap.put("1", new UserInfo("1", MD5tools.makeCipherText("1"), "1"));
		ServerThread.m_userInfoMap.put("2", new UserInfo("2", MD5tools.makeCipherText("2"), "2"));
		ServerThread.m_userInfoMap.put("3", new UserInfo("3", MD5tools.makeCipherText("3"), "3"));
		ServerThread.m_userInfoMap.put("4", new UserInfo("4", MD5tools.makeCipherText("4"), "4"));
		ShowAllUser();
		while(true)
		{
			try {
				socket = serverSocket.accept();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ServerThread thread = new ServerThread(socket);
			thread.start();
			System.out.println("�ȴ���һ�����ӡ�����");
		}
	}
	
	public static void ShowAllUser()
	{
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	
            	Set<String> allUserSet = ServerThread.m_userInfoMap.keySet();
           	ServerWindow.m_allUserListTextArea.setText(allUserSet.toString());
            }
        });
		
	}
	

}



