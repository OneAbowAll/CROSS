package Commands;

import Messages.Requests.CancelRequest;
import Systems.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class CancelCommand extends CrossCommand
{
	@Override
	public void Execute(Connection connection, String[] args) throws IOException, TimeoutException
	{
		if(args.length != 1)
		{
			System.out.println("Invalid number of arguments. Usage: " + CmdUsage());
			return;
		}

		if(args[0].isBlank())
		{
			System.out.println("No order_id was provided.");
			return;
		}

		CancelRequest request = new CancelRequest(Integer.parseInt(args[0]));
		connection.SendMessage(request);
	}

	@Override
	public String CmdUsage(){ return "cancel <order_id>"; }
}
