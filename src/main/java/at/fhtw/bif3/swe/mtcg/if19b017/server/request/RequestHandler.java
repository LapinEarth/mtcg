package at.fhtw.bif3.swe.mtcg.if19b017.server.request;

import at.fhtw.bif3.swe.mtcg.if19b017.server.response.Response;
import at.fhtw.bif3.swe.mtcg.if19b017.server.response.ResponseHandler;
import java.io.*;
import java.net.Socket;

public class RequestHandler implements Runnable
{
    private Socket clientSocket;
    private PrintWriter output;
    private BufferedReader in;

    public RequestHandler(Socket clientSocket) throws IOException
    {
        this.clientSocket = clientSocket;
        this.in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
        this.output = new PrintWriter(this.clientSocket.getOutputStream(), true);
    }

    @Override
    public void run()
    {
        try
        {
            Response response;
            Request request = RequestBuilder.buildRequest(this.in);
            if (request.getPathname() == null)
            {
                response = new Response("404", "application/json");
            }else
                {
                    response = ResponseHandler.handleRequest(request);
                }
            output.write(response.get());
        }catch (IOException e)
            {
                System.err.println(Thread.currentThread().getName() + " Error: " + e.getMessage());
            }
        finally
        {
            try
            {
                if (output != null)
                {
                    output.close();
                }
                if (in != null)
                {
                    in.close();
                    clientSocket.close();
                }
            }catch (IOException e)
                {
                    e.printStackTrace();
                }
        }
    }
}
