package Exchange;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Order implements Comparable<Order>
{
	private final int orderID;

	private final OrderKind type;
	private final OrderType orderType;

	private long size;
	private final long price;

	private final LocalDateTime date;

	private String owner;

	public Order(int orderID, OrderKind kind, OrderType type, long size, long price, int timeStamp, String owner)
	{
		this.orderID = orderID;
		this.type = kind;
		this.orderType = type;
		this.size = size;
		this.price = price;
		this.date = LocalDateTime.ofInstant(Instant.ofEpochSecond(timeStamp), ZoneId.of("+0"));
		this.owner = owner;
	}

	public LocalDateTime GetDate()
	{
		return date;
	}

	public long GetPrice()
	{
		return price;
	}

	public long GetSize()
	{
		return size;
	}

	/**
	 * Prova a vendere amount monete dell'ordine.
	 * @param amount Quante monete si vuole provare a vendere.
	 * @return Quante monete sono state realmente vendute.
	 */
	public long TrySell(long amount)
	{
		long oldSize = size;
		size = Math.max(size - amount, 0);

		return oldSize - size;
	}

	public OrderType GetOrderType()
	{
		return orderType;
	}

	public OrderKind GetType()
	{
		return type;
	}

	public int GetOrderID()
	{
		return orderID;
	}

	public String GetOwner()
	{
		return owner;
	}

	@Override
	public String toString()
	{
		return "Exchange.Order{ " +
				"orderID=" + orderID +
				", type=" + type +
				", orderType=" + orderType +
				", size=" + size +
				", price=" + price +
				", date=" + date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy H:m:s", Locale.ENGLISH)) +
				" }";
	}

	public static Order Market(OrderKind type, long size, long price, String owner)
	{
		return new Order(History.GetNextId(), type, OrderType.MARKET, size, price,
				(int)LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(0)), owner);
	}

	public static Order Limit(OrderKind type, long size, long price, String owner)
	{
		return new Order(History.GetNextId(), type, OrderType.LIMIT, size, price,
				(int)LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(0)), owner);
	}

	public static Order Stop(OrderKind type, long size, long price, String owner)
	{
		return new Order(History.GetNextId(), type, OrderType.STOP, size, price,
				(int)LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(0)), owner);
	}

	public static Order Null(OrderKind type)
	{
		return new Order(-1, type, OrderType.MARKET, 0, 0,
				(int)LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(0)), "");
	}

	@Override
	public int compareTo(Order o)
	{
		//Se stiamo confrontando due order di tipi(bid/ask) diversi, non ha molto senso.
		//In teoria sta cosa non succederà mai, ma nel dubbio lasciamo invariato l'ordinamento tra i due.
		if(!type.equals(o.type)) return 0;

		//Se i nostri prezzi sono uguali (indifferentemente dal tipo di ordine) diamo priorità all'ordine più vecchio.
		if(price == o.price)
		{
			return date.compareTo(o.date);
		}

		if(type == OrderKind.BID)
		{
			if(price > o.price) return -1;
			else return 1;
		}
		else
		{
			if(price < o.price) return -1;
			else return 1;
		}
	}
}
