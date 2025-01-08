import Messages.Requests.ExitRequest;
import Messages.Requests.LogoutRequest;

import java.io.IOException;

public class LogoutCommand extends CrossCommand
{
	@Override
	public void Execute(Connection connection, String[] args) throws IOException
	{
		if(args.length != 0)
		{
			System.out.println("Invalid number of argument. Usage: " + CmdUsage());
			return;
		}

		System.out.println("Logging out...");
		connection.SendMessage(new LogoutRequest().ToMessage());
	}

	@Override
	public String CmdUsage()
	{
		return "logout";
	}
}
