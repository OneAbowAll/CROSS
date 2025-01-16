package Systems;

import Messages.CrossMessage;
import Messages.Message;
import Messages.OperationType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeoutException;

public class Connection
{
    private Socket socket;

    private final DataInputStream input;
    private final DataOutputStream output;

    private boolean close;

    private long lastActionTime; //Last time someone did something using this connection (in milliseconds)
    private long maxTimeout;

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

        close = false;
        maxTimeout = 0;
        RefreshTimeout();
    }

    synchronized void RefreshTimeout()
    {
        lastActionTime = System.currentTimeMillis();
    }

    public void SendMessage(CrossMessage request) throws IOException
    {
        SendMessage(request.ToMessage());
    }

    public void SendMessage(Message message) throws IOException
    {
        output.writeInt(message.GetType().GetValue());
        output.writeUTF(message.GetData());

        RefreshTimeout();
    }

    public Message WaitMessage() throws IOException, TimeoutException
	{
        while(!close && input.available() <= 0 && maxTimeout != 0)
        {
            if(System.currentTimeMillis() - lastActionTime > maxTimeout)
            {
                throw new TimeoutException();
            }
        }

        Message received = new Message(OperationType.Get(input.readInt()), input.readUTF());
        RefreshTimeout();
        return received;
    }

    //Really simple shorthand for what will probably be a very common sequence of commands.
    public Message SendAndWait(Message message) throws IOException, TimeoutException
	{
        SendMessage(message);
        return WaitMessage();
    }

    public synchronized boolean IsClosed()
    {
        return close || socket.isOutputShutdown() || socket.isInputShutdown();
    }

    public synchronized boolean TryClose()
    {
        if(close) { return false; }

        close = true;

		try {
			socket.close();
            return true;
		}
        catch (IOException e) {
            return false;
		}
	}

    /**
     * @param maxInactivityTime If provided value>0 a call to WaitMessage(...) will block for only the specified amount of time(in milliseconds).
     *                          if value=0 a call to WaitMessage(...) will block the thread indefinitely.
     */
    public void SetTimeout(int maxInactivityTime)
    {
        maxTimeout = Math.max(maxInactivityTime, 0);
    }

    public Socket GetSocket(){ return socket; }

}
