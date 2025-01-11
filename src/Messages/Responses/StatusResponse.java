package Messages.Responses;

import Messages.OperationType;

public class StatusResponse extends OperationResponse
{
    public StatusResponse(String statusMessage)
    {
        super(OperationType.GET_STATUS, 100, statusMessage);
    }
}
