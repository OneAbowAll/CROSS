import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ServerConfigs
{
	public static final int ACCEPT_TIMEOUT = GetInt("accept_timeout");
	public static final int CLIENT_MAX_TIMEOUT = GetInt("client_max_timeout");

	public static final int USER_AUTOMATIC_SAVE = GetInt("user_automatic_save");
	public static final int HISTORY_AUTOMATIC_SAVE = GetInt("history_automatic_save");

	static Properties serverConfig;

	static void CheckAndLoad()
	{
		if(serverConfig == null)
		{
			serverConfig = new Properties();
			File configFile = new File("server.config");

			try {
				serverConfig.load(new FileReader(configFile));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	static String Get(String propName)
	{
		CheckAndLoad();

		assert serverConfig != null;
		return serverConfig.getProperty(propName);
	}

	static int GetInt(String propName)
	{
		return Integer.parseInt(Get(propName));
	}
}
