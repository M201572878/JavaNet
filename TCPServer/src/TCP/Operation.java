package TCP;
import java.io.Serializable;

public class Operation implements Serializable {
	public String m_operationName;
	public String m_user;
	public String m_password;
	public String m_quessionAnswer = null;
	public UserInfo m_userInfo = null;
}
