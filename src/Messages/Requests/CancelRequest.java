package Messages.Requests;

import Messages.OperationType;

public class CancelRequest extends Request
{
	private int orderId;

	private CancelRequest(){ super(OperationType.CANCEL_ORDER); }

	public CancelRequest(int orderId)
	{
		this();
		this.orderId = orderId;
	}

	public int GetOrderId()
	{
		return orderId;
	}
}
