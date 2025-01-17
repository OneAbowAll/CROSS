package Commands;

import Exchange.OrderKind;
import Messages.Requests.MarketOrderRequest;
import Systems.Connection;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

public class MarketOrderCommand extends CrossCommand
{
    @Override
    public void Execute(Connection connection, String[] args) throws IOException, TimeoutException
	{
        if(args.length != 2)
        {
            System.out.println("Invalid number of arguments. Usage: " + CmdUsage());
            return;
        }

        if(!args[0].equals("bid") && !args[0].equals("ask") && !args[0].equals("buy") && !args[0].equals("sell"))
        {
            System.out.println("Invalid order type. Usage: " + CmdUsage());
            return;
        }

        //Cosa stupida ma utile
        if(args[0].equals("buy")) { args[0] = "bid"; }
        if(args[0].equals("sell")) { args[0] = "ask"; }

        MarketOrderRequest request = new MarketOrderRequest(Objects.requireNonNull(OrderKind.Get(args[0])),
                                                            Long.parseLong(args[1]),
                                                            "");

        connection.SendMessage(request);
    }

    @Override
    public String CmdUsage() { return "insertMarketOrder <type: {bid/ask, buy/sell}> <size>"; }
}
