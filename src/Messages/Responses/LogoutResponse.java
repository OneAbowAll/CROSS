package Messages.Responses;

import Messages.OperationType;

public class LogoutResponse extends OperationResponse
{
	private LogoutResponse()
	{
		super(OperationType.LOGOUT, -1, "");
	}

	public LogoutResponse(int response)
	{
		this();
		this.response = response;
		this.errorMessage = ResponseInfo.GetMeaning(type, response);
	}
}
