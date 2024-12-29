public class Message
{
    int code;
    String data;

    protected Message(int code, String data)
    {
        this.code = code;
        this.data = data;
    }

    public int GetCode()
    {
        return code;
    }

    public String GetData()
    {
        return data;
    }

    public String toString()
    {
        return "{ type: " + code + ", data: " + data + " }";
    }
}
