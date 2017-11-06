package TCP;
import java.io.Serializable;

public class Operation implements Serializable {
	public String m_operationName;
	public String m_user;
	public String m_password;
	public String m_quessionAnswer = null;
	public UserInfo m_userInfo = null;
	public String m_detail = null;
	public String m_ip =null;
	public String m_dstUser = null;
	public String m_msg = null;
	public String m_users = null;
	public String m_userStates = null;
	public String m_udpIp = null;
	public int m_udpPort = 0;
	public int m_port;
}
