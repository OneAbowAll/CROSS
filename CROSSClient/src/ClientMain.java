import Commands.*;
import Systems.Connection;
import Systems.GlobalConfigs;
import Systems.User;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class ClientMain

{
	static Socket cmd_socket;
	static HashMap<String, CrossCommand> commands;

	//Every order done in this client session
	static ArrayList<Integer> savedOrders;

	static
	{
		commands = new HashMap<>();
		commands.put("register", new RegisterCommand());
		commands.put("login", new LoginCommand());
		commands.put("updateCredentials", new UpdateCredentialsCommand());
		commands.put("insertMarketOrder", new MarketOrderCommand());
		commands.put("insertLimitOrder", new LimitOrderCommand());
		commands.put("insertStopOrder", new StopOrderCommand());
		commands.put("cancelOrder", new CancelCommand());
		commands.put("getPriceHistory", new HistoryCommand());
		commands.put("status", new StatusCommand());
		commands.put("logout", new LogoutCommand());
		commands.put("exit", new ExitCommand());
		commands.put("help", new HelpCommand(commands));

		savedOrders = new ArrayList<>();
	}

	public static void main(String[] args)
	{
		System.out.println("▩▩▩▩▩▩ CROSS Client ▩▩▩▩▩▩");
		System.out.println("Trying to connect to CROSS Server...");

		do
		{
			try
			{
				cmd_socket = new Socket(GlobalConfigs.SERVER_IP, GlobalConfigs.CMD_PORT);
			}
			catch (IOException e) { continue; }

			break;
		}while(true);


		Connection cmdConnection = new Connection(cmd_socket);
		cmdConnection.SetTimeout(0);

		ResponseListener listener = new ResponseListener(cmdConnection);
		listener.start();

		NotificationListener notificationListener = new NotificationListener(cmdConnection);
		notificationListener.start();

		Scanner scanner = new Scanner(System.in);

		System.out.println("CROSS Server was found!");
		System.out.println("For a list of available commands use help command.");

		while(!cmdConnection.IsClosed())
		{
			String input = scanner.nextLine();

			//La chiamata a scanner.NextLine è bloccante, è quindi possibile che nel mentre qualcuno abbia prova a chiudere la connesione.
			if(cmdConnection.IsClosed()) { break; }

			//Un comando avrà il seguente formato -> command arg1 arg2 ...
			String[] tokens = input.split(" ");

			CrossCommand cmd = commands.get(tokens[0]);
			if(cmd == null)
			{
				System.out.println("Unknown command: " + tokens[0]);
				continue;
			}

			String[] cmdArgs = Arrays.copyOfRange(tokens, 1, tokens.length);

			try { cmd.Execute(cmdConnection, cmdArgs); }
			catch (IOException e){
				System.out.println("[WARNING] Server is no longer reachable. Close application by pressing any key.");
				break;
			}
			catch (TimeoutException _){ continue; }
		}

		//Join dei thread aperti
		try
		{
			listener.join();
			System.out.println("ResponseListener-Thread has been closed.");
			notificationListener.interrupt();
			notificationListener.join();
			System.out.println("NotificationListener-Thread has been closed.");

		}
		catch (InterruptedException e) { throw new RuntimeException(e); }

		//Close connection
        cmdConnection.TryClose();
	}

	public synchronized static void SaveOrder(int orderId)
	{
		savedOrders.add(orderId);
	}

	public synchronized static boolean HasOrder(int orderId)
	{
		return savedOrders.contains(orderId);
	}
}