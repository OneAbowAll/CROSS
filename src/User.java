public class User
{
    String username;
    String password;

    boolean isConnected;

    public User(String username, String password)
    {
        this.username = username;
        this.password = password;
    }

    public String GetUsername()
    {
        return username;
    }

    public String GetPassword()
    {
        return password;
    }

    public void SetConnected(boolean connected)
    {
        isConnected = connected;
    }

    public boolean IsConnected()
    {
        return isConnected;
    }

    @Override
    public String toString()
    {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
