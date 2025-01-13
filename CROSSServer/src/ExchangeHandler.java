import Exchange.OrderBook;

public class ExchangeHandler extends Thread
{
	@Override
	public void run()
	{
		while(true)
		{
			OrderBook.TryCompleteStops();
		}
	}
}
