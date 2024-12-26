import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain
{
	static ServerSocket acceptSocket;

	public static void main(String[] args)
	{
		System.out.println("Ciao sono il server ヾ(•ω•`)o");
		System.out.println("Ora mi metto ad ascoltare... ᕦ(ò_óˇ)ᕤ");
		try {
			acceptSocket = new ServerSocket(GlobalConfigs.CMD_PORT);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		while(true)
		{
			try {
				Socket clientSocket = acceptSocket.accept();
				System.out.println("Ne ho trovato uno (～￣▽￣)～");

			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
