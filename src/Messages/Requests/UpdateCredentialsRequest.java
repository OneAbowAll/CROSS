package Messages.Requests;

import Messages.OperationType;

public class UpdateCredentialsRequest extends Request
{
	private String username;
	private String oldPassword;
	private String newPassword;

	private UpdateCredentialsRequest()
	{
		super(OperationType.UPDATE_CREDENTIALS);
	}

	public UpdateCredentialsRequest(String username, String oldPassword, String newPassword)
	{
		this();
		this.username = username;
		this.oldPassword = oldPassword;
		this.newPassword = newPassword;
	}

	public String GetUsername()
	{
		return username;
	}

	public String GetOldPassword()
	{
		return oldPassword;
	}

	public String GetNewPassword()
	{
		return newPassword;
	}

	@Override
	public String toString()
	{
		return "UpdateCredentialsRequest{" +
				"username='" + username + '\'' +
				", oldPassword='" + oldPassword + '\'' +
				", newPassword='" + newPassword + '\'' +
				'}';
	}
}
