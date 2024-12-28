import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable
{
    private Connection connection;

    public ClientHandler(Socket clientSocket)
    {
        connection = new Connection(clientSocket);
    }

    @Override
    public void run()
    {
        //Login / Register
        while(true){
            Request req = connection.WaitResponse();
        }


        //Client CmdLoop
        while(true)
        {

        }

        //Close connection
        try
        {
            connection.Close();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
