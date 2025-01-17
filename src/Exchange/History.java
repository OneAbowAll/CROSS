package Exchange;

import DataStructures.Tuple;
import com.google.gson.FormattingStyle;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class History
{
	private static final PriorityQueue<Order> history;
	private static Long nextOrderId = 0L;

	static
	{
		history = new PriorityQueue<>(new Comparator<Order>()
		{
			@Override
			public int compare(Order o1, Order o2)
			{
				return o1.GetDate().compareTo(o2.GetDate());
			}
		});
		Load();
	}

	public static void Load()
	{
		File historyFile = new File("storicoOrdini.json");

		try(FileReader fr = new FileReader(historyFile);
			BufferedReader br = new BufferedReader(fr);
			JsonReader reader = new JsonReader(br);)
		{
			reader.beginObject();

			reader.nextName();
			reader.beginArray();

			while(reader.hasNext())
			{
				reader.beginObject();

				//OrderID
				String name = reader.nextName();
				assert(name.equals("orderId"));
				long id = reader.nextLong();

				//Type
				name = reader.nextName();
				assert(name.equals("type"));
				OrderKind kind = (reader.nextString().equals("bid"))? OrderKind.BID: OrderKind.ASK;

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
				long size = reader.nextLong();

				//Price
				name = reader.nextName();
				assert(name.equals("price"));
				long price = reader.nextLong();

				//Timestamp
				name = reader.nextName();
				assert(name.equals("timestamp"));
				long timestamp = reader.nextLong();

				//Degli ordini completati non mi interessa sapere da chi è stato creato.
				Order order = new Order(id, kind, type, size, price, timestamp, "");
				history.add(order);

				reader.endObject();

				synchronized (nextOrderId)
				{
					if(nextOrderId < id)
						nextOrderId = id;
				}
			}

			reader.endArray();
			reader.endObject();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static void Save()
	{
		File historyFile = new File("storicoOrdini.json");

		try(FileWriter fw = new FileWriter(historyFile);
		   	BufferedWriter bw = new BufferedWriter(fw);
			JsonWriter js = new JsonWriter(bw))
		{

			js.setFormattingStyle(FormattingStyle.PRETTY);

			js.beginObject();
			js.name("trades").beginArray();

			synchronized (history)
			{
				for(Order order : history)
				{
					js.jsonValue(order.ToJson().toString());
					/*
					js.beginObject();
					js.name("orderId").value(order.GetOrderID());
					js.name("type").value(order.GetType().GetName());
					js.name("orderType").value(order.GetOrderType().name().toLowerCase(Locale.ROOT));
					js.name("size").value(order.GetSize());
					js.name("price").value(order.GetPrice());
					js.name("timestamp").value(order.GetDate().toEpochSecond(ZoneOffset.of("+0")));
					js.endObject();
					*/
				}
			}

			js.endArray();
			js.endObject();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static synchronized long GetNextId()
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

	public static Tuple<Integer> GetOrdersRange(Month month, int year)
	{
		int indexFrom= -1;
		int indexTo = -1;

		PriorityQueue<Order> copy;
		synchronized (history)
		{
			copy= new PriorityQueue<>(history);
		}

		Order order = copy.poll();

		int i = 0;
        while(order != null)
        {
			if(order.GetDate().getMonth() == month && order.GetDate().getYear() == year && indexFrom == -1)
				indexFrom = i;

			if(order.GetDate().getMonth() == month && order.GetDate().getYear() == year && indexFrom != -1)
				indexTo = i;

			i++;
			order = copy.poll();
        }

		return new Tuple<Integer>(indexFrom, indexTo);
	}

	public static String GetPeriodInfo(Month month, int year)
	{
		StringBuilder periodInfo = new StringBuilder();
		periodInfo.append(String.format("%9s %20s  %27s %20s\n", "", "ASK", "|", "BID"));
		periodInfo.append(String.format("%9s %10s %10s %10s %10s     | %10s %10s %10s %10s\n", month.name(),
										"Open", "Close", "Min", "Max", "Open", "Close", "Min", "Max"));
		//periodInfo.append("-------------------------------------------------------------------------------------------------------\n");

		Tuple<Integer> range = GetOrdersRange(month, year);

		if(range.GetFirst().equals(range.GetLast()))
		{
			return "NO DATA AVAILABLE";
		}

		Order[] orders;
		synchronized (history)
		{
			orders = history.toArray(new Order[0]);
		}

		Arrays.sort(orders, new Comparator<Order>()
		{
			@Override
			public int compare(Order o1, Order o2)
			{
				return o1.GetDate().compareTo(o2.GetDate());
			}
		});

		int currentDay = (orders[range.GetFirst()]).GetDate().getDayOfMonth();
		long askOpenPrice = 0;
		long askClosePrice = 0;
		long bidOpenPrice = 0;
		long bidClosePrice = 0;

		long askMinPrice = Integer.MAX_VALUE;
		long askMaxPrice = -1;
		long bidMinPrice = Integer.MAX_VALUE;
		long bidMaxPrice = -1;

		int j = 0;
		for (int i = range.GetFirst(); i <= range.GetLast(); i++)
		{
			j++;
			Order order = orders[i];

			//Verifico che stiamo lavorando sempre sullo stesso giorno,
			// nel caso contrario aggiungo le informazioni calcolate e faccio un reset delle variabili
			// in preparazione al prossimo giorno
			if(order.GetDate().getDayOfMonth() != currentDay)
			{
				//Mi assicuro che non escano valori assurdi (tipo -1) nel caso non ci siano stati ordini ask o bid per quel giorno.
				if(askMinPrice == Integer.MAX_VALUE) { askMinPrice = 0; }
				askMaxPrice = Math.max(0, askMaxPrice);

				if(bidMinPrice == Integer.MAX_VALUE) { bidMinPrice = 0; }
				bidMaxPrice = Math.max(0, bidMaxPrice);

				//Stampo le info
				periodInfo.append(String.format("%9d => %10d %10d %10d %10d  | %10d %10d %10d %10d\n", currentDay,
												askOpenPrice, askClosePrice, askMinPrice, askMaxPrice,
												bidOpenPrice, bidClosePrice, bidMinPrice, bidMaxPrice));

				//Reset per il giorno successivo
				currentDay = order.GetDate().getDayOfMonth();
				askOpenPrice = 0;
				askClosePrice = 0;
				bidOpenPrice = 0;

				askMinPrice = Integer.MAX_VALUE;
				askMaxPrice = -1;
				bidMinPrice = Integer.MAX_VALUE;
				bidMaxPrice = -1;

			}

			if(order.GetType() == OrderKind.BID)
			{
				bidClosePrice = order.GetPrice();

				if(bidOpenPrice == 0) bidOpenPrice = order.GetPrice();

				if(bidMinPrice > order.GetPrice()) bidMinPrice = order.GetPrice();
				if(bidMaxPrice < order.GetPrice()) bidMaxPrice = order.GetPrice();
			}
			else
			{
				askClosePrice = order.GetPrice();
				if(askOpenPrice == 0) askOpenPrice = order.GetPrice();

				if(askMinPrice > order.GetPrice()) askMinPrice = order.GetPrice();
				if(askMaxPrice < order.GetPrice()) askMaxPrice = order.GetPrice();
			}
		}

		periodInfo.append("Records amount: ").append(j).append("\n");

		return periodInfo.toString();
	}

	public static void TestPrint()
	{
		for (Order order : history) {
			System.out.println(order.toString());
		}
	}
}
