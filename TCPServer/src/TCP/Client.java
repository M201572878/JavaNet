package TCP;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import TCP.Operation;

//客户端
public class Client {

	private Socket socket;
	
	public Client(){		
			try {
				socket = new Socket("localhost", 8088);			
			} catch (Exception e) {			
				e.printStackTrace();
			}	
	}
	public UserInfo GetUserInfo()
	{
		Scanner scanner = new Scanner(System.in);
		String userName = scanner.nextLine();
		String firstPwdInput = scanner.nextLine();
		String secondPwdInput = scanner.nextLine();
		String firstQuessionAnswer = scanner.nextLine();
		String secondQuessionAnswer = scanner.nextLine();
		if(!firstPwdInput.equals(secondPwdInput) || !firstQuessionAnswer.equals(secondQuessionAnswer))
		{
			return null;
		}
		UserInfo userInfo = new UserInfo(userName, firstPwdInput, firstQuessionAnswer);
		return userInfo;
	}
//	找到问题所在了，在父类发送前使用 ObjectOutputStream 的 reset 方法 或者 使用writeUnshared 代替 writeObject，就可以让每次 write 的对象都是一个单独对象。（怪我JDK看得不仔细！之前的深拷贝也可以解决，不过跟这个比起来就比较麻烦了）
	public void start(){
		
		try{
			OutputStream out = socket.getOutputStream();
			//OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
			ObjectOutputStream osw = new ObjectOutputStream(out);
//			PrintWriter pw = new PrintWriter(osw, true);
			
			InputStream in = socket.getInputStream();
//			InputStreamReader isw = new InputStreamReader(in, "UTF-8");
//			BufferedReader  br =  new BufferedReader(isw);
			ObjectInputStream isw = new ObjectInputStream(in);
			DataInputStream inStr = new DataInputStream(in);
			//创建Scanner读取用户输入内容
			Scanner scanner = new Scanner(System.in);
			boolean runContinue = true;
			Operation operationObject = new Operation();
			ServerReply serverReplyObject = new ServerReply();
			String currentLoginUser = null;
			while(runContinue){
				System.out.println("选择操作类型：1.注册 2.登陆 3.退出4.密码找回5.发送消息");
				int operationType = scanner.nextInt();
				scanner.nextLine();
				UserInfo userInfo;
				String serverResp = null;
				switch(operationType)
				{
				case 1:
					System.out.println("输入用户名,两次密保问题的答案，两次设置的密码");
					operationObject.m_operationName = "register";
					userInfo = GetUserInfo();
					if(null != userInfo)
					{
						operationObject.m_userInfo = userInfo;
						operationObject.m_user = userInfo.m_userName;
						//发送给服务器
						osw.writeUnshared(operationObject);
						//osw.writeObject(null);
						//接受服务器的回复
//						serverResp = br.readLine();
						serverReplyObject = (ServerReply) isw.readObject();
						System.out.println(serverReplyObject.m_responseStr);
					}
					else
					{
						System.out.println("输入不一致");
					}
					break;
				case 2:
					System.out.println("输入用户名和密码");
					operationObject.m_operationName = "login";
					operationObject.m_user = scanner.nextLine();
					operationObject.m_password = scanner.nextLine();
					osw.writeUnshared(operationObject);
//					serverResp = br.readLine();
//					System.out.println(serverResp);
					serverReplyObject = (ServerReply) isw.readObject();
					System.out.println(serverReplyObject.m_responseStr);
					if(serverReplyObject.m_responseStr.equals("登陆成功"))
					{
						currentLoginUser = operationObject.m_user;
					}
					inStr.readUTF();
//					这里不会写了emmmm
					break;
				case 3:
					operationObject.m_operationName = "logoff";
					if(currentLoginUser != null)
					{
						operationObject.m_user = currentLoginUser;
						
					}
					osw.writeUnshared(operationObject);
					runContinue = false;
					break;
				case 4:
					System.out.println("输入用户名,两次密保问题的答案，两次设置的密码");
					operationObject.m_operationName = "findpassword";
					userInfo = GetUserInfo();
					if(null != userInfo)
					{
						operationObject.m_userInfo = userInfo;
						operationObject.m_user = userInfo.m_userName;
						//发送给服务器
						osw.writeUnshared(operationObject);
						//osw.writeObject(null);
						//接受服务器的回复
//						serverResp = br.readLine();
//						System.out.println(serverResp);
						serverReplyObject = (ServerReply) isw.readObject();
						System.out.println(serverReplyObject.m_responseStr);
					}
					else
					{
						System.out.println("输入不一致");
					}
					break;
				case 5:
				{
					System.out.println("请输入想发送消息的用户名");
					operationObject.m_wantSendUser = scanner.nextLine();
					osw.writeUnshared(operationObject);
					serverReplyObject = (ServerReply) isw.readObject();
					System.out.println(serverReplyObject.m_responseStr);
					if(serverReplyObject.m_wantSendUserRegistered = false ){
						;
//						用户没有注册就空语句
					}
					else if(serverReplyObject.m_wantSendUserState = false ){
						operationObject.m_wantSendMessage = scanner.nextLine();
						osw.writeUnshared(operationObject);
					}
					else {
//						在线消息处理
					}

					
				}
				default:
					break;
				}
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(socket != null){
				try{
					socket.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}				
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Client client = new Client();
		client.start();
	}

}