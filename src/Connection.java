import Messages.CrossMessage;
import Messages.Message;
import Messages.OperationType;

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

    public void SendMessage(CrossMessage request) throws IOException
    {
        SendMessage(request.ToMessage());
    }

    public void SendMessage(Message message) throws IOException
    {
        output.writeInt(message.GetType().GetValue());
        output.writeUTF(message.GetData());
    }

    public Message WaitMessage() throws IOException
	{
        return new Message(OperationType.Get(input.readInt()), input.readUTF());
    }

    //Really simple shorthand for what will probably be a very common sequence of commands.
    public Message SendAndWait(Message message) throws IOException
    {
        SendMessage(message);
        return WaitMessage();
    }

    /*
    public void SendRequest(Messages.Requests.Request request) throws IOException
    {
        output.writeInt(request.GetCode());
        output.writeUTF(request.Serialize());
    }

    public void SendResponse(Response response) throws IOException
    {
        output.writeInt(response.GetCode());
       // output.writeUTF(response.GetCode());
    }

    public Messages.Requests.Request WaitRequest() throws IOException
    {
        return new Messages.Requests.Request(input.readInt(), input.readUTF());
    }

    public Response WaitResponse() throws IOException
    {
        return new Response(input.readInt(), input.readUTF());
    }
    */

    public void Close() throws IOException
    {
        socket.close();
    }
}
