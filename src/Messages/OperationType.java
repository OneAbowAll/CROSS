package Messages;

public enum OperationType
{
    LOGIN(201),
    REGISTER(202),
    UPDATE_CREDENTIALS(203),

    MARKET_ORDER(301),
    LIMIT_ORDER(302),
    STOP_ORDER(303),
    PRICE_HISTORY(311),
    CANCEL_ORDER(312),
    GET_STATUS(313),

    LOGOUT(400),
    EXIT(555);

    private final int _value;
    OperationType(int value)
    {
        this._value = value;
    }

    public int GetValue()
    {
        return _value;
    }

    public static OperationType Get(int value)
    {
        OperationType[] values = OperationType.values();

        for (OperationType operationType : values)
        {
            if (operationType.GetValue() == value)
                return operationType;
        }

        return null;
    }
}
