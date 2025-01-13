package Commands;

import Messages.Requests.RegisterRequest;
import Systems.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RegisterCommand extends CrossCommand
{
	@Override
	public void Execute(Connection connection, String[] args) throws IOException, TimeoutException
	{
		if(args.length != 2)
		{
			System.out.println("Invalid number of arguments. Usage: " + CmdUsage());
			return;
		}

		RegisterRequest request = new RegisterRequest(args[0], args[1]);
		connection.SendMessage(request);
	}

	@Override
	public String CmdUsage()
	{
		return "register <username> <password>";
	}
}
