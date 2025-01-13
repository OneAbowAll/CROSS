package Messages.Responses;

import Messages.OperationType;

public class HistoryResponse extends OperationResponse
{
    public HistoryResponse(String history)
    {
        super(OperationType.PRICE_HISTORY, 100, history);
    }
}
