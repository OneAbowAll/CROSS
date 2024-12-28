public class Response extends Message
{
    protected Response(int code, String data)
    {
        super(code, data);
    }

    public String toString()
    {
        return "Response " + super.toString();
    }
}
