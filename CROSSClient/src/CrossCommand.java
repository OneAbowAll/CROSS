import Messages.Message;

import java.io.IOException;

public abstract class CrossCommand
{
	public abstract void Execute(Connection connection, String[] args) throws IOException;

	public abstract String CmdUsage();
}
