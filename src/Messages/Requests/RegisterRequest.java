package Messages.Requests;

import Messages.OperationType;

public class RegisterRequest extends Request
{
	String username;
	String password;

	//Needed 4 correct gson deserialization (when using gson.fromJson(..., ...) this constructor gets used, not the one below).
	public RegisterRequest()
	{
		super(OperationType.REGISTER);
	}

	public RegisterRequest(String username, String password)
	{
		this();
		this.username = username;
		this.password = password;
	}

	@Override
	public String toString()
	{
		return "RegisterRequest{" +
				"username='" + username + '\'' +
				", password='" + password + '\'' +
				'}';
	}
}
