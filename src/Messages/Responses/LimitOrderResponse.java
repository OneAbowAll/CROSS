package Messages.Responses;

import Exchange.Order;
import Messages.OperationType;

public class LimitOrderResponse extends OrderResponse
{
    public boolean isEvicted;

    private LimitOrderResponse(Order order)
    {
        super(OperationType.LIMIT_ORDER, order);
    }

    public LimitOrderResponse(Order order, boolean isEvicted)
    {
        this(order);
        this.isEvicted = isEvicted;
    }

    public boolean HasBeenEvicted()
    {
        return isEvicted;
    }
}
