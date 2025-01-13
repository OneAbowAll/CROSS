package Commands;

import Systems.Connection;
import Messages.Requests.LoginRequest;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class LoginCommand extends CrossCommand
{
	@Override
	public void Execute(Connection connection, String[] args) throws IOException, TimeoutException
	{
		if(args.length != 2)
		{
			System.out.println("Invalid number of arguments. Usage: " + CmdUsage());
			return;
		}

		LoginRequest request = new LoginRequest(args[0], args[1]);
		connection.SendMessage(request);
	}

	@Override
	public String CmdUsage()
	{
		return "login <username> <password>";
	}
}
