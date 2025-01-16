package Commands;

import Messages.Requests.ExitRequest;
import Systems.Connection;

import java.io.IOException;

public class ExitCommand extends CrossCommand
{
	@Override
	public void Execute(Connection connection, String[] args) throws IOException
	{
		if(args.length != 0)
		{
			System.out.println("Invalid number of arguments. Usage: " + CmdUsage());
			return;
		}

		System.out.println("Disconnecting from server...");
		connection.SendMessage(new ExitRequest().ToMessage());

		connection.TryClose();
	}

	@Override
	public String CmdUsage()
	{
		return "exit";
	}
}
