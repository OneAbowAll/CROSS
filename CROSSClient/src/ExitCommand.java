import Messages.Message;
import Messages.Requests.ExitRequest;

import java.io.IOException;

public class ExitCommand extends CrossCommand
{
	@Override
	public void Execute(Connection connection, String[] args) throws IOException
	{
		if(args.length != 0)
		{
			System.out.println("Invalid number of argument. Usage: " + CmdUsage());
			return;
		}

		System.out.println("Disconnecting from server...");
		connection.SendMessage(new ExitRequest().ToMessage());

		try { connection.Close(); }
		catch (IOException e) { throw new RuntimeException(e); }
	}

	@Override
	public String CmdUsage()
	{
		return "exit";
	}
}
