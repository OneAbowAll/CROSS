public enum ResponseCode
{
    OK(100),
    UNEXPECTED_CMD(200);

    final int _value;
    ResponseCode(int value)
    {
        this._value = value;
    }

    public int GetValue()
    {
        return _value;
    }
}
