package Exceptions;

public class UnexpectedRequestException extends RuntimeException
{
	public UnexpectedRequestException(String message)
	{
		super(message);
	}
}
