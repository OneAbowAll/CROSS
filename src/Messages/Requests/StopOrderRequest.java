package Messages.Requests;

import Exchange.OrderKind;
import Messages.OperationType;

public class StopOrderRequest extends Request
{
	String type;
	long size;
	long price;

	String owner;

	private StopOrderRequest()
	{
		super(OperationType.STOP_ORDER);
	}

	public StopOrderRequest(OrderKind kind, long size, long price, String owner)
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

	public long GetSize()
	{
		return size;
	}

	public long GetPrice()
	{
		return price;
	}

	public String GetOwner()
	{
		return owner;
	}

	public void SetOwner(String owner) { this.owner = owner; }
}
