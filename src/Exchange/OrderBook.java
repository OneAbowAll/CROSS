package Exchange;

import Exceptions.UnexpectedOrderException;
import Messages.Requests.LimitOrderRequest;
import Messages.Requests.MarketOrderRequest;
import Messages.Requests.StopOrderRequest;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class OrderBook
{
	/*
		BID <=> BUY // OFFERTE DI ACQUISTO: io voglio comprare X a prezzo Y
		ASK <=> SELL //OFFERTE DI VENDITA: io voglio vendere X a prezzo Y
	 */

	//Lato Bid
	private PriorityQueue<Order> limitOrdersBid;
	private PriorityQueue<Order> stopOrdersBid;

	//Lato Ask
	private PriorityQueue<Order> limitOrdersAsk;
	private PriorityQueue<Order> stopOrdersAsk;


	public OrderBook()
	{
		limitOrdersBid = new PriorityQueue<>();
		stopOrdersBid = new PriorityQueue<>();

		limitOrdersAsk = new PriorityQueue<>();
		try
		{
			limitOrdersAsk.add(Order.Limit(OrderKind.ASK, 100, 2500, ""));
			Thread.sleep(2000);
			limitOrdersAsk.add(Order.Limit(OrderKind.ASK, 200, 2500, ""));
			Thread.sleep(2000);
			limitOrdersAsk.add(Order.Limit(OrderKind.ASK, 600, 2200, ""));
			Thread.sleep(2000);
			limitOrdersAsk.add(Order.Limit(OrderKind.ASK, 100, 8000, ""));
		}
		catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		stopOrdersAsk = new PriorityQueue<>();
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

	public void Print()
	{
		System.out.format("%15s\n", "Ask Side");
		Info(limitOrdersAsk);

		System.out.println();

		System.out.format("%15s\n", "Bid Side");
		Info(limitOrdersBid);
	}

	public void Info(PriorityQueue<Order> orderQueue)
	{
		System.out.format("%10s%10s%10s\n", "Price", "Size", "Total");

		if(orderQueue.isEmpty())
			return;

		PriorityQueue<Order> orders = new PriorityQueue<>(orderQueue);
		Order order = orders.poll();

		int price = order.GetPrice();
		int size = order.GetSize();

		while(!orders.isEmpty())
		{
			order = orders.poll();
			if(order.GetPrice() == price)
			{
				size += order.GetSize();
			}
			else
			{
				System.out.format("%10d%10d%10d\n", price, size, price * size);

				price = order.GetPrice();
				size = order.GetSize();
			}
		}

		System.out.format("%10d%10d%10d\n", price, size, price * size);
	}

	public void TestBid()
	{
		System.out.println("Prima");
		Print();

		System.out.println("--------------------------------------------------------");

		Bid(new LimitOrderRequest(OrderKind.BID, 800, 2200, ""));
		Bid(new LimitOrderRequest(OrderKind.BID, 1000, 2500, ""));

		System.out.println("Dopo");
		Print();
	}


	public Order Bid(LimitOrderRequest limitRequest)
	{
		//Se è un Buy/Bid
		if(limitRequest.GetType() == OrderKind.ASK)
			throw new UnexpectedOrderException("To make a Bid you need a Bid-Request.");

		//Trovo l'offerta di vendita migliore -> lato Ask
		Order order = limitOrdersAsk.peek();

		int coinsToSell = limitRequest.GetSize();

		//Continua a pescare ordini finchè o finiscono o non riescono più a soddisfare l'offerta di acquisto.
		while(order != null && (order.GetPrice() <= limitRequest.GetPrice()) && coinsToSell > 0)
		{
			int amountSold = order.TrySell(coinsToSell);
			int total = order.GetPrice() * amountSold;

			coinsToSell -= amountSold;

			//Se soddisfatto salva l'ordine
			if (order.GetSize() == 0) {
				//History.Save(order) or History.Add(order)
				limitOrdersAsk.poll(); //Rimuovi se l'ordine è soddisfatto.
			}

			order = limitOrdersAsk.peek();
		}

		//Se la richiesta di buy non è stata completata, aggiungo un nuovo LimitOrder al lato ask dove chiedo ciò che rimane al prezzo richiesto.
		if(coinsToSell > 0)
		{
			Order newOrder = Order.Limit(OrderKind.BID, coinsToSell, limitRequest.GetPrice(), limitRequest.GetOwner());
			limitOrdersBid.add(newOrder);

			return newOrder;
		}

		//Se l'ordine è stato completamente evaso aggiungilo alla History e restituiscilo.
		if(coinsToSell == 0)
		{
			Order finalOrder = Order.Limit(OrderKind.BID, limitRequest.GetSize(), limitRequest.GetPrice(), limitRequest.GetOwner());
			//History.Save(finalOrder) or History.Add(finalOrder)
			return finalOrder;
		}

		//Fallback
		return Order.Null(OrderKind.BID);
	}

	public Order Bid(MarketOrderRequest marketRequest)
	{
		//Se è un Buy/Bid
		if(marketRequest.GetType() == OrderKind.ASK)
			throw new UnexpectedOrderException("To make a Bid you need a Bid-Request.");

		//Trovo l'offerta di vendita migliore -> lato Ask
		Order order = limitOrdersAsk.peek();

		if(order == null)
			return Order.Null(OrderKind.BID);

		//Vedo se soddisfa il MarketOrder
		if(order.GetSize() >= marketRequest.GetSize())
		{
			int total = order.GetPrice() * marketRequest.GetSize();
			order.TrySell(marketRequest.GetSize());

			//Se soddisfatto salva l'ordine
			if (order.GetSize() == 0) {
				//History.Save(order) or History.Add(order)
				limitOrdersAsk.poll(); //Rimuovi se l'ordine è soddisfatto.
			}

			Order finalOrder = Order.Market(OrderKind.BID, marketRequest.GetSize(), total, marketRequest.GetOwner());
			//History.Save(finalOrder) or History.Add(finalOrder)

			return finalOrder;
		}

		return Order.Null(OrderKind.BID);
	}


	public Order Ask(LimitOrderRequest limitRequest)
	{
		//Se è un Buy/Bid
		if(limitRequest.GetType() == OrderKind.BID)
			throw new UnexpectedOrderException("To make an Ask you need a Ask-Request.");

		//Trovo l'offerta di vendita migliore -> lato Ask
		Order order = limitOrdersBid.peek();

		int coinsToSell = limitRequest.GetSize();

		//Continua a pescare ordini finchè o finiscono o non riescono più a soddisfare l'offerta di acquisto.
		while(order != null && (order.GetPrice() >= limitRequest.GetPrice()) && coinsToSell > 0)
		{
			int amountSold = order.TrySell(coinsToSell);
			int total = order.GetPrice() * amountSold;

			coinsToSell -= amountSold;

			//Se soddisfatto salva l'ordine
			if (order.GetSize() == 0) {
				//History.Save(order) or History.Add(order)
				limitOrdersBid.poll(); //Rimuovi se l'ordine è soddisfatto.
			}

			order = limitOrdersBid.peek();
		}

		//Se la richiesta di buy non è stata completata, aggiungo un nuovo LimitOrder al lato ask dove chiedo ciò che rimane al prezzo richiesto.
		if(coinsToSell > 0)
		{
			Order newOrder = Order.Limit(OrderKind.ASK, coinsToSell, limitRequest.GetPrice(), limitRequest.GetOwner());
			limitOrdersAsk.add(newOrder);

			return newOrder;
		}

		//Se l'ordine è stato completamente evaso aggiungilo alla History e restituiscilo.
		if(coinsToSell == 0)
		{
			Order finalOrder = Order.Limit(OrderKind.ASK, limitRequest.GetSize(), limitRequest.GetPrice(), limitRequest.GetOwner());
			//History.Save(finalOrder) or History.Add(finalOrder)
			return finalOrder;
		}

		//Fallback
		return Order.Null(OrderKind.BID);
	}

	public Order Ask(MarketOrderRequest marketRequest)
	{
		//Se è un Buy/Bid
		if(marketRequest.GetType() == OrderKind.BID)
			throw new UnexpectedOrderException("To make an Ask you need a Ask-Request.");

		//Trovo l'offerta di vendita migliore -> lato Ask
		Order order = limitOrdersBid.peek();

		if(order == null)
			return Order.Null(OrderKind.ASK);

		//Vedo se soddisfa il MarketOrder
		if(order.GetSize() >= marketRequest.GetSize())
		{
			int total = order.GetPrice() * marketRequest.GetSize();
			order.TrySell(marketRequest.GetSize());

			//Se soddisfatto salva l'ordine
			if (order.GetSize() == 0) {
				//History.Save(order) or History.Add(order)
				limitOrdersBid.poll(); //Rimuovi se l'ordine è soddisfatto.
			}

			Order finalOrder = Order.Market(OrderKind.ASK, marketRequest.GetSize(), order.GetPrice(), marketRequest.GetOwner());
			//History.Save(finalOrder) or History.Add(finalOrder)

			return finalOrder;
		}

		return Order.Null(OrderKind.ASK);
	}
}
