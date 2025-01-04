package Messages.Responses;

import Messages.OperationType;

public class RegisterResponse extends OperationResponse
{
    private RegisterResponse()
    {
        super(OperationType.REGISTER, -1, "");
    }

    public RegisterResponse(int response)
    {
        this();
        this.response = response;
        this.errorMessage = ResponseInfo.GetMeaning(type, response);
    }
}
