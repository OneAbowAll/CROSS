package Messages.Responses;

import Messages.OperationType;

public class LoginResponse extends OperationResponse
{
    private LoginResponse()
    {
        super(OperationType.LOGIN, -1, "");
    }

    public LoginResponse(int response)
    {
        this();
        this.response = response;
        this.errorMessage = ResponseInfo.GetMeaning(type, response);
    }
}
