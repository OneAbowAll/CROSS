package ResponseCodes;

public enum UpdateResponseCode
{
    INVALID_PSW(101, "Invalid Password"),
    USRNAME_NAVBL(102, "Username already exists"),
    PSW_MIS_USER_NEXIST(103, "Username does not exist / Old password does not match"),
    NEW_PSW_EQUAL(103, "New password equal to old password"),
    USER_LOGGED(104, "User currently logged in"),
    OTHER_ERROR(105, "Other error");


    final int _value;
    final String _description;

    UpdateResponseCode(int value, String description)
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
