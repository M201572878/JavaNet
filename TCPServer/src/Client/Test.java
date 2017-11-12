package Client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class Test {
	public static InetAddress getLocalHostLANAddress() throws Exception {
	    try {
	        InetAddress candidateAddress = null;
	        // �������е�����ӿ�
	        for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements(); ) {
	            NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
	            System.out.println(iface.getName());
	            if(!iface.getName().equals("eth0"))
	            {
	            	continue;
	            }
	            // �����еĽӿ����ٱ���IP
	            for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
	                InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
	                if (!inetAddr.isLoopbackAddress()) {// �ų�loopback���͵�ַ
	                    if (inetAddr.isSiteLocalAddress()) {
	                        // �����site-local��ַ����������
	                        return inetAddr;
	                    } else if (candidateAddress == null) {
	                        // site-local���͵ĵ�ַδ�����֣��ȼ�¼��ѡ��ַ
	                        candidateAddress = inetAddr;
	                    }
	                }
	            }
	        }
	        if (candidateAddress != null) {
	            return candidateAddress;
	        }
	        // ���û�з��� non-loopback��ַ.ֻ�������ѡ�ķ���
	        InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
	        return jdkSuppliedAddress;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null;
	}

	public static void main(String[] args) throws Exception {
		try {
			System.out.println("������IP = " + Test.getLocalHostLANAddress().getHostAddress());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
//		FileOutputStream fos = null;
//		try {
//			fos = new FileOutputStream("./tmp/teset.dd");
//			fos.write(new Byte((byte) 1111));
//	        fos.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

}
