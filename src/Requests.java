import Exceptions.UnexpectedRequestException;
import com.google.gson.Gson;

public class Requests
{
	//Deserialization methods ----------------------------------------------------------

	public static LoginRequest ToLoginRequest(Message message)
	{
		if(message.code != RequestType.LOGIN.GetValue())
			throw new UnexpectedRequestException("The message is not a login request");

		Gson gson = new Gson();
		return gson.fromJson(message.GetData(), LoginRequest.class);
	}
}
