import Exchange.OrderBook;
import Exchange.History;
import Systems.Users;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerManager extends Thread
{
	public static AtomicInteger openConnectionsAmount = new AtomicInteger(0);

	private boolean stop;

	@Override
	public void run()
	{
		long lastUserSave = System.currentTimeMillis();
		long lastHistorySave = System.currentTimeMillis();

		stop = false;
		while(!stop)
		{
			OrderBook.TryCompleteStops();

			long currentMs = System.currentTimeMillis();

			//Automatic save of user data
			if(currentMs - lastUserSave > ServerConfigs.USER_AUTOMATIC_SAVE)
			{
				Users.Save();
				lastUserSave = currentMs;
			}

			//Automatic save of order book data
			if(currentMs - lastHistorySave > ServerConfigs.HISTORY_AUTOMATIC_SAVE)
			{
				History.Save();
				lastHistorySave	= currentMs;
			}
		}
	}

	public synchronized void Stop()
	{
		stop = true;
	}
}
