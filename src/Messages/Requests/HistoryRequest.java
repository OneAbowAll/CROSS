package Messages.Requests;

import Messages.OperationType;

public class HistoryRequest extends Request
{

    private String query;

    //Needed 4 correct gson deserialization (when using gson.fromJson(..., ...) this constructor gets used, not the one below).
    private HistoryRequest(){ super(OperationType.PRICE_HISTORY); }

    public HistoryRequest(String query)
    {
        this();
        this.query = query;
    }

    public int GetMonth()
    {
        return Integer.parseInt(query.substring(0, 2));
    }

    public int GetYear()
    {
        return Integer.parseInt(query.substring(2));
    }
}
