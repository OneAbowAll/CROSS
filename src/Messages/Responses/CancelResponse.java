package Messages.Responses;

import Messages.OperationType;

public class CancelResponse extends OperationResponse
{
	private CancelResponse() { super(OperationType.CANCEL_ORDER, -1, ""); }

	public CancelResponse(int response)
	{
		this();
		this.response = response;
		this.errorMessage = ResponseInfo.GetMeaning(type, response);
	}
}
