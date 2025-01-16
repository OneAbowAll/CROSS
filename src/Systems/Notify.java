package Systems;

import Exchange.Order;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.*;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Notify
{
	private static DatagramSocket socket = null;
	private static DatagramPacket notification = null;

	private static ConcurrentHashMap<String, InetAddress> usersConnection;

	public static void Startup()
	{
		usersConnection = new ConcurrentHashMap<>();

		try {
			socket = new DatagramSocket();

			notification = new DatagramPacket(new byte[1024], 1024);
			notification.setPort(GlobalConfigs.NOTIF_PORT);
		}
		catch (IOException e) {	throw new RuntimeException(e); }
	}

	public static void Register(User user, Connection connection)
	{
		usersConnection.putIfAbsent(user.GetUsername(), connection.GetSocket().getInetAddress());
	}

	public static void Send(Order order)
	{
					//Valore indifferente se non è uno stop order
		Send(order, false);
	}

	public static void Send(Order order, boolean stopOrderOutcome)
	{
		JsonObject content = new JsonObject();
		JsonArray trades = new JsonArray();

		JsonObject oJson = order.ToJson();
		oJson.addProperty("stopOrderOutcome", stopOrderOutcome);
		trades.add(oJson);

		content.add("trades", trades);

		synchronized (notification)
		{
			byte[] udpContent = content.toString().getBytes();
			notification.setData(udpContent);

			InetAddress address = usersConnection.get(order.GetOwner());
			if(address == null) { return; }

			notification.setAddress(address);

			synchronized (socket)
			{
				try {
					socket.send(notification);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

}
