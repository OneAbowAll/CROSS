import Messages.Responses.UpdateCredentialsResponse;

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

    public synchronized void SetConnected(boolean connected)
    {
        this.isConnected = connected;
    }

    public synchronized boolean TryConnect()
    {
        if(IsConnected()) return false;

        this.SetConnected(true);
        return true;
    }

    public synchronized boolean IsConnected()
    {
        return this.isConnected;
    }

    public synchronized int TryChangePassword(String oldPassword, String newPassword)
    {
        if(IsConnected())
            return 104;

        //Se la password passata non combacia con quella attuale
        if(!oldPassword.equals(this.password))
            return 102;

        //Se è uguale a quella vecchia
        if(newPassword.equals(this.password))
            return 103;

        //Se la password è vuota non è valida
        if(newPassword.isBlank() || newPassword.length() < 3)
            return 101;

        //Cambia password e conferma il cambiamento
        this.password = newPassword;
        return 100;
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
