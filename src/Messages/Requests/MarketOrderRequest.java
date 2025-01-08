package Messages.Requests;

import Exchange.OrderKind;
import Messages.OperationType;

public class MarketOrderRequest extends Request
{
	String type;
	int size;

	//Needed 4 correct gson deserialization (when using gson.fromJson(..., ...) this constructor gets used, not the one below).
	private MarketOrderRequest()
	{
		super(OperationType.MARKET_ORDER);
	}

	public MarketOrderRequest(OrderKind kind, int size)
	{
		this();
		this.type = kind.GetName();
		this.size = size;
	}
}