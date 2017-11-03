package TCP;
import java.io.Serializable;

public class ServerReply implements Serializable{
	public String m_responseStr = null;
	public boolean m_wantSendUserRegistered = false;
	public boolean m_wantSendUserState = false;
	public String m_MessageFromWho = null;

}
