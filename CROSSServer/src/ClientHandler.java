import Messages.Deserializer;
import Messages.Message;
import Messages.Requests.LoginRequest;
import Messages.Requests.RegisterRequest;
import Messages.Responses.LoginResponse;
import Messages.Responses.RegisterResponse;

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
        //Login / Register
		/*
        while(true){
            //Wait for client request
			try
			{
				Message msg = connection.WaitMessage();
				if(msg.code != RequestType.LOGIN.GetValue() || msg.code != RequestType.REGISTER.GetValue()  || msg.code != RequestType.LOGOUT.GetValue())
				{
					Message response = new Message(ResponseCode.UNEXPECTED_CMD.GetValue(),  "Unexpected command received. Please login/register first.");
					connection.SendMessage(response);
					continue;
				}

				//Try handle login request
				try {
					LoginRequest logReq = Requests.ToLoginRequest(msg);
					System.out.println(logReq.toString());

					break;
				}
				catch (UnexpectedRequestException _){}

				//Try handle register request
				try {
					Messages.Requests.RegisterRequest regReq = Requests.ToRegisterRequest(msg);
					System.out.println(regReq.toString());

					break;
				}
				catch (UnexpectedRequestException _){}

				//Try handle login request
				try {
					Messages.Requests.RegisterRequest regReq = Requests.ToRegisterRequest(msg);
					System.out.println(regReq.toString());

					break;
				}
				catch (UnexpectedRequestException _){}
            }
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		*/


        //Client CmdLoop
		boolean exit = false;
		while(!exit)
		{
			try
			{
				Message msg = connection.WaitMessage();

				switch (msg.GetType())
				{
					case LOGIN -> {
						LoginRequest logReq = Deserializer.ToLoginRequest(msg);
						System.out.println(logReq.toString());

						LoginResponse logRep = new LoginResponse(102);
						connection.SendMessage(logRep);
					}

					case REGISTER -> {
						RegisterRequest regReq = Deserializer.ToRegisterRequest(msg);
						System.out.println(regReq.toString());

						//Message response = new Message(ResponseCode.UNEXPECTED_CMD.GetValue(),  "Unexpected command received. You have already logged in.");
						RegisterResponse regRep = new RegisterResponse(102);
						connection.SendMessage(regRep);
					}
					/*
					case UPDATE_CREDENTIALS -> {
						continue;
					}
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
						exit = true;
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
