package Messages.Responses;

import Exchange.Order;
import Exchange.OrderType;
import Messages.CrossMessage;
import Messages.OperationType;

public abstract class OrderResponse extends CrossMessage
{
    private final int orderID;

    protected OrderResponse(OperationType type, Order order)
    {
        super(type);
        this.orderID = order.GetOrderID();
    }

    public int GetOrderID()
    {
        return orderID;
    }
}
