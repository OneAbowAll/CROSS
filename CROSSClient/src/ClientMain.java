import java.io.IOException;
import java.net.Socket;

public class ClientMain

{
	static Socket cmd_socket;

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

		System.out.println("Mi sono connesso ( •̀ .̫ •́ )✧)");
		while(true)
		{
            try
            {
                cmdConnection.SendRequest(new Request(110, "LOGIN REQUEST"));
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
	}
}