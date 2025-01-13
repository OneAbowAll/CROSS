import Messages.Deserializer;
import Messages.Message;
import Messages.Responses.*;
import Systems.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ResponseListener extends Thread
{
	private final Connection _listenConnection;

	public ResponseListener(Connection connection)
	{
		_listenConnection = connection;
	}

	@Override
	public void run()
	{
		while(!_listenConnection.IsClosed())
		{
			try
			{
				//Ascolta per un messaggio / Tutti i messaggi in arrivo verso il client sono Response
				Message msg = _listenConnection.WaitMessage();
				switch (msg.GetType())
				{
					case LOGIN -> {
						LoginResponse response = Deserializer.ToLoginResponse(msg);
						System.out.println(response.GetErrorMessage());
					}

					case REGISTER -> {
						RegisterResponse response = Deserializer.ToRegisterResponse(msg);
						System.out.println(response.GetErrorMessage());
					}

					case UPDATE_CREDENTIALS -> {
						UpdateCredentialsResponse response = Deserializer.ToUpdateCredentialsResponse(msg);
						System.out.println(response.GetErrorMessage());
					}

					case MARKET_ORDER -> {
						MarketOrderResponse response = Deserializer.ToMarketOrderResponse(msg);

						if(response.GetOrderID() == -1)
							System.out.println("The requested MarketOrder could not be completed. You could try placing a LimitOrder.");
						else
							System.out.println("The MarketOrder has been successfully completed. Your OrderId is => "+ response.GetOrderID());
					}

					case LIMIT_ORDER -> {
						LimitOrderResponse response = Deserializer.ToLimitOrderResponse(msg);
						if(response.HasBeenEvicted())
							System.out.println("The LimitOrder has been successfully completed. Your OrderId is => "+ response.GetOrderID());
						else
							System.out.println("Your LimitOrder couldn't be completed.\n" +
												"Your order has been added to the OrderBook with the following OrderId => "
												+ response.GetOrderID());
					}

					case STOP_ORDER -> {
						StopOrderResponse response = Deserializer.ToStopOrderResponse(msg);

						if(response.GetOrderID() == -1)
							System.out.println("The StopOrder could not be completed.");
						else
							System.out.println("The StopOrder has been created. Your OrderId is => "+ response.GetOrderID()+
												"\n You can cancel your StopOrder any time by using the cancel-command.");

					}

					case PRICE_HISTORY -> {
						HistoryResponse response = Deserializer.ToHistoryResponse(msg);
						System.out.println(response.GetErrorMessage());
					}

					case CANCEL_ORDER -> {
						CancelResponse response = Deserializer.ToCancelResponse(msg);
						System.out.println(response.GetErrorMessage());
					}

					case GET_STATUS -> {
						StatusResponse response = Deserializer.ToStatusResponse(msg);
						System.out.println(response.GetErrorMessage());
					}

					case LOGOUT -> {
						LogoutResponse response = Deserializer.ToLogoutResponse(msg);
						System.out.println(response.GetErrorMessage());
					}

					case EXIT -> {
						continue;
					}
				}

			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
			catch (TimeoutException _)
			{
				continue;
			}

		}
	}
}
