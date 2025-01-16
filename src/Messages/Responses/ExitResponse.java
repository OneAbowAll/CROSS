package Messages.Responses;

import Messages.OperationType;

public class ExitResponse extends OperationResponse
{
	private ExitResponse()
	{
		super(OperationType.EXIT, -1, "");
	}

	public ExitResponse(int response)
	{
		this();
		this.response = response;
		this.errorMessage = ResponseInfo.GetMeaning(type, response);
	}
}
