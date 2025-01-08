package Messages.Requests;

import Messages.OperationType;

public class LoginRequest extends Request
{
	private String username;
	private String password;

	//Needed 4 correct gson deserialization (when using gson.fromJson(..., ...) this constructor gets used, not the one below).
	private LoginRequest()
	{
		super(OperationType.LOGIN);
	}

	public LoginRequest(String username, String password)
	{
		this();
		this.username = username;
		this.password = password;
	}

	public String GetUsername()
	{
		return username;
	}

	public String GetPassword()
	{
		return password;
	}

	@Override
	public String toString()
	{
		return "Messages.Requests.LoginRequest{" +
				"type=" + type + '\'' +
				", username='" + username + '\'' +
				", password='" + password + '\'' +
				'}';
	}
}
