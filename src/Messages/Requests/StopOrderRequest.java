package Messages.Requests;

import Exchange.OrderKind;
import Messages.OperationType;

public class StopOrderRequest extends Request
{
	String type;
	int size;
	int price;

	String owner;

	private StopOrderRequest()
	{
		super(OperationType.STOP_ORDER);
	}

	public StopOrderRequest(OrderKind kind, int size, int price, String owner)
	{
		this();
		this.type = kind.GetName();
		this.size = size;
		this.price = price;
		this.owner = owner;
	}

	public OrderKind GetType()
	{
		return OrderKind.Get(type);
	}

	public int GetSize()
	{
		return size;
	}

	public int GetPrice()
	{
		return price;
	}

	public String GetOwner()
	{
		return owner;
	}
}
