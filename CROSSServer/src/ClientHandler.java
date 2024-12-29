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
            //Wait for client request
			try {
				Message msg = connection.WaitMessage();

				//Im expecting a LoginRequest so I will try to desirialize the message into a LoginRequest
				LoginRequest logReq = Requests.ToLoginRequest(msg);

				System.out.println(logReq.toString());
                //Handle the request and create a response

            } catch (IOException e) {
				throw new RuntimeException(e);
			}
		}


        //Client CmdLoop
        /*
		while(true) {
		}
		*/

        //Close connection
        /*
        try
        {
            connection.Close();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
		*/
    }
}
