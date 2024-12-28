import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Connection
{
    Socket socket;

    DataInputStream input;
    DataOutputStream output;

    public Connection(Socket socket)
    {
        this.socket = socket;
        try
        {
            this.input = new DataInputStream(socket.getInputStream());
            this.output = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void SendRequest(Request request) throws IOException
    {
        SendMessage(request);
    }

    public void SendResponse(Response response) throws IOException
    {
        SendMessage(response);
    }

    public void SendMessage(Message message) throws IOException
    {
        output.writeInt(message.GetCode());
        output.writeUTF(message.GetData());
    }

    public Request WaitRequest() throws IOException
    {
        return new Request(input.readInt(), input.readUTF());
    }

    public Response WaitResponse() throws IOException
    {
        return new Response(input.readInt(), input.readUTF());
    }

    public void Close() throws IOException
    {
        socket.close();
    }
}
