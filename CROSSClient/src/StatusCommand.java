import Messages.Deserializer;
import Messages.Message;
import Messages.Requests.LoginRequest;
import Messages.Requests.StatusRequest;
import Messages.Responses.LoginResponse;
import Messages.Responses.StatusResponse;

import java.io.IOException;

public class StatusCommand extends CrossCommand
{
    @Override
    public void Execute(Connection connection, String[] args) throws IOException
    {
        if(args.length != 0)
        {
            System.out.println("Invalid number of argument. Usage: " + CmdUsage());
            return;
        }

        StatusRequest request = new StatusRequest();

        Message msg = connection.SendAndWait(request.ToMessage());
        StatusResponse response = Deserializer.ToStatusResponse(msg);

        System.out.println(response.GetErrorMessage());
    }

    @Override
    public String CmdUsage()
    {
        return "status";
    }
}
