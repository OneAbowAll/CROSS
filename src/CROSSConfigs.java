import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class CROSSConfigs
{
	public static final String SERVER_IP = Get("server_ip");
	public static final int CMD_PORT = GetInt("cmd_port");

	static Properties crossConfig;

	static void CheckAndLoad()
	{
		if(crossConfig == null)
		{
			crossConfig = new Properties();
			File configFile = new File("cross.config");

			try {
				crossConfig.load(new FileReader(configFile));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	static String Get(String propName)
	{
		CheckAndLoad();

		return crossConfig.getProperty(propName);
	}

	static int GetInt(String propName)
	{
		return Integer.parseInt(Get(propName));
	}
}
