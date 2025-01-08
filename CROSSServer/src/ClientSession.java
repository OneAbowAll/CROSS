import Messages.Requests.*;
import Messages.Responses.*;

public class ClientSession
{
	private volatile User user;

	public ClientSession()
	{
	}

	public LoginResponse TryLogin(LoginRequest logReq)
	{
		//Se per questa sessione è già stato fatto un login resitutisci un error-103
		if(user != null) { return new LoginResponse(103); }

		User foundUser = UsersManager.Find(logReq.GetUsername());
		if(foundUser == null) { return new LoginResponse(101); }

		if(foundUser.IsConnected()) {	return new LoginResponse(102); }

		//Se è stato trovato un utente e la password combacia conferma il login per la sessione
		if(foundUser.GetPassword().equals(logReq.GetPassword()))
		{
			user = foundUser;
			user.SetConnected(true);

			return new LoginResponse(100);
		}
		else
		{
			return new LoginResponse(101);
		}
	}

	public RegisterResponse TryRegister(RegisterRequest regReq)
	{
		//Se per questa sessione è già stato fatto un login resitutisci un error-102
		if(user != null) { return new RegisterResponse(102); }

		boolean success = UsersManager.TryRegisterUser(regReq.GetUsername(), regReq.GetPassword());
		if(!success) { return new RegisterResponse(101); }

		user = UsersManager.Find(regReq.GetUsername());
		user.SetConnected(true);
		return new RegisterResponse(100);
	}

	public UpdateCredentialsResponse TryUpdateCredentials(UpdateCredentialsRequest request)
	{
		User foundUser = UsersManager.Find(request.GetUsername());
		if(foundUser == null) { return new UpdateCredentialsResponse(102); }

		synchronized (foundUser)
		{
			if(foundUser.IsConnected())
				return new UpdateCredentialsResponse(104);

			//Se la password passata non combacia con quella attuale
			if(!request.GetOldPassword().equals(foundUser.GetPassword()))
				return  new UpdateCredentialsResponse(102);

			//Se è uguale a quella vecchia
			if(request.GetNewPassword().equals(foundUser.GetPassword()))
				return new UpdateCredentialsResponse(103);

			//Se la password è vuota non è valida
			if(request.GetNewPassword().isBlank())
				return new UpdateCredentialsResponse(101);

			//Cambia password e conferma il cambiamento
			foundUser.SetPassword(request.GetNewPassword());
		}

		return new UpdateCredentialsResponse(100);
	}

	public void Logout()
	{
		System.out.println("User "+ user.GetUsername() +" logged out.");

		user.SetConnected(false);
		user = null;
	}

	@Override
	public String toString()
	{
		return "CrossSession{" +
				"user=" + ((user == null)?"null":user.toString()) +
				'}';
	}
}
