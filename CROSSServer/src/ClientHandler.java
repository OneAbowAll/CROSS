import Messages.Deserializer;
import Messages.Message;
import Messages.Requests.LoginRequest;
import Messages.Requests.RegisterRequest;
import Messages.Requests.UpdateCredentialsRequest;

import java.io.IOException;
import java.net.Socket;

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
		ClientSession session = new ClientSession();

        //Client CmdLoop
		while(!connection.IsClosed())
		{
			try
			{
				Message msg = connection.WaitMessage();

				switch (msg.GetType())
				{
					case LOGIN -> {
						LoginRequest logReq = Deserializer.ToLoginRequest(msg);
						connection.SendMessage(session.TryLogin(logReq));
					}

					case REGISTER -> {
						RegisterRequest regReq = Deserializer.ToRegisterRequest(msg);
						connection.SendMessage(session.TryRegister(regReq));
					}

					case UPDATE_CREDENTIALS -> {
						UpdateCredentialsRequest updReq = Deserializer.ToUpdateCredentialsRequest(msg);
						connection.SendMessage(session.TryUpdateCredentials(updReq));
					}
					/*
					case MARKET_ORDER -> {
						continue;
					}
					case LIMIT_ORDER -> {
						continue;
					}
					case STOP_ORDER -> {
						continue;
					}
					case PRICE_HISTORY -> {
						continue;
					}
					case CANCEL_ORDER -> {
						continue;
					}*/

					case LOGOUT -> {
						session.Logout();
					}

					case EXIT -> {
						session.Logout();
						connection.Close();
					}

					case null, default -> {
						//Message response = new Message(ResponseCode.UNEXPECTED_CMD.GetValue(),  "Unexpected command received. Please login/register first.");
						//connection.SendMessage(response);
					}

				}
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}

			System.out.println(session.toString());
		}


        //Close connection
        try
        {
            connection.Close();
        }
		catch (IOException e){ throw new RuntimeException(e); }

    }
}
