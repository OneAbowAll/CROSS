package Exchange;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class History
{
	//TODO: RENDI QUESTA CLASSE USABILE DA PIU' THREADS
	private static ArrayList<Order> history;
	private static int nextOrderId = 0;

	static
	{
		history = new ArrayList<>();
		File historyFile = new File("storicoOrdini.json");

		Gson gson = new Gson();
		try(FileReader fr = new FileReader(historyFile);
			BufferedReader br = new BufferedReader(fr);
			JsonReader reader = new JsonReader(br);)
		{
			reader.beginObject();

			reader.nextName();
			reader.beginArray();

			int longestTs = -1;
			while(reader.hasNext())
			{
				reader.beginObject();

				//OrderID
				String name = reader.nextName();
				assert(name.equals("orderId"));
				int id = reader.nextInt();

				//Type
				name = reader.nextName();
				assert(name.equals("type"));
				OrderKind kind = (reader.nextString().equals("bid"))? OrderKind.BID:OrderKind.ASK;

				//Exchange.OrderType
				name = reader.nextName();
				assert(name.equals("orderType"));

				OrderType type;
				String typeString = reader.nextString();
				if(typeString.equals("market"))
					type = OrderType.MARKET;
				else if(typeString.equals("limit"))
					type = OrderType.LIMIT;
				else
					type = OrderType.STOP;

				//Size
				name = reader.nextName();
				assert(name.equals("size"));
				int size = reader.nextInt();

				//Price
				name = reader.nextName();
				assert(name.equals("price"));
				int price = reader.nextInt();

				//Timestamp
				name = reader.nextName();
				assert(name.equals("timestamp"));
				int timestamp = reader.nextInt();

				//Degli ordini completati non mi interessa sapere da chi è stato creato.
				Order order = new Order(id, kind, type, size, price, timestamp, "");
				history.add(order);

				reader.endObject();
			}

			reader.endArray();
			reader.endObject();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}

	}

	public static synchronized int GetNextId()
	{
		return nextOrderId++;
	}

	public static void AddOrder()
	{

	}

	public static Order[] GetOrders(LocalDateTime to, LocalDateTime from)
	{
		return new Order[0];
	}

	public static void TestPrint()
	{
		for (Order order : history) {
			System.out.println(order.toString());
		}
	}
}
