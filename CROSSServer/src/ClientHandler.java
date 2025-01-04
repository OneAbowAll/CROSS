import Messages.*;
import Messages.Requests.*;
import Messages.Responses.*;

import java.net.Socket;
import java.io.IOException;

public class ClientHandler implements Runnable
{
    private final Connection connection;

    public ClientHandler(Socket clientSocket)
    {
        connection = new Connection(clientSocket);
    }

    @Override
    public void run()
    {
        //Login / Register

        //while(true)
        {
            //Wait for client request
			try {
				Message msg = connection.WaitMessage();

				//Im expecting a Messages.Requests.LoginRequest so I will try to deserialize the message into a Messages.Requests.LoginRequest
				LoginRequest logReq = RequestDeserializer.ToLoginRequest(msg);
				System.out.println(logReq.toString());

                LoginResponse logResp = new LoginResponse(102);
                connection.SendMessage(logResp.ToMessage());

                //Handle the request and create a response
                connection.Close();

            }
            catch (IOException e)
            {
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
