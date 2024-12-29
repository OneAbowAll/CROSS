import com.google.gson.*;

public abstract class Request
{
	protected transient RequestType type; //Non mi interessa che sia serializzato

    protected Request(RequestType type)
    {
		this.type = type;
    }

    public String Serialize()
    {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

	public Message ToMessage()
	{
		return new Message(type.GetValue(), Serialize());
	}

    public String toString()
    {
        return type + " " + Serialize();
    }
}
