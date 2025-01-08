public class User
{
    private final String username;
    private String password;

    private transient volatile boolean isConnected;

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

    public void SetPassword(String password)
    {
        this.password = password;
    }

    public void SetConnected(boolean connected)
    {
        this.isConnected = connected;
    }

    public boolean IsConnected()
    {
        return this.isConnected;
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
