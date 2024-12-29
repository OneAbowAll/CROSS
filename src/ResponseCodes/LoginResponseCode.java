package ResponseCodes;

public enum LoginResponseCode
{
    PSW_MIS_USER_NEXIST(101, "Username does not exist / Password mismatch"),
    USER_LOGGED(102, "User already logged in"),
    OTHER_ERROR(103, "Other error");

    final int _value;
    final String _description;

    LoginResponseCode(int value, String description)
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
