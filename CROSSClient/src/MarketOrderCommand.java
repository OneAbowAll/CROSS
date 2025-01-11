import Exchange.OrderKind;
import Messages.Deserializer;
import Messages.Message;
import Messages.Requests.LoginRequest;
import Messages.Requests.MarketOrderRequest;
import Messages.Responses.LoginResponse;
import Messages.Responses.MarketOrderResponse;

import java.io.IOException;
import java.util.Objects;

public class MarketOrderCommand extends CrossCommand
{
    @Override
    public void Execute(Connection connection, String[] args) throws IOException
    {
        if(ClientMain.loggedInUser == null)
        {
            System.out.println("Before placing any kind of order you need to login/register.");
            return;
        }

        if(args.length != 2)
        {
            System.out.println("Invalid number of argument. Usage: " + CmdUsage());
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

        MarketOrderRequest request = new MarketOrderRequest(Objects.requireNonNull(OrderKind.Get(args[0])), Integer.parseInt(args[1]), "");

        Message msg = connection.SendAndWait(request.ToMessage());
        MarketOrderResponse response = Deserializer.ToMarketOrderResponse(msg);

        if(response.GetOrderID() == -1)
            System.out.println("The requested MarketOrder could not be completed. You could try placing a LimitOrder.");
        else
            System.out.println("The MarketOrder has been successfully completed. Your OrderId is => "+ response.GetOrderID());
    }

    @Override
    public String CmdUsage() { return "market <type: bid/ask, buy/sell}> <size>"; }
}
