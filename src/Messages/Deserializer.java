package Messages;
import Messages.Responses.LoginResponse;
import Messages.Responses.RegisterResponse;
import Messages.Responses.UpdateCredentialsResponse;
import com.google.gson.Gson;

import Messages.Requests.*;
import Exceptions.UnexpectedRequestException;

public class Deserializer
{
	static Gson gson;

	static
	{
		gson = new Gson();
	}

	//Requests ---------------------------------------------------------------------------------------------------------
	public static LoginRequest ToLoginRequest(Message message)
	{
		if (message.GetType() != OperationType.LOGIN)
			throw new UnexpectedRequestException("The message is not a Login request");

		return gson.fromJson(message.GetData(), LoginRequest.class);
	}

	public static RegisterRequest ToRegisterRequest(Message message)
	{
		if (message.GetType() != OperationType.REGISTER)
			throw new UnexpectedRequestException("The message is not a Register request");

		return gson.fromJson(message.GetData(), RegisterRequest.class);
	}

	public static UpdateCredentialsRequest ToUpdateCredentialsRequest(Message message)
	{
		if (message.GetType() != OperationType.UPDATE_CREDENTIALS)
			throw new UnexpectedRequestException("The message is not an UpdateCredentials request");

		return gson.fromJson(message.GetData(), UpdateCredentialsRequest.class);
	}

	//Responses --------------------------------------------------------------------------------------------------------
	public static LoginResponse ToLoginResponse(Message message)
	{
		if(message.GetType() != OperationType.LOGIN)
			throw new UnexpectedRequestException("The message is not a Login response");

		return gson.fromJson(message.GetData(), LoginResponse.class);
	}

	public static RegisterResponse ToRegisterResponse(Message message)
	{
		if(message.GetType() != OperationType.REGISTER)
			throw new UnexpectedRequestException("The message is not a Register response");

		return gson.fromJson(message.GetData(), RegisterResponse.class);
	}

	public static UpdateCredentialsResponse ToUpdateCredentialsResponse(Message message)
	{
		if(message.GetType() != OperationType.UPDATE_CREDENTIALS)
			throw new UnexpectedRequestException("The message is not a UpdateCredentials response");

		return gson.fromJson(message.GetData(), UpdateCredentialsResponse.class);
	}
}
