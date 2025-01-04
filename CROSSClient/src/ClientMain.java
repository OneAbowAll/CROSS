import Messages.Message;
import Messages.Requests.*;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

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
		Scanner scanner = new Scanner(System.in);

		System.out.println("Mi sono connesso ( •̀ .̫ •́ )✧)");
		while(true)
		{
            try
            {
				String input = scanner.nextLine();

				if(input.equalsIgnoreCase("login"))
				{
					cmdConnection.SendMessage(new LoginRequest("dado", "123"));
					Message responseMsg = cmdConnection.WaitMessage();
					System.out.println(responseMsg.toString());
				}

				if(input.equalsIgnoreCase("register"))
				{
					cmdConnection.SendMessage(new RegisterRequest("dado", "123"));
					Message responseMsg = cmdConnection.WaitMessage();
					System.out.println(responseMsg.toString());
				}

				if(input.equalsIgnoreCase("exit"))
				{
					cmdConnection.SendMessage(new LogoutRequest());
					System.out.println("Disconnecting from server...");

					break;
				}

            }
			catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }

		//Close connection
        try
        {
			cmdConnection.Close();
			System.out.println("Successfully logged out.");
        } catch (IOException e)
        {
            throw new RuntimeException("Something went wrong while logging out: "+ e);
        }

	}
}