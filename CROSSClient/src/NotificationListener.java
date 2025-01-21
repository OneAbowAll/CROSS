import Exchange.Order;
import Exchange.OrderKind;
import Exchange.OrderType;
import Systems.Connection;
import Systems.GlobalConfigs;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.StringReader;
import java.net.*;
import java.util.ArrayList;

public class NotificationListener extends Thread
{
	//Non uso la listenConnection, ma la sfrutto per capire quando dovrebbe chiudersi il thread.
	private final Connection _listenConnection;

	public NotificationListener(Connection listenConnection)
	{
		_listenConnection = listenConnection;
	}

	@Override
	public void run()
	{
		try(DatagramSocket socket = new DatagramSocket(null))
		{
			//Il reuse è necessario quando testiamo più di un client in locale
			socket.setReuseAddress(true);
			socket.bind(new InetSocketAddress("127.0.0.1", GlobalConfigs.NOTIF_PORT));
			socket.setSoTimeout(2000);

			DatagramPacket receivedNotification = new DatagramPacket(new byte[1024], 1024);

			while(!_listenConnection.IsClosed())
			{
				try {
					socket.receive(receivedNotification);
				} catch (IOException e) {
					continue;
				}

				String content = new String(receivedNotification.getData());

				try(StringReader reader = new StringReader(content);
					JsonReader jsonReader = new JsonReader(reader))
				{
					//Se non viene settata la strictness a lenient JsonParser continuerà a
					jsonReader.setStrictness(Strictness.LENIENT);
					JsonObject obj = JsonParser.parseReader(jsonReader).getAsJsonObject();

					JsonArray array = obj.getAsJsonArray("trades");
					for(int i = 0; i < array.size(); i++)
					{
						JsonObject orderJson = array.get(i).getAsJsonObject();

						PrintNotifica(orderJson);
					}
				}
			}
		} catch (IOException e) { throw new RuntimeException(e); }
	}

	void PrintNotifica(JsonObject jsonOrder)
	{
		Order order = Order.FromJson(jsonOrder);
		boolean stopOrderOutcome = jsonOrder.get("stopOrderOutcome").getAsBoolean();

		String outputMessage = "";
		if(order.GetOrderType() == OrderType.STOP && !stopOrderOutcome)
		{
			outputMessage = String.format("[INFO] Your %s_Order#%d has failed. ", order.GetOrderType() , order.GetOrderID() );
			outputMessage += "You were trying to " + ((order.GetType() == OrderKind.BID)?"buy":"sell");
		}
		else
		{
			outputMessage = String.format("[INFO] Your %s_Order#%d has been completed. ", order.GetOrderType() , order.GetOrderID() );
			outputMessage += "You manged to "  + ((order.GetType() == OrderKind.BID)?"buy":"sell");
		}
		outputMessage += String.format(" %d฿ at %d$ each.", order.GetSize(), order.GetPrice());

		System.out.println(outputMessage);
	}
}
