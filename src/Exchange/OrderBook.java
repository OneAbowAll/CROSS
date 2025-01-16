package Exchange;

import Exceptions.UnexpectedOrderException;
import Messages.Requests.LimitOrderRequest;
import Messages.Requests.MarketOrderRequest;
import Messages.Requests.StopOrderRequest;
import Systems.Notify;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class OrderBook
{
	/*
		BID <=> BUY // OFFERTE DI ACQUISTO: io voglio comprare X a prezzo Y
		ASK <=> SELL //OFFERTE DI VENDITA: io voglio vendere X a prezzo Y
	 */

	//Lato Bid
	private static final PriorityQueue<Order> limitOrdersBid;
	private static final ArrayList<Order> stopOrdersBid;

	//Lato Ask
	private static final PriorityQueue<Order> limitOrdersAsk;
	private static final ArrayList<Order> stopOrdersAsk;


	static
	{
		limitOrdersBid = new PriorityQueue<>();
		stopOrdersBid = new ArrayList<>();

		limitOrdersAsk = new PriorityQueue<>();
		try
		{
			limitOrdersAsk.add(Order.Limit(OrderKind.ASK, 100, 2500, ""));
			Thread.sleep(1000);
			limitOrdersAsk.add(Order.Limit(OrderKind.ASK, 200, 2500, ""));
			Thread.sleep(1000);
			limitOrdersAsk.add(Order.Limit(OrderKind.ASK, 600, 2200, ""));
			Thread.sleep(1000);
			limitOrdersAsk.add(Order.Limit(OrderKind.ASK, 100, 8000, ""));
		}
		catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		stopOrdersAsk = new ArrayList<>();
	}

	public static String Info(PriorityQueue<Order> orderQueue)
	{
		StringBuilder info = new StringBuilder();
		info.append(String.format("%10s%10s%10s\n", "Price", "Size", "Total"));

		if(orderQueue.isEmpty())
			return info.toString();

		Order order = orderQueue.poll();

		long price = order.GetPrice();
		long size = order.GetSize();

		while(!orderQueue.isEmpty())
		{
			order = orderQueue.poll();
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
		status += String.format("%24s\n", "Limit Orders");
		status += String.format("%22s\n", "Ask Side");

		PriorityQueue<Order> orders;
		synchronized (limitOrdersAsk)
		{
			orders = new PriorityQueue<>(limitOrdersAsk);
		}
		status += Info(orders);

		status += "-------------------------------------\n";
		status += String.format("Bid/Ask Spread: %5d\n", GetBestBuyPrice() - GetBestSellPrice());
		status += "-------------------------------------\n";

		status += String.format("%22s\n", "Bid Side");
		synchronized (limitOrdersBid)
		{
			orders = new PriorityQueue<>(limitOrdersBid);
		}
		status += Info(orders);

		status += "\n\n";
		status += String.format("%24s\n", "Stop Orders");
		status += GetStopsStatus();

		return status;
	}

	public static String GetStopsStatus()
	{
		StringBuilder status = new StringBuilder();
		status.append(String.format("%22s\n", "Ask Side"));

		synchronized (stopOrdersAsk)
		{
			status.append(String.format("%10s%10s%10s\n", "StopPrice", "Size", "User"));
			for (Order o: stopOrdersAsk)
			{
				status.append(String.format("%10d%10d%10s\n", o.GetPrice(), o.GetSize(), o.GetOwner()));
			}
		}

		status.append("-------------------------------------\n");

		status.append(String.format("%22s\n", "Bid Side"));
		synchronized (stopOrdersBid)
		{
			status.append(String.format("%10s%10s%10s\n", "StopPrice", "Size", "User"));
			for (Order o: stopOrdersBid)
			{
				status.append(String.format("%10d%10d%10s\n", o.GetPrice(), o.GetSize(), o.GetOwner()));
			}
		}

		return status.toString();
	}

	public synchronized static long GetBestBuyPrice()
	{
		if(limitOrdersAsk.isEmpty()) return 0;

		return limitOrdersAsk.peek().GetPrice();
	}

	public synchronized static long GetBestSellPrice()
	{
		if(limitOrdersBid.isEmpty()) return 0;

		return limitOrdersBid.peek().GetPrice();
	}

	public static void TryCompleteStops()
	{
		ArrayList<Order> removedStops = new ArrayList<>();
		ArrayList<Boolean> stopsOutcome = new ArrayList<>();

		//Try complete the bidOrders
		synchronized (stopOrdersBid)
		{
			for (Order order : stopOrdersBid)
			{
				//Se il prezzo di vendita supera la soglia
				if(order.GetPrice() >= GetBestBuyPrice() && GetBestBuyPrice() != 0) {
					//Prova a fare un marketOrder, se ha successo bene se no niente
					Order result = Bid(new MarketOrderRequest(OrderKind.BID, order.GetSize(), order.GetOwner()));

					if(result.GetOrderID() == -1)
					{
						//Notifica l'utente che non è andata a buon fine :(
						Notify.Send(order, false);
					}
					else
					{
						//Notifica l'utente che è andata a buon fine
						Notify.Send(order, true);
						History.SaveOrder(order);
					}

					//In entrambi i casi si rimuove lo stop order
					removedStops.add(order);
				}
			}

			stopOrdersBid.removeAll(removedStops);
		}

		//Try complete the askOrders
		synchronized (stopOrdersAsk)
		{
			removedStops = new ArrayList<>();
			for (Order order : stopOrdersAsk)
			{
				//Se il prezzo di acquisto supera la soglia
				if(order.GetPrice() <= GetBestSellPrice() && GetBestSellPrice() != 0) {
					//Prova a fare un marketOrder, se ha successo bene se no niente
					Order result = Ask(new MarketOrderRequest(OrderKind.ASK, order.GetSize(), order.GetOwner()));

					if(result.GetOrderID() == -1)
					{
						//Notifica l'utente che non è andata a buon fine :(
						Notify.Send(order, false);
					}
					else
					{
						//Notifica l'utente che è andata a buon fine :(
						order.SetPrice(result.GetPrice());
						Notify.Send(order, true);
						History.SaveOrder(order);
					}

					//In entrambi i casi si rimuove lo stop order
					removedStops.add(order);
				}
			}

			stopOrdersAsk.removeAll(removedStops);
		}
	}

	public static Order Bid(LimitOrderRequest limitRequest)
	{
		//Se è un Buy/Bid
		if(limitRequest.GetType() == OrderKind.ASK)
			throw new UnexpectedOrderException("To make a Bid you need a Bid-Request.");

		long coinsToSell = 0;
		synchronized (limitOrdersAsk)
		{
			//Trovo l'offerta di vendita migliore -> lato Ask
			Order order = limitOrdersAsk.peek();

			//Teniamo traccia di quanto vogliamo vendere/quanto ci rimarrà da vendere(potenzialmente)
			coinsToSell = limitRequest.GetSize();

			//Continua a pescare ordini finchè o finiscono o non riescono più a soddisfare l'offerta di acquisto.
			while(order != null && (order.GetPrice() <= limitRequest.GetPrice()) && coinsToSell > 0)
			{
				long amountSold = order.TrySell(coinsToSell);
				coinsToSell -= amountSold;

				//Se soddisfatto salva l'ordine
				if (order.GetSize() == 0) {
					order.SetSize(amountSold);
					Notify.Send(order);
					History.SaveOrder(order);
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
			Order finalOrder = Order.Limit(OrderKind.BID, limitRequest.GetSize(), limitRequest.GetPrice(), "");
			History.SaveOrder(finalOrder);

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

		long price = 0;
		synchronized (limitOrdersAsk)
		{
			if(limitOrdersAsk.isEmpty())
				return Order.Null(OrderKind.BID);

			//Trovo l'offerta di vendita migliore -> lato Ask
			PriorityQueue<Order> orders = new PriorityQueue<>(limitOrdersAsk);
			Order order = orders.poll();

			//Teniamo traccia di quanto vogliamo vendere
			long coinsToSell = marketRequest.GetSize();
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
				long amountSold = order.TrySell(coinsToSell);

				coinsToSell -= amountSold;

				//Se soddisfatto salva l'ordine
				if (order.GetSize() == 0)
				{
					order.SetSize(amountSold);
					Notify.Send(order);
					History.SaveOrder(order);
					limitOrdersAsk.poll(); //Rimuovi se l'ordine è soddisfatto.
				}

				order = limitOrdersAsk.peek();
			}
		}

		//A questo punto so che il marketOrder è fattibile, quindi lo creo, lo salvo e lo resitutisco.
		Order finalOrder = Order.Market(OrderKind.BID, marketRequest.GetSize(), price, "");
		History.SaveOrder(finalOrder);

		return finalOrder;
	}

	public static Order Bid(StopOrderRequest stopRequest)
	{
		if(stopRequest.GetType() == OrderKind.ASK)
			throw new UnexpectedOrderException("To make a Bid you need a Bid-Request.");

		synchronized (stopOrdersBid)
		{
			Order newOrder = Order.Stop(OrderKind.BID, stopRequest.GetSize(), stopRequest.GetPrice(), stopRequest.GetOwner());
			stopOrdersBid.add(newOrder);

			return newOrder;
		}
	}

	public static Order Ask(LimitOrderRequest limitRequest)
	{
		//Se è un Buy/Bid
		if(limitRequest.GetType() == OrderKind.BID)
			throw new UnexpectedOrderException("To make an Ask you need a Ask-Request.");

		long coinsToSell = 0;
		synchronized (limitOrdersBid)
		{
			//Trovo l'offerta di vendita migliore -> lato Ask
			Order order = limitOrdersBid.peek();

			coinsToSell = limitRequest.GetSize();

			//Continua a pescare ordini finchè o finiscono o non riescono più a soddisfare l'offerta di acquisto.
			while (order != null && (order.GetPrice() >= limitRequest.GetPrice()) && coinsToSell > 0)
			{
				long amountSold = order.TrySell(coinsToSell);

				coinsToSell -= amountSold;

				//Se soddisfatto salva l'ordine
				if (order.GetSize() == 0)
				{
					order.SetSize(amountSold);
					Notify.Send(order);
					History.SaveOrder(order);
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
			Order finalOrder = Order.Limit(OrderKind.ASK, limitRequest.GetSize(), limitRequest.GetPrice(), "");
			History.SaveOrder(finalOrder);
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

		long price = 0;
		synchronized (limitOrdersBid)
		{
			if(limitOrdersBid.isEmpty())
				return Order.Null(OrderKind.ASK);

			//Trovo l'offerta di acquisto migliore -> lato Bid
			PriorityQueue<Order> orders = new PriorityQueue<>(limitOrdersBid);
			Order order = orders.poll();

			//Teniamo traccia di quanto vogliamo vendere
			long coinsToSell = marketRequest.GetSize();
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
				long amountSold = order.TrySell(coinsToSell);

				coinsToSell -= amountSold;

				//Se soddisfatto salva l'ordine
				if (order.GetSize() == 0)
				{
					order.SetSize(amountSold);
					Notify.Send(order);
					History.SaveOrder(order);
					limitOrdersBid.poll(); //Rimuovi se l'ordine è soddisfatto.
				}

				order = limitOrdersBid.peek();
			}
		}

		//A questo punto so che il marketOrder è fattibile, quindi lo creo, lo salvo e lo resitutisco.
		Order finalOrder = Order.Market(OrderKind.ASK, marketRequest.GetSize(), price, "");
		History.SaveOrder(finalOrder);

		return finalOrder;
	}

	public static Order Ask(StopOrderRequest stopRequest)
	{
		if(stopRequest.GetType() == OrderKind.BID)
			throw new UnexpectedOrderException("To make an Ask you need a Ask-Request.");

		synchronized (stopOrdersAsk)
		{
			Order newOrder = Order.Stop(OrderKind.ASK, stopRequest.GetSize(), stopRequest.GetPrice(), stopRequest.GetOwner());
			stopOrdersAsk.add(newOrder);

			return newOrder;
		}
	}

	public static synchronized boolean TryCancel(int orderId)
	{
		return  limitOrdersBid.removeIf(order -> order.GetOrderID() == orderId) ||
				limitOrdersAsk.removeIf(order -> order.GetOrderID() == orderId) ||
				stopOrdersAsk.removeIf(order -> order.GetOrderID() == orderId)  ||
				stopOrdersBid.removeIf(order -> order.GetOrderID() == orderId);
	}
}
