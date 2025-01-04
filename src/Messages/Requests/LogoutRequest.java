package Messages.Requests;

import Messages.OperationType;

public class LogoutRequest extends Request
{
	//Needed 4 correct gson deserialization (when using gson.fromJson(..., ...) this constructor gets used, not the one below).
	public LogoutRequest()
	{
		super(OperationType.LOGOUT);
	}
}
