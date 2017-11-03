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

//�ͻ���
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
//	�ҵ����������ˣ��ڸ��෢��ǰʹ�� ObjectOutputStream �� reset ���� ���� ʹ��writeUnshared ���� writeObject���Ϳ�����ÿ�� write �Ķ�����һ���������󡣣�����JDK���ò���ϸ��֮ǰ�����Ҳ���Խ��������������������ͱȽ��鷳�ˣ�
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
			//����Scanner��ȡ�û���������
			Scanner scanner = new Scanner(System.in);
			boolean runContinue = true;
			Operation operationObject = new Operation();
			ServerReply serverReplyObject = new ServerReply();
			String currentLoginUser = null;
			while(runContinue){
				System.out.println("ѡ��������ͣ�1.ע�� 2.��½ 3.�˳�4.�����һ�5.������Ϣ");
				int operationType = scanner.nextInt();
				scanner.nextLine();
				UserInfo userInfo;
				String serverResp = null;
				switch(operationType)
				{
				case 1:
					System.out.println("�����û���,�����ܱ�����Ĵ𰸣��������õ�����");
					operationObject.m_operationName = "register";
					userInfo = GetUserInfo();
					if(null != userInfo)
					{
						operationObject.m_userInfo = userInfo;
						operationObject.m_user = userInfo.m_userName;
						//���͸�������
						osw.writeUnshared(operationObject);
						//osw.writeObject(null);
						//���ܷ������Ļظ�
//						serverResp = br.readLine();
						serverReplyObject = (ServerReply) isw.readObject();
						System.out.println(serverReplyObject.m_responseStr);
					}
					else
					{
						System.out.println("���벻һ��");
					}
					break;
				case 2:
					System.out.println("�����û���������");
					operationObject.m_operationName = "login";
					operationObject.m_user = scanner.nextLine();
					operationObject.m_password = scanner.nextLine();
					osw.writeUnshared(operationObject);
//					serverResp = br.readLine();
//					System.out.println(serverResp);
					serverReplyObject = (ServerReply) isw.readObject();
					System.out.println(serverReplyObject.m_responseStr);
					if(serverReplyObject.m_responseStr.equals("��½�ɹ�"))
					{
						currentLoginUser = operationObject.m_user;
					}
					inStr.readUTF();
//					���ﲻ��д��emmmm
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
					System.out.println("�����û���,�����ܱ�����Ĵ𰸣��������õ�����");
					operationObject.m_operationName = "findpassword";
					userInfo = GetUserInfo();
					if(null != userInfo)
					{
						operationObject.m_userInfo = userInfo;
						operationObject.m_user = userInfo.m_userName;
						//���͸�������
						osw.writeUnshared(operationObject);
						//osw.writeObject(null);
						//���ܷ������Ļظ�
//						serverResp = br.readLine();
//						System.out.println(serverResp);
						serverReplyObject = (ServerReply) isw.readObject();
						System.out.println(serverReplyObject.m_responseStr);
					}
					else
					{
						System.out.println("���벻һ��");
					}
					break;
				case 5:
				{
					System.out.println("�������뷢����Ϣ���û���");
					operationObject.m_wantSendUser = scanner.nextLine();
					osw.writeUnshared(operationObject);
					serverReplyObject = (ServerReply) isw.readObject();
					System.out.println(serverReplyObject.m_responseStr);
					if(serverReplyObject.m_wantSendUserRegistered = false ){
						;
//						�û�û��ע��Ϳ����
					}
					else if(serverReplyObject.m_wantSendUserState = false ){
						operationObject.m_wantSendMessage = scanner.nextLine();
						osw.writeUnshared(operationObject);
					}
					else {
//						������Ϣ����
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