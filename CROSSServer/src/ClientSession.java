import Messages.Requests.*;
import Messages.Responses.*;
import Systems.User;
import Systems.Users;

public class ClientSession
{
	private User user;

	public ClientSession()
	{
	}

	public User GetUser()
	{
		return user;
	}

	public LoginResponse TryLogin(LoginRequest logReq)
	{
		//Se per questa sessione è già stato fatto un login resitutisci un error-103
		if(user != null) { return new LoginResponse(103); }

		User foundUser = Users.Find(logReq.GetUsername());
		if(foundUser == null) { return new LoginResponse(101); }

		//Se è stato trovato un utente e la password combacia conferma il login per la sessione
		if(foundUser.GetPassword().equals(logReq.GetPassword()))
		{
			user = foundUser;
			if(!user.TryConnect())
				return new LoginResponse(102);

			System.out.println("User "+ user.GetUsername() +" logged in.");
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

		boolean success = Users.TryRegisterUser(regReq.GetUsername(), regReq.GetPassword());
		if(!success) { return new RegisterResponse(101); }

		user = Users.Find(regReq.GetUsername());
		user.SetConnected(true);
		return new RegisterResponse(100);
	}

	public UpdateCredentialsResponse TryUpdateCredentials(UpdateCredentialsRequest request)
	{
		User foundUser = Users.Find(request.GetUsername());
		if(foundUser == null) { return new UpdateCredentialsResponse(102); }

		return new UpdateCredentialsResponse(foundUser.TryChangePassword(request.GetOldPassword(), request.GetNewPassword()));
	}

	public void Logout()
	{
		if(user == null)
		{
			return;
		}

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
