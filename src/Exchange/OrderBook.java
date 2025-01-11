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
	private static PriorityQueue<Order> limitOrdersBid;
	private static PriorityQueue<Order> stopOrdersBid;

	//Lato Ask
	private static PriorityQueue<Order> limitOrdersAsk;
	private static PriorityQueue<Order> stopOrdersAsk;


	static
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

	public static String Info(PriorityQueue<Order> orderQueue)
	{
		StringBuilder info = new StringBuilder();
		info.append(String.format("%10s%10s%10s\n", "Price", "Size", "Total"));

		if(orderQueue.isEmpty())
			return info.toString();

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
				info.append(String.format("%10d%10d%10d\n", price, size, price * size));

				price = order.GetPrice();
				size = order.GetSize();
			}
		}

		info.append(String.format("%10d%10d%10d\n", price, size, price * size));
		return info.toString();
	}

	public static void TestBid()
	{
		System.out.println("Prima");
		System.out.println(GetStatus());

		System.out.println("--------------------------------------------------------");

		Bid(new LimitOrderRequest(OrderKind.BID, 800, 2200, ""));
		Bid(new LimitOrderRequest(OrderKind.BID, 1000, 2500, ""));

		System.out.println("Dopo");
		System.out.println(GetStatus());
	}

	public static String GetStatus()
	{
		String status = "";
		status += String.format("%20s\n", "Ask Side");

		status += Info(limitOrdersAsk);

		status += "-------------------------------------\n";

		status += String.format("%20s\n", "Bid Side");
		status += Info(limitOrdersBid);

		return status;
	}


	public static Order Bid(LimitOrderRequest limitRequest)
	{
		//Se è un Buy/Bid
		if(limitRequest.GetType() == OrderKind.ASK)
			throw new UnexpectedOrderException("To make a Bid you need a Bid-Request.");

		int coinsToSell = 0;
		synchronized (limitOrdersAsk)
		{
			//Trovo l'offerta di vendita migliore -> lato Ask
			Order order = limitOrdersAsk.peek();

			//Teniamo traccia di quanto vogliamo vendere/quanto ci rimarrà da vendere(potenzialmente)
			coinsToSell = limitRequest.GetSize();

			//Continua a pescare ordini finchè o finiscono o non riescono più a soddisfare l'offerta di acquisto.
			while(order != null && (order.GetPrice() <= limitRequest.GetPrice()) && coinsToSell > 0)
			{
				int amountSold = order.TrySell(coinsToSell);

				coinsToSell -= amountSold;

				//Se soddisfatto salva l'ordine
				if (order.GetSize() == 0) {
					//History.Save(order) or History.Add(order)
					limitOrdersAsk.poll(); //Rimuovi se l'ordine è soddisfatto.
				}

				order = limitOrdersAsk.peek();
			}
		}

		//Se la richiesta di buy non è stata completata, aggiungo un nuovo LimitOrder al lato ask dove chiedo ciò che rimane al prezzo richiesto.
		if(coinsToSell > 0)
		{
			Order newOrder = Order.Limit(OrderKind.BID, coinsToSell, limitRequest.GetPrice(), limitRequest.GetOwner());

			synchronized (limitOrdersBid)
			{
				limitOrdersBid.add(newOrder);
			}

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

	public static Order Bid(MarketOrderRequest marketRequest)
	{
		//Se è un Buy/Bid
		if(marketRequest.GetType() == OrderKind.ASK)
			throw new UnexpectedOrderException("To make a Bid you need a Bid-Request.");

		int price = 0;
		synchronized (limitOrdersAsk)
		{
			if(limitOrdersAsk.isEmpty())
				return Order.Null(OrderKind.BID);

			//Trovo l'offerta di vendita migliore -> lato Ask
			PriorityQueue<Order> orders = new PriorityQueue<>(limitOrdersAsk);
			Order order = orders.poll();

			//Teniamo traccia di quanto vogliamo vendere
			int coinsToSell = marketRequest.GetSize();
			price = order.GetPrice();

			//In un certo senso sto "simulando" la viabilità del MarketOrder.
			while (order != null && order.GetPrice() == price && coinsToSell > 0)
			{
				coinsToSell = Math.max(0, coinsToSell - order.GetSize());
				order = orders.poll();
			}

			//Se esco dal loop precedente, significa che non ci sono abbastanza Asks(o in generale o con prezzo conveniente)
			if (coinsToSell > 0)
				return Order.Null(OrderKind.BID);

			//In alternativa resetta coinToSell ed evadi realmente la MarketRequest e i LimitOrder rilevanti.
			coinsToSell = marketRequest.GetSize();
			order = limitOrdersAsk.peek();

			while (order != null && order.GetPrice() == price && coinsToSell > 0)
			{
				int amountSold = order.TrySell(coinsToSell);

				coinsToSell -= amountSold;

				//Se soddisfatto salva l'ordine
				if (order.GetSize() == 0)
				{
					//History.Save(order) or History.Add(order)
					limitOrdersAsk.poll(); //Rimuovi se l'ordine è soddisfatto.
				}

				order = limitOrdersAsk.peek();
			}
		}

		//A questo punto so che il marketOrder è fattibile, quindi lo creo, lo salvo e lo resitutisco.
		Order finalOrder = Order.Market(OrderKind.BID, marketRequest.GetSize(), price, marketRequest.GetOwner());
		//History.Save(finalOrder) or History.Add(finalOrder)

		return finalOrder;
	}


	public static Order Ask(LimitOrderRequest limitRequest)
	{
		//Se è un Buy/Bid
		if(limitRequest.GetType() == OrderKind.BID)
			throw new UnexpectedOrderException("To make an Ask you need a Ask-Request.");


		int coinsToSell = 0;
		synchronized (limitOrdersAsk)
		{
			//Trovo l'offerta di vendita migliore -> lato Ask
			Order order = limitOrdersBid.peek();

			coinsToSell = limitRequest.GetSize();

			//Continua a pescare ordini finchè o finiscono o non riescono più a soddisfare l'offerta di acquisto.
			while (order != null && (order.GetPrice() >= limitRequest.GetPrice()) && coinsToSell > 0)
			{
				int amountSold = order.TrySell(coinsToSell);
				int total = order.GetPrice() * amountSold;

				coinsToSell -= amountSold;

				//Se soddisfatto salva l'ordine
				if (order.GetSize() == 0)
				{
					//History.Save(order) or History.Add(order)
					limitOrdersBid.poll(); //Rimuovi se l'ordine è soddisfatto.
				}

				order = limitOrdersBid.peek();
			}
		}

		//Se la richiesta di buy non è stata completata, aggiungo un nuovo LimitOrder al lato ask dove chiedo ciò che rimane al prezzo richiesto.
		if(coinsToSell > 0)
		{
			Order newOrder = Order.Limit(OrderKind.ASK, coinsToSell, limitRequest.GetPrice(), limitRequest.GetOwner());

			synchronized (limitOrdersAsk)
			{
				limitOrdersAsk.add(newOrder);
			}
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

	public static Order Ask(MarketOrderRequest marketRequest)
	{
		//Se è un Buy/Bid
		if(marketRequest.GetType() == OrderKind.BID)
			throw new UnexpectedOrderException("To make an Ask you need a Ask-Request.");

		int price = 0;
		synchronized (limitOrdersAsk)
		{
			if(limitOrdersAsk.isEmpty())
				return Order.Null(OrderKind.BID);

			//Trovo l'offerta di acquisto migliore -> lato Bid
			PriorityQueue<Order> orders = new PriorityQueue<>(limitOrdersBid);
			Order order = orders.poll();

			//Teniamo traccia di quanto vogliamo vendere
			int coinsToSell = marketRequest.GetSize();
			price = order.GetPrice();

			//In un certo senso sto "simulando" la viabilità del MarketOrder.
			while (order != null && order.GetPrice() == price && coinsToSell > 0)
			{
				coinsToSell = Math.max(0, coinsToSell - order.GetSize());
				order = orders.poll();
			}

			//Se esco dal loop precedente, significa che non ci sono abbastanza Bids(o in generale o con prezzo conveniente)
			if (coinsToSell > 0)
				return Order.Null(OrderKind.ASK);

			//In alternativa resetta coinToSell ed evadi realmente la MarketRequest e i LimitOrder rilevanti.
			coinsToSell = marketRequest.GetSize();
			order = limitOrdersBid.peek();

			while (order != null && order.GetPrice() == price && coinsToSell > 0)
			{
				int amountSold = order.TrySell(coinsToSell);

				coinsToSell -= amountSold;

				//Se soddisfatto salva l'ordine
				if (order.GetSize() == 0)
				{
					//History.Save(order) or History.Add(order)
					limitOrdersBid.poll(); //Rimuovi se l'ordine è soddisfatto.
				}

				order = limitOrdersBid.peek();
			}
		}

		//A questo punto so che il marketOrder è fattibile, quindi lo creo, lo salvo e lo resitutisco.
		Order finalOrder = Order.Market(OrderKind.ASK, marketRequest.GetSize(), price, marketRequest.GetOwner());
		//History.Save(finalOrder) or History.Add(finalOrder)

		return finalOrder;
	}
}
