package Exchange;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

public class History
{
	private static final ArrayList<Order> history;
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

	public static void SaveOrder(Order order)
	{
		synchronized(history)
		{
			history.add(order);
		}
	}

	public static synchronized ArrayList<Integer> GetOrdersRange(Month month)
	{
		int indexFrom= -1;
		int indexTo = -1;

        for (int i = 0; i < history.size(); i++)
        {
			Order order = history.get(i);
			if(order.GetDate().getMonth() == month && indexFrom == -1)
				indexFrom = i;

			if(order.GetDate().getMonth() == month && indexFrom != -1)
				indexTo = i;
        }

		int finalIndexFrom = indexFrom;
		int finalIndexTo = indexTo;
		return new ArrayList<>(){{ add(finalIndexFrom); add(finalIndexTo); }};
	}

	public static synchronized String GetPeriodInfo(Month month)
	{
		int daysInMonth = month.length(false);

		StringBuilder periodInfo = new StringBuilder();
		periodInfo.append(String.format("%20s\n", month.name()));

		ArrayList<Integer> range = GetOrdersRange(month);

		if(range.getFirst().equals(range.getLast()))
		{
			return "NO DATA AVAILABLE";
		}

		int currentDay = -1;
		int askOpenPrice = -1;
		int askClosePrice = -1;
		int bidOpenPrice = -1;
		int bidClosePrice = -1;

		int askMinPrice = Integer.MAX_VALUE;
		int askMaxPrice = -1;
		int bidMinPrice = Integer.MAX_VALUE;
		int bidMaxPrice = -1;

		for (int i = range.getFirst(); i <= range.getLast(); i++)
		{
			Order order = history.get(i);
			if(currentDay == -1)
			{
				currentDay = order.GetDate().getDayOfMonth();
			}

			if(order.GetType() == OrderKind.BID)
			{
				bidClosePrice = order.GetPrice();

				if(bidOpenPrice == -1) bidOpenPrice = order.GetPrice();

				if(bidMinPrice > order.GetPrice()) bidMinPrice = order.GetPrice();
				if(bidMaxPrice < order.GetPrice()) bidMaxPrice = order.GetPrice();
			}
			else
			{
				askClosePrice = order.GetPrice();
				if(askOpenPrice == -1) askOpenPrice = order.GetPrice();

				if(askMinPrice > order.GetPrice()) askMinPrice = order.GetPrice();
				if(askMaxPrice < order.GetPrice()) askMaxPrice = order.GetPrice();
			}

			if(order.GetDate().getDayOfMonth() != currentDay)
			{
				periodInfo.append(String.format("%2d => %2d %2d %2d %2d -- %2d %2d %2d %2d\n", currentDay,
												askOpenPrice, askClosePrice, askMinPrice, askMaxPrice,
												bidOpenPrice, bidClosePrice, bidMinPrice, bidMaxPrice));

				currentDay = -1;
				askOpenPrice = -1;
				askClosePrice = -1;
				bidOpenPrice = -1;
				bidClosePrice = -1;

				askMinPrice = -1;
				askMaxPrice = Integer.MAX_VALUE;
				bidMinPrice = -1;
				bidMaxPrice = Integer.MAX_VALUE;

			}
		}


		return periodInfo.toString();
	}

	public static void TestPrint()
	{
		for (Order order : history) {
			System.out.println(order.toString());
		}
	}
}
