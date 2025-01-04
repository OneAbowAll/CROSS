package Messages;

public class Message
{
    OperationType type;
    String data;

    public Message(OperationType type, String data)
    {
        this.type = type;
        this.data = data;
    }

    public OperationType GetType()
    {
        return type;
    }

    public String GetData()
    {
        return data;
    }

    public String toString()
    {
        return "{ type: " + type.toString() + ", data: " + data + " }";
    }
}
