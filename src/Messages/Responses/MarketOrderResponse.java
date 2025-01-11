package Messages.Responses;

import Exchange.Order;
import Messages.OperationType;

public class MarketOrderResponse extends OrderResponse
{
    public MarketOrderResponse(Order order)
    {
        super(OperationType.MARKET_ORDER, order);
    }

}
