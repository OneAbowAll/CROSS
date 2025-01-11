import Exchange.Order;
import Exchange.OrderBook;
import Exchange.OrderKind;
import Messages.Deserializer;
import Messages.Message;
import Messages.Requests.LoginRequest;
import Messages.Requests.MarketOrderRequest;
import Messages.Requests.RegisterRequest;
import Messages.Requests.UpdateCredentialsRequest;
import Messages.Responses.MarketOrderResponse;
import Messages.Responses.StatusResponse;

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

					case MARKET_ORDER -> {
						MarketOrderRequest mrkRequest = Deserializer.ToMarketOrderRequest(msg);
						mrkRequest.SetOwner(session.GetUser().GetUsername());
						Order result = OrderBook.Bid(mrkRequest);

						MarketOrderResponse mrkResponse = new MarketOrderResponse(result);
						connection.SendMessage(mrkResponse);
					}

					/*
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

					case GET_STATUS ->
					{
						StatusResponse statusResponse = new StatusResponse(OrderBook.GetStatus());
						connection.SendMessage(statusResponse);
					}

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
