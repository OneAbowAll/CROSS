package Commands;

import Messages.Requests.HistoryRequest;
import Systems.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class HistoryCommand extends CrossCommand
{
    @Override
    public void Execute(Connection connection, String[] args) throws IOException, TimeoutException
	{
        if(args.length != 2)
        {
            System.out.println("Invalid number of arguments. Usage: " + CmdUsage());
            return;
        }

        if(args[0].length() != 2)
        {
            System.out.println("Please provide month number with double digit (e.g. July: 07).");
            return;
        }

        int month = Integer.parseInt(args[0]);
        if(month < 0 || month > 12)
        {
            System.out.println("Invalid month. Usage: " + CmdUsage());
            return;
        }

        HistoryRequest request = new HistoryRequest(args[0].concat(args[1]));
        connection.SendMessage(request);
    }

    @Override
    public String CmdUsage()
    {
        return "getPriceHistory <month: {01, 02, ..., 12}> <year>";
    }
}
