package Messages;
import Messages.Responses.*;
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

	public static MarketOrderRequest ToMarketOrderRequest(Message message)
	{
		if(message.GetType() != OperationType.MARKET_ORDER)
			throw new UnexpectedRequestException("The message is not a MarketOrder request");

		return gson.fromJson(message.GetData(), MarketOrderRequest.class);
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

	public static MarketOrderResponse ToMarketOrderResponse(Message message)
	{
		if(message.GetType() != OperationType.MARKET_ORDER)
			throw new UnexpectedRequestException("The message is not a MarketOrder response");

		return gson.fromJson(message.GetData(), MarketOrderResponse.class);
	}

	public static StatusResponse ToStatusResponse(Message message)
	{
		if(message.GetType() != OperationType.GET_STATUS)
			throw new UnexpectedRequestException("The message is not a Status response");

		return gson.fromJson(message.GetData(), StatusResponse.class);
	}
}
