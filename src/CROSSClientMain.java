import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Properties;

public class CROSSClientMain
{
	static Socket cmd_socket;

	public static void main(String[] args)
	{
		System.out.println("Ciao sono il client ヾ(≧▽≦*)o");
		System.out.println("Provo a connettermi (⊙_⊙)？");
		do
		{
			try {
				cmd_socket = new Socket(CROSSConfigs.SERVER_IP, CROSSConfigs.CMD_PORT);
			} catch (IOException e) {
				continue;
			}
			break;
		}while(true);

		System.out.println("Mi sono connesso ( •̀ .̫ •́ )✧)");
		while(true)
		{

		}
	}
}