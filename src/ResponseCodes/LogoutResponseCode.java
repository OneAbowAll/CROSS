package ResponseCodes;

public enum LogoutResponseCode
{
    LOGIN_ERROR(101, "Username does not exist / Old password does not match");

    final int _value;
    final String _description;

    LogoutResponseCode(int value, String description)
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
