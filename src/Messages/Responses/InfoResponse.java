package Messages.Responses;

import Messages.OperationType;

public class InfoResponse  extends OperationResponse
{
	private InfoResponse()
	{
		super(OperationType.INFO, -1, "");
	}

	public InfoResponse(int response)
	{
		this();
		this.response = response;
		this.errorMessage = ResponseInfo.GetMeaning(type, response);
	}
}
