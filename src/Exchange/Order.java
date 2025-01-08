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

	private final float size;
	private final float price;

	private final LocalDateTime date;

	public Order(int orderID, OrderKind kind, OrderType type, float size, float price, int timeStamp)
	{
		this.orderID = orderID;
		this.type = kind;
		this.orderType = type;
		this.size = size;
		this.price = price;
		this.date = LocalDateTime.ofInstant(Instant.ofEpochSecond(timeStamp), ZoneId.of("+0"));
	}

	public LocalDateTime GetDate()
	{
		return date;
	}

	public float GetPrice()
	{
		return price;
	}

	public float GetSize()
	{
		return size;
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

	public static Order Market(OrderKind type, int size, int price)
	{
		return new Order(History.GetNextId(), type, OrderType.MARKET, (float) (size/1000.0), (float) (price/1000.0),
				(int)LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(0)));
	}

	public static Order Limit(OrderKind type, int size, int price)
	{
		return new Order(History.GetNextId(), type, OrderType.LIMIT, (float) (size/1000.0), (float) (price/1000.0),
				(int)LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(0)));
	}

	public static Order Stop(OrderKind type, int size, int price)
	{
		return new Order(History.GetNextId(), type, OrderType.STOP, (float) (size/1000.0), (float) (price/1000.0),
				(int)LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(0)));
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
