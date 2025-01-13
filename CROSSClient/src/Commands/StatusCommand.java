package Commands;

import Messages.Requests.StatusRequest;
import Systems.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class StatusCommand extends CrossCommand
{
    @Override
    public void Execute(Connection connection, String[] args) throws IOException, TimeoutException
	{
        if(args.length != 0)
        {
            System.out.println("Invalid number of arguments. Usage: " + CmdUsage());
            return;
        }

        System.out.println("This may take a couple of seconds...");
        StatusRequest request = new StatusRequest();

        connection.SendMessage(request);
    }

    @Override
    public String CmdUsage()
    {
        return "status";
    }
}
