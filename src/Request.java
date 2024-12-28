public class Request extends Message
{
    protected Request(int code, String data)
    {
        super(code, data);
    }

    public String toString()
    {
        return "Request " + super.toString();
    }
}
