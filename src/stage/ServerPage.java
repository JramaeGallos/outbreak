package stage;


import java.io.*;
import java.net.*;
import java.util.*;

public class ServerPage {

    ArrayList<PrintWriter> clientOutputStreams;

    public void startServer() {
        clientOutputStreams = new ArrayList<>();

        try {
            ServerSocket serverSock = new ServerSocket(5000); // listen on port 5000

            while (true) {
                Socket clientSock = serverSock.accept();
                PrintWriter writer = new PrintWriter(clientSock.getOutputStream(), true);
                clientOutputStreams.add(writer);

                // create a new thread to handle incoming messages from this client
                Thread clientThread = new Thread(new ClientHandler(clientSock));
                clientThread.start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // send message to all connected clients
    public void broadcast(String message) {
        for (PrintWriter writer : clientOutputStreams) {
            writer.println(message);
        }
    }

    // class to handle incoming messages from a client
    public class ClientHandler implements Runnable {
        BufferedReader reader;
        Socket sock;

        public ClientHandler(Socket clientSock) {
            try {
                sock = clientSock;
                reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    broadcast(message);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
