package Exceptions;

public class UnexpectedOrderException extends RuntimeException
{
	public UnexpectedOrderException(String message)
	{
		super(message);
	}
}
