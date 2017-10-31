package TCP;

import java.io.Serializable;

public class UserInfo implements Serializable{
	public String m_userName = null;
	public String m_password = null;
	public String m_quessionAnswer = null;
	UserInfo(String userName, String password, String quessionAnswer)
	{
		m_userName = userName;
		m_password = password;
		m_quessionAnswer = quessionAnswer;
	}
}
