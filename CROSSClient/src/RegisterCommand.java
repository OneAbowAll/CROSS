import Messages.Deserializer;
import Messages.Message;
import Messages.Requests.RegisterRequest;
import Messages.Responses.RegisterResponse;

import java.io.IOException;

public class RegisterCommand extends CrossCommand
{
	@Override
	public void Execute(Connection connection, String[] args) throws IOException
	{
		if(args.length != 2)
		{
			System.out.println("Invalid number of argument. Usage: " + CmdUsage());
			return;
		}

		RegisterRequest request = new RegisterRequest(args[0], args[1]);

		Message msg = connection.SendAndWait(request.ToMessage());
		RegisterResponse response = Deserializer.ToRegisterResponse(msg);

		System.out.println(response.GetErrorMessage());
	}

	@Override
	public String CmdUsage()
	{
		return "register <username> <password>";
	}
}
