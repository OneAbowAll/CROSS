package Messages.Responses;

import Messages.OperationType;

import java.util.HashMap;

public class ResponseInfo
{
    static HashMap<Integer, String> loginResponses = new HashMap<>()
    {{
        put(100, "Login successful");
        put(101, "User doesn't exist or password is incorrect");
        put(102, "User is already logged in");
        put(103, "Unknown error while trying to login");
    }};

    static HashMap<Integer, String> registerResponses = new HashMap<>()
    {{
        put(100, "Registration successful");
        put(101, "Username is already taken");
        put(102, "Unknown error while trying to register");
    }};

    static HashMap<Integer, String> updateCredentialsResponses = new HashMap<>()
    {{
        put(100, "The users credentials were successfully updated");
        put(101, "Invalid new password");
        put(102, "User doesn't exist or old password is incorrect");
        put(103, "New password matches the old password");
        put(104, "User is currently logged in");
        put(105, "Unknown error while trying to update credentials");
    }};

    public static String GetMeaning(OperationType type, int response)
    {
        switch (type)
        {
            case LOGIN ->
            {
                return loginResponses.get(response);
            }

            case REGISTER ->
            {
                return registerResponses.get(response);
            }

            case UPDATE_CREDENTIALS ->
            {
                return updateCredentialsResponses.get(response);
            }
            /*
            case MARKET_ORDER ->
            {
            }
            case LIMIT_ORDER ->
            {
            }
            case STOP_ORDER ->
            {
            }
            case PRICE_HISTORY ->
            {
            }
            case CANCEL_ORDER ->
            {
            }
            case LOGOUT ->
            {
            }*/
        }

        return "non ho trovato una ceppa";
    }
}
