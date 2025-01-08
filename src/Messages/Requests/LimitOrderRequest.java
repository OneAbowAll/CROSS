package Messages.Requests;

import Exchange.OrderKind;
import Messages.OperationType;

public class LimitOrderRequest extends Request
{
	String type;
	int size;
	int price;

	//Needed 4 correct gson deserialization (when using gson.fromJson(..., ...) this constructor gets used, not the one below).
	private LimitOrderRequest()
	{
		super(OperationType.LIMIT_ORDER);
	}

	public LimitOrderRequest(OrderKind kind, int size, int price)
	{
		this();
		this.type = kind.GetName();
		this.size = size;
		this.price = price;
	}
}
