package TCP;
import java.io.Serializable;

public class Operation implements Serializable {
	public String m_operationName = null;
	public String m_user = null;
	public String m_password = null;
	public String m_quessionAnswer = null;
	public String m_wantSendUser = null;
	public String m_wantSendMessage = null;
	public String m_MessageFrom = null;
//	public Boolean m_wantSendUserState = false;
	public UserInfo m_userInfo = null;
}
