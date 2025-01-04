package Messages.Responses;

import Messages.OperationType;

public class UpdateCredentialsResponse extends OperationResponse
{
    public UpdateCredentialsResponse()
    {
        super(OperationType.UPDATE_CREDENTIALS, -1, "");
    }

    public UpdateCredentialsResponse(int response)
    {
        this();
        this.response = response;
        this.errorMessage = ResponseInfo.GetMeaning(type, response);
    }
}