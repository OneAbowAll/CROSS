import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class UsersManager
{
    static final String USERS_FILE = "users.json";
    static HashMap<String, User> users;

    static
    {
        if(!Files.exists(Path.of(USERS_FILE)))
        {
            users = new HashMap<>();
        }
        else
        {
            //Load in the hashMap the json file :)
            Load();
        }
    }

    public static void RegisterUser(String username, String password)
    {
        User user = new User(username, password);
        users.put(user.GetUsername(), user);
    }

    public static User Find(String username)
    {
        return users.get(username);
    }

    public static boolean Exists(String username)
    {
        return users.containsKey(username);
    }

    public static void Save()
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try(FileWriter fw = new FileWriter(USERS_FILE);
            BufferedWriter bw = new BufferedWriter(fw))
        {

            String usersJson = gson.toJson(users);
            bw.write(usersJson);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void Load()
    {
        Gson gson = new Gson();
        try(FileReader fr = new FileReader(USERS_FILE);
            BufferedReader br = new BufferedReader(fr))
        {
           users = gson.fromJson(br, new TypeToken<HashMap<String, User>>(){});
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
