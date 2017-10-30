package Client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import TCP.Operation;

//客户端
public class Client2 {

	private Socket socket;
	
	public Client2(){		
			try {
				socket = new Socket("localhost", 8088);			
			} catch (Exception e) {			
				e.printStackTrace();
			}	
	}
//	找到问题所在了，在父类发送前使用 ObjectOutputStream 的 reset 方法 或者 使用writeUnshared 代替 writeObject，就可以让每次 write 的对象都是一个单独对象。（怪我JDK看得不仔细！之前的深拷贝也可以解决，不过跟这个比起来就比较麻烦了）
	public void start(){
		
		try{
			OutputStream out = socket.getOutputStream();
			//OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
			ObjectOutputStream osw = new ObjectOutputStream(out);
			PrintWriter pw = new PrintWriter(osw, true);
			
			InputStream in = socket.getInputStream();
			InputStreamReader isw = new InputStreamReader(in, "UTF-8");
			BufferedReader  br =  new BufferedReader(isw);
			//创建Scanner读取用户输入内容
			Scanner scanner = new Scanner(System.in);
			boolean runContinue = true;
			Operation operationObject = new Operation();
			while(runContinue){
				System.out.println("选择操作类型：1.注册 2.登陆 3.退出4.密码找回");
				int operationType = scanner.nextInt();
				scanner.nextLine();
				String firstPwdInput = null;
				String secondPwdInput = null;
				String firstQuessionAnswer = null;
				String secondQuessionAnswer = null;
				String serverResp = null;
				switch(operationType)
				{
				case 1:
					System.out.println("输入用户名和两次密码,两次密保");
					operationObject.m_operationName = "register";
					operationObject.m_user = scanner.nextLine();
					firstPwdInput = scanner.nextLine();
					secondPwdInput = scanner.nextLine();
					firstQuessionAnswer = scanner.nextLine();
					secondQuessionAnswer = scanner.nextLine();
					if(firstPwdInput.equals(secondPwdInput) && firstQuessionAnswer.equals(secondQuessionAnswer))
					{
						operationObject.m_password = firstPwdInput;
						//发送给服务器
						osw.writeUnshared(operationObject);
						//osw.writeObject(null);
						//接受服务器的回复
						serverResp = br.readLine();
						System.out.println(serverResp);
						
					}
					else
					{
						System.out.println("二次密码不一致");
					}
					break;
				case 2:
					System.out.println("输入用户名和密码");
					operationObject.m_operationName = "login";
					operationObject.m_user = scanner.nextLine();
					operationObject.m_password = scanner.nextLine();
					osw.writeUnshared(operationObject);
					serverResp = br.readLine();
					System.out.println(serverResp);
					break;
				case 3:
					runContinue = false;
					break;
				case 4:
					System.out.println("输入用户名和密保问题的答案");
					operationObject.m_operationName = "findpassword";
					operationObject.m_user = scanner.nextLine();
					//operationObject.m_password = scanner.nextLine();
					osw.writeUnshared(operationObject);
					serverResp = br.readLine();
					System.out.println(serverResp);
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
		Client2 client = new Client2();
		client.start();
	}

}