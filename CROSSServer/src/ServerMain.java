import Messages.Responses.HistoryResponse;
import Systems.GlobalConfigs;
import Systems.Notify;
import Systems.Users;

import Exchange.History;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ServerMain
{
	static ServerSocket acceptSocket;

	public static void main(String[] args)
	{
		Notify.Startup();

		//Apri connessione al mondo
		System.out.println("▩▩▩▩▩▩ CROSS Server ▩▩▩▩▩▩");
		System.out.println("Waiting for CROSS Clients...");
		try {
			acceptSocket = new ServerSocket(GlobalConfigs.CMD_PORT);
			acceptSocket.setSoTimeout(ServerConfigs.ACCEPT_TIMEOUT);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}

		//Clients Thread pool
		ExecutorService clientPool = Executors.newCachedThreadPool();
		ServerManager serverManager = new ServerManager();
		serverManager.start();

		while(!acceptSocket.isClosed())
		{
			try {
				Socket clientSocket = acceptSocket.accept();
				clientPool.submit(new ClientHandler(clientSocket));

				System.out.println("New client accepted! Currently connected clients: "+ ServerManager.openConnectionsAmount.get() + 1);
			}
			catch (IOException e) //Scattato il timeout
			{
				//Se c'è ancora qualche utente loggato continua a girare
				if(ServerManager.openConnectionsAmount.get() > 0) continue;

				//In alternativa esci
				System.out.println("No activity detected. Initializing automatic server shutdown...");
				break;
			}
		}

		try {
			acceptSocket.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		System.out.println("Closing clients thread-pool...");
		clientPool.shutdown();
		try { clientPool.awaitTermination(1, TimeUnit.MINUTES);}
		catch (InterruptedException e) { throw new RuntimeException(e);	}

		System.out.println("Closing ServerManager-Thread...");
		serverManager.Stop();
		try { serverManager.join();	}
		catch (InterruptedException e) { System.out.println("Error while trying to close ServerManager-Thread.");	}

		System.out.println("Saving system's data...");
		Users.Save();
		History.Save();

		System.out.println("Server closed.");
	}
}
