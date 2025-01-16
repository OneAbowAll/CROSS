package Messages.Responses;

import Exchange.Order;
import Messages.CrossMessage;
import Messages.OperationType;

public abstract class OrderResponse extends CrossMessage
{
    private final long orderID;

    protected OrderResponse(OperationType type, Order order)
    {
        super(type);
        this.orderID = order.GetOrderID();
    }

    public long GetOrderID()
    {
        return orderID;
    }
}
