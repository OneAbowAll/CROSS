public enum RequestType
{
    LOGIN(201),
    REGISTER(202),
    UPDATE_CREDENTIALS(203),

    MARKET_ORDER(301),
    LIMIT_ORDER(302),
    STOP_ORDER(303),
    PRICE_HISTORY(311),
    CANCEL_ORDER(312),

    LOGOUT(400);

    final int _value;
    RequestType(int value)
    {
        this._value = value;
    }

    public int GetValue()
    {
        return _value;
    }
}
