import Messages.Deserializer;
import Messages.Message;
import Messages.Requests.LoginRequest;
import Messages.Requests.RegisterRequest;
import Messages.Responses.LoginResponse;

import java.io.IOException;

public class LoginCommand extends CrossCommand
{
	@Override
	public void Execute(Connection connection, String[] args) throws IOException
	{
		if(args.length != 2)
		{
			System.out.println("Invalid number of argument. Usage: " + CmdUsage());
			return;
		}

		LoginRequest request = new LoginRequest(args[0], args[1]);

		Message msg = connection.SendAndWait(request.ToMessage());
		LoginResponse response = Deserializer.ToLoginResponse(msg);

		System.out.println(response.GetErrorMessage());
	}

	@Override
	public String CmdUsage()
	{
		return "login <username> <password>";
	}
}
