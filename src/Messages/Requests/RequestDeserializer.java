package Messages.Requests;

import Exceptions.UnexpectedRequestException;
import Messages.Message;
import Messages.OperationType;
import com.google.gson.Gson;

public class RequestDeserializer
{
	//Deserialization methods ----------------------------------------------------------
	public static LoginRequest ToLoginRequest(Message message)
	{
		if(message.GetType() != OperationType.LOGIN)
			throw new UnexpectedRequestException("The message is not a Login request");

		Gson gson = new Gson();
		return gson.fromJson(message.GetData(), LoginRequest.class);
	}

	public static LoginRequest ToRegisterRequest(Message message) throws Exception
    {
		if(message.GetType() != OperationType.REGISTER)
			throw new UnexpectedRequestException("The message is not a Register request");

		Gson gson = new Gson();
		throw new Exception("TONNO, NON HAI CREATO ANCORA REGISTERREQUEST");
		//return gson.fromJson(message.GetData(), LoginRequest.class);
	}
}
