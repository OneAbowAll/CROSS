package Messages.Requests;

import Messages.OperationType;

public class StatusRequest extends Request
{
    //Needed 4 correct gson deserialization (when using gson.fromJson(..., ...) this constructor gets used, not the one below).
    public StatusRequest()
    {
        super(OperationType.GET_STATUS);
    }
}
