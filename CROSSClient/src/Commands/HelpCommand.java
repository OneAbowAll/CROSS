package Commands;

import Systems.Connection;

import java.io.IOException;
import java.util.HashMap;

public class HelpCommand extends CrossCommand
{
	private HashMap<String, CrossCommand> commands;

	public HelpCommand(HashMap<String, CrossCommand> commands)
	{
		this.commands = commands;
	}

	@Override
	public void Execute(Connection connection, String[] args) throws IOException
	{
		if(args.length == 0)
		{
			System.out.println("Command list:");
			for(String command : commands.keySet())
			{
				System.out.println("\t" + command);
			}

			return;
		}

		if(args.length > 1)
		{
			System.out.println("Invalid number of arguments. Usage: " + CmdUsage());
			return;
		}

		if(commands.get(args[0]) == null)
		{
			System.out.println("Invalid argument. Usage: " + CmdUsage());
			return;
		}

		System.out.println("Usage for " + args[0] +": " + commands.get(args[0]).CmdUsage());
	}

	@Override
	public String CmdUsage()
	{
		return "help <command_name>";
	}
}
