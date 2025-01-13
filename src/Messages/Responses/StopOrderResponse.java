package Messages.Responses;

import Exchange.Order;
import Messages.OperationType;

public class StopOrderResponse extends OrderResponse
{
	public StopOrderResponse(Order order)
	{
		super(OperationType.STOP_ORDER, order);
	}

}
