package Messages.Responses;

import Messages.CrossMessage;
import Messages.OperationType;

public abstract class OperationResponse extends CrossMessage
{
    protected int response;
    protected String errorMessage;

    protected OperationResponse(OperationType type, int response, String errorMessage)
    {
        super(type);
        this.response = response;
        this.errorMessage = errorMessage;
    }

    public String GetErrorMessage()
    {
        return errorMessage;
    }

    @Override
    public String toString()
    {
        return "Messages.Responses.OperationResponse{" +
                "type=" + type +
                ", response=" + response +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}