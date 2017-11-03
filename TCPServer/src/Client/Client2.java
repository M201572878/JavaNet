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

//�ͻ���
public class Client2 {

	private Socket socket;
	
	public Client2(){		
			try {
				socket = new Socket("localhost", 8088);			
			} catch (Exception e) {			
				e.printStackTrace();
			}	
	}
//	�ҵ����������ˣ��ڸ��෢��ǰʹ�� ObjectOutputStream �� reset ���� ���� ʹ��writeUnshared ���� writeObject���Ϳ�����ÿ�� write �Ķ�����һ���������󡣣�����JDK���ò���ϸ��֮ǰ�����Ҳ���Խ��������������������ͱȽ��鷳�ˣ�
	public void start(){
		
		try{
			OutputStream out = socket.getOutputStream();
			//OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
			ObjectOutputStream osw = new ObjectOutputStream(out);
//			PrintWriter pw = new PrintWriter(osw, true);
			
			InputStream in = socket.getInputStream();
			InputStreamReader isw = new InputStreamReader(in, "UTF-8");
			BufferedReader  br =  new BufferedReader(isw);
			//����Scanner��ȡ�û���������
			Scanner scanner = new Scanner(System.in);
			boolean runContinue = true;
			Operation operationObject = new Operation();
			while(runContinue){
				System.out.println("ѡ��������ͣ�1.ע�� 2.��½ 3.�˳�4.�����һ�");
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
					System.out.println("�����û�������������,�����ܱ�");
					operationObject.m_operationName = "register";
					operationObject.m_user = scanner.nextLine();
					firstPwdInput = scanner.nextLine();
					secondPwdInput = scanner.nextLine();
					firstQuessionAnswer = scanner.nextLine();
					secondQuessionAnswer = scanner.nextLine();
					if(firstPwdInput.equals(secondPwdInput) && firstQuessionAnswer.equals(secondQuessionAnswer))
					{
						operationObject.m_password = firstPwdInput;
						//���͸�������
						osw.writeUnshared(operationObject);
						//osw.writeObject(null);
						//���ܷ������Ļظ�
						serverResp = br.readLine();
						System.out.println(serverResp);
						
					}
					else
					{
						System.out.println("�������벻һ��");
					}
					break;
				case 2:
					System.out.println("�����û���������");
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
					System.out.println("�����û������ܱ�����Ĵ�");
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