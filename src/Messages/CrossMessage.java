package Messages;

import com.google.gson.*;

public abstract class CrossMessage
{
	protected transient OperationType type; //Non mi interessa che sia serializzato

    protected CrossMessage(OperationType type)
    {
		this.type = type;
    }

    private String Serialize()
    {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

	public Message ToMessage()
	{
		return new Message(type, Serialize());
	}

    public String toString()
    {
        return type + " " + Serialize();
    }
}
