package at.fhtw.bif3.swe.mtcg.if19b017.server;

import at.fhtw.bif3.swe.mtcg.if19b017.server.request.RequestHandler;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public void start(int port) throws IOException {
        ServerSocket socket = new ServerSocket(port);
        System.out.println("Server running on port: " + port);
        while (true) {
            try {
                Socket clientSocket = socket.accept();
                RequestHandler requestHandler = new RequestHandler(clientSocket);
                Thread new_request = new Thread(requestHandler);
                new_request.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
