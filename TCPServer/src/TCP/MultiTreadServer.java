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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//服务器 运行
public class MultiTreadServer {
	
	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(8088);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Socket socket = null;
		ServerThread.m_userInfoMap.put("1", new UserInfo("1", "1", "1"));
		ServerThread.m_userInfoMap.put("2", new UserInfo("2", "2", "2"));
		ServerThread.m_userInfoMap.put("3", new UserInfo("3", "3", "3"));
		ServerThread.m_userInfoMap.put("4", new UserInfo("4", "4", "4"));
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
			System.out.println("等待下一个连接。。。");
		}
	}

}



