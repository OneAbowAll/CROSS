package Exchange;

import Exceptions.UnexpectedOrderException;
import Exceptions.UnexpectedRequestException;
import Messages.Requests.LimitOrderRequest;
import Messages.Requests.MarketOrderRequest;

import java.util.ArrayList;
import java.util.TreeSet;

public class OrderBook
{
	/*
		BID <=> BUY // OFFERTE DI ACQUISTO: io voglio comprare X a prezzo Y
		ASK <=> SELL //OFFERTE DI VENDITA: io voglio vendere X a prezzo Y
	 */

	//Lato Bid
	private TreeSet<Order> limitOrdersBid;
	private ArrayList<Order> stopOrdersBid;

	//Lato Ask
	private ArrayList<Order> limitOrdersAsk;
	private ArrayList<Order> stopOrdersAsk;


	public OrderBook()
	{
		limitOrdersBid = new TreeSet<>();
		try
		{
			limitOrdersBid.add(Order.Limit(OrderKind.BID, 100, 2500000));
				Thread.sleep(2000);
			limitOrdersBid.add(Order.Limit(OrderKind.BID, 200, 2500000));
				Thread.sleep(2000);
			limitOrdersBid.add(Order.Limit(OrderKind.BID, 600, 2200000));
				Thread.sleep(2000);
			limitOrdersBid.add(Order.Limit(OrderKind.BID, 100, 8000000));
		}
		catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		stopOrdersBid = new ArrayList<>();

		limitOrdersAsk = new ArrayList<>();
		stopOrdersAsk = new ArrayList<>();
	}

	void AddOrder(Order order)
	{
		switch (order.GetOrderType())
		{
			case LIMIT -> {
				if(order.GetType() == OrderKind.BID)
					limitOrdersBid.add(order);
				else
					limitOrdersAsk.add(order);
			}

			case STOP -> {
				if(order.GetType() == OrderKind.BID)
					stopOrdersBid.add(order);
				else
					stopOrdersAsk.add(order);
			}

			case MARKET -> throw new UnexpectedOrderException("Market order are not managed by the order book.");
		}
	}

	public void TestPrint()
	{
		System.out.println(limitOrdersBid.pollFirst().toString());
		System.out.println("--------------------------------");
		for (Order order : limitOrdersBid) {
			System.out.println(order.toString());
		}
	}

	/*
	public Order Bid(LimitOrderRequest limitRequest)
	{

	}

	public Order Bid(MarketOrderRequest marketRequest)
	{

	}

	public Order Ask(LimitOrderRequest limitRequest)
	{

	}
	*/
}
