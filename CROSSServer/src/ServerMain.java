import Exchange.History;
import Exchange.OrderBook;

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
		OrderBook orderBook = new OrderBook();
		orderBook.TestPrint();

		//Apri connessione al mondo
		System.out.println("Ciao sono il server ヾ(•ω•`)o");
		System.out.println("Ora mi metto ad ascoltare... ᕦ(ò_óˇ)ᕤ");
		try {
			acceptSocket = new ServerSocket(GlobalConfigs.CMD_PORT);
			acceptSocket.setSoTimeout(ServerConfigs.ACCEPT_TIMEOUT);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}

		//Clients Thread pool
		ExecutorService clientPool = Executors.newCachedThreadPool();
		while(!acceptSocket.isClosed())
		{
			try {
				Socket clientSocket = acceptSocket.accept();
				System.out.println("Ne ho trovato uno (～￣▽￣)～");

				clientPool.submit(new ClientHandler(clientSocket));

			}
			catch (IOException e) {
				break;
			}
		}

		clientPool.shutdown();
		try { clientPool.awaitTermination(1, TimeUnit.MINUTES);}
		catch (InterruptedException e) { throw new RuntimeException(e);	}

		UsersManager.Save();
	}
}
