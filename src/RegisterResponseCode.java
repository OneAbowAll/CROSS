public enum RegisterResponseCode
{
    INVALID_PSW(101, "Invalid Password"),
    USRNAME_NAVBL(102, "Username not available"),
    OTHER_ERROR(103, "Other error");

    final int _value;
    final String _description;

    RegisterResponseCode(int value, String description)
    {
        this._value = value;
        this._description = description;
    }

    public int GetValue()
    {
        return _value;
    }

    public String GetDesc()
    {
        return _description;
    }
}
