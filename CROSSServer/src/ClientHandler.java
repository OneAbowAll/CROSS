import Exchange.Order;
import Exchange.OrderBook;
import Exchange.OrderKind;
import Messages.Deserializer;
import Messages.Message;
import Messages.Requests.*;
import Messages.Responses.*;
import Exchange.History;
import Systems.Connection;

import java.io.IOException;
import java.net.Socket;
import java.time.Month;
import java.util.concurrent.TimeoutException;

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
		connection.SetTimeout(ServerConfigs.CLIENT_MAX_TIMEOUT);

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

					case MARKET_ORDER ->
					{
						MarketOrderRequest mrkRequest = Deserializer.ToMarketOrderRequest(msg);
						mrkRequest.SetOwner(session.GetUser().GetUsername());

						Order result;
						if(mrkRequest.GetType() == OrderKind.BID)
							result = OrderBook.Bid(mrkRequest);
						else
							result = OrderBook.Ask(mrkRequest);

						MarketOrderResponse mrkResponse = new MarketOrderResponse(result);
						connection.SendMessage(mrkResponse);
					}

					case LIMIT_ORDER ->
					{
						LimitOrderRequest lmtRequest = Deserializer.ToLimitOrderRequest(msg);
						lmtRequest.SetOwner(session.GetUser().GetUsername());

						Order result;
						if(lmtRequest.GetType() == OrderKind.BID)
							result = OrderBook.Bid(lmtRequest);
						else
							result = OrderBook.Ask(lmtRequest);

						LimitOrderResponse lmtResponse = new LimitOrderResponse(result, (lmtRequest.GetSize() == result.GetSize()));
						connection.SendMessage(lmtResponse);
					}

					case STOP_ORDER ->
					{
						StopOrderRequest stopRequest = Deserializer.ToStopOrderRequest(msg);
						stopRequest.SetOwner(session.GetUser().GetUsername());

						Order result;
						if(stopRequest.GetType() == OrderKind.BID)
							result = OrderBook.Bid(stopRequest);
						else
							result = OrderBook.Ask(stopRequest);

						StopOrderResponse stopResponse = new StopOrderResponse(result);
						connection.SendMessage(stopResponse);
						continue;
					}

					case PRICE_HISTORY ->
					{
						HistoryRequest hstRequest = Deserializer.ToHistoryRequest(msg);

						Month month = Month.of(hstRequest.GetMonth());

						String result = History.GetPeriodInfo(Month.of(hstRequest.GetMonth()), hstRequest.GetYear());
						connection.SendMessage(new HistoryResponse(result));
					}

					case CANCEL_ORDER ->
					{
						CancelRequest cancelReq = Deserializer.ToCancelRequest(msg);
						boolean success = OrderBook.TryCancel(cancelReq.GetOrderId());

						connection.SendMessage(new CancelResponse((success)?100:101));
					}

					case GET_STATUS ->
					{
						StatusResponse statusResponse = new StatusResponse(OrderBook.GetStatus());
						connection.SendMessage(statusResponse);
					}

					case LOGOUT -> {
						if(session.GetUser() == null)
						{
							LogoutResponse logoutResponse = new LogoutResponse(101);
							connection.SendMessage(logoutResponse);
							continue;
						}

						session.Logout();
						LogoutResponse logoutResponse = new LogoutResponse(100);
						connection.SendMessage(logoutResponse);
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
				System.out.println("Chiusura connessioneaasdasasd");
				throw new RuntimeException(e);
			}
			catch (TimeoutException e) {
				try {
					//Se l'utente non è loggato si ignora il timeout, ovvero che solo in questo caso,
					//dopo l'eccezione la chiamata al WaitMessage diventa bloccante a tempo indeterminato finchè non viene fatto un login.
					//In parole povere se un utente non fa il login/registrazione, può rimanere connesso per sempre.
					if(session.GetUser() == null) { continue; }

					session.Logout();
					connection.SendMessage(new LogoutResponse(102));
				}
				catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			}

			System.out.println(session.toString());
		}

		try { connection.Close(); }
		catch (IOException ex) { throw new RuntimeException(ex); }
    }
}
