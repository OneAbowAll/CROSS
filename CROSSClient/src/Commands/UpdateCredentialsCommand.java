package Commands;

import Messages.Requests.UpdateCredentialsRequest;
import Systems.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class UpdateCredentialsCommand extends CrossCommand
{
	@Override
	public void Execute(Connection connection, String[] args) throws IOException, TimeoutException
	{
		if(args.length != 3)
		{
			System.out.println("Invalid number of arguments. Usage: " + CmdUsage());
			return;
		}

		UpdateCredentialsRequest request = new UpdateCredentialsRequest(args[0], args[1], args[2]);

		connection.SendMessage(request);
	}

	@Override
	public String CmdUsage()
	{
		return "updateCredentials <username> <old_password> <new_password>";
	}
}
