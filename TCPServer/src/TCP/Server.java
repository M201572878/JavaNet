package TCP;

import TCP.Operation;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//服务器 运行
public class Server {

	private ServerSocket serverSocket;
	
	public Server(){
		try{
			serverSocket = new ServerSocket(8088);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void start(){
		try{
			System.out.println("等待客户端连接。。。");
			//方法会产生阻塞，直到某个Socket连接，返回请求连接的Socket
			Socket socket = serverSocket.accept();
			System.out.println("客户端已连接！");
			InputStream in = socket.getInputStream();
			ObjectInputStream isr = new ObjectInputStream(in);
//			BufferedReader br = new BufferedReader(isr);
			
			OutputStream out = socket.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
			BufferedWriter  pw = new BufferedWriter (osw);
			//System.out.println("客户端说：" + br.readLine());
			//不断读取客户端数据
			HashMap userMap = new HashMap();
			int index = 0;
			while(true){
				//System.out.println("客户端说：" + br.readLine());
				Operation operationObj = null;
				try
				{
					operationObj = (Operation) isr.readObject();
				}
				catch(Exception ex)
                {
                    //ex.printStackTrace();
                    socket.close();
                    break;
                }
				System.out.println(operationObj.m_operationName);
				index++;
				if(operationObj.m_operationName.equals("register"))
				{
					System.out.println("register回复");
					if(userMap.containsKey(operationObj.m_user))
					{
						System.out.println("register回复");
						pw.write("注册失败，已存在的账号");
						pw.newLine();
						pw.flush();
					}
					else
					{
						System.out.println("register回复2");
						userMap.put(operationObj.m_user, operationObj.m_password);
						pw.write("注册成功，请登陆");
						pw.newLine();
						pw.flush();
					}
				}
				else if(operationObj.m_operationName.equals("login"))
				{
					System.out.println("login回复");
					if(userMap.containsKey(operationObj.m_user) &&
							userMap.get(operationObj.m_user).equals(operationObj.m_password))
					{
						pw.write("登陆成功");
						pw.newLine();
						pw.flush();
					}
					else
					{
						pw.write("错误的用户名或密码");
						pw.newLine();
						pw.flush();
					}
				}
				else if(operationObj.m_operationName.equals("findpassword"))
				{
					System.out.println("findpassword回复");
					if(userMap.containsKey(operationObj.m_user)){
					pw.write("密码找回成功，为了您的账户安全，请重新设置密码");
					pw.newLine();
					pw.flush();
					}
					else
					{
						pw.write("错误的用户名，密码找回失败");
						pw.newLine();
						pw.flush();
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Server server = new Server();
		server.start();
	}

}

