public class LoginRequest extends Request
{
	String username;
	String password;

	//Needed 4 correct gson deserialization (when using gson.fromJson(..., ...) this constructor gets used, not the one below).
	public LoginRequest()
	{
		super(RequestType.LOGIN);
	}

	public LoginRequest(String username, String password)
	{
		this();
		this.username = username;
		this.password = password;
	}
}
