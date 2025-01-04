package Messages.Requests;

import Messages.CrossMessage;
import Messages.OperationType;

public abstract class Request extends CrossMessage
{
    protected Request(OperationType type)
    {
        super(type);
    }
}
