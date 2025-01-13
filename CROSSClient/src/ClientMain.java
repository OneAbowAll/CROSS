import Commands.*;
import Systems.Connection;
import Systems.GlobalConfigs;
import Systems.User;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class ClientMain

{
	static Socket cmd_socket;
	static HashMap<String, CrossCommand> commands;

	static
	{
		commands = new HashMap<>();
		commands.put("register", new RegisterCommand());
		commands.put("login", new LoginCommand());
		commands.put("updateCredentials", new UpdateCredentialsCommand());
		commands.put("market", new MarketOrderCommand());
		commands.put("limit", new LimitOrderCommand());
		commands.put("stop", new StopOrderCommand());
		commands.put("cancel", new CancelCommand());
		commands.put("history", new HistoryCommand());
		commands.put("status", new StatusCommand());
		commands.put("logout", new LogoutCommand());
		commands.put("exit", new ExitCommand());
		commands.put("help", new HelpCommand(commands));
	}

	public static void main(String[] args)
	{
		System.out.println("Ciao sono il client ヾ(≧▽≦*)o");
		System.out.println("Provo a connettermi (⊙_⊙)？");
		do
		{
			try {
				cmd_socket = new Socket(GlobalConfigs.SERVER_IP, GlobalConfigs.CMD_PORT);
			} catch (IOException e) {
				continue;
			}
			break;
		}while(true);


		Connection cmdConnection = new Connection(cmd_socket);
		cmdConnection.SetTimeout(0);

		ResponseListener listener = new ResponseListener(cmdConnection);
		listener.start();

		Scanner scanner = new Scanner(System.in);

		System.out.println("Mi sono connesso ( •̀ .̫ •́ )✧)");

		while(!cmdConnection.IsClosed())
		{
			String input = scanner.nextLine();

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
			catch (IOException e){ break; }
			catch (TimeoutException _){ continue; }
		}

		//Close connection
        try
        {
			cmdConnection.Close();
			System.out.println("Systems.Connection closed.");
        } catch (IOException e)
        {
            throw new RuntimeException("Something went wrong while closing connection: "+ e);
        }

	}
}