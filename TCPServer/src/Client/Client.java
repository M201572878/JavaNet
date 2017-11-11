package Client;

import java.io.IOException;

public class Client {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		try {
//			java.awt.Desktop.getDesktop().open(new java.io.File("c://"));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		ClientSocket clientSocket = new ClientSocket();
		clientSocket.Init();
		LoginWindow clientWindow = new LoginWindow(clientSocket); 
	}

}
