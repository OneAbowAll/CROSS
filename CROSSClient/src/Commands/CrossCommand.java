package Commands;

import Systems.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public abstract class CrossCommand
{
	public abstract void Execute(Connection connection, String[] args) throws IOException, TimeoutException;

	public abstract String CmdUsage();
}
