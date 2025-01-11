import Messages.Deserializer;
import Messages.Message;
import Messages.Requests.UpdateCredentialsRequest;
import Messages.Responses.UpdateCredentialsResponse;

import java.io.IOException;

public class UpdateCredentialsCommand extends CrossCommand
{
	@Override
	public void Execute(Connection connection, String[] args) throws IOException
	{
		if(args.length != 3)
		{
			System.out.println("Invalid number of argument. Usage: " + CmdUsage());
			return;
		}

		UpdateCredentialsRequest request = new UpdateCredentialsRequest(args[0], args[1], args[2]);

		Message msg = connection.SendAndWait(request.ToMessage());
		UpdateCredentialsResponse response = Deserializer.ToUpdateCredentialsResponse(msg);

		System.out.println(response.GetErrorMessage());
	}

	@Override
	public String CmdUsage()
	{
		return "updateCredentials <username> <old_password> <new_password>";
	}
}
