package Commands;

import Exchange.OrderKind;
import Messages.Requests.StopOrderRequest;
import Systems.Connection;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

public class StopOrderCommand extends CrossCommand
{
	@Override
	public void Execute(Connection connection, String[] args) throws IOException, TimeoutException
	{
		if(args.length != 3)
		{
			System.out.println("Invalid number of arguments. Usage: " + CmdUsage());
			return;
		}

		if(!args[0].equals("bid") && !args[0].equals("ask") && !args[0].equals("buy") && !args[0].equals("sell"))
		{
			System.out.println("Invalid order type. Usage: " + CmdUsage());
			return;
		}

		//Cosa stupida ma utile
		if(args[0].equals("buy")) { args[0] = "bid"; }
		if(args[0].equals("sell")) { args[0] = "ask"; }

		StopOrderRequest request = new StopOrderRequest(Objects.requireNonNull(OrderKind.Get(args[0])),
				Long.parseLong(args[1]),
				Long.parseLong(args[2]),
				"");

		connection.SendMessage(request);
	}

	@Override
	public String CmdUsage() { return "insertStopOrder <type: {bid/ask, buy/sell}> <size> <stop_price>"; }
}
