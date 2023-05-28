package stage;


import java.io.*;
import java.net.*;
import java.util.*;

import game.Ship;

public class ServerPage {

    ArrayList<PrintWriter> clientOutputStreams;
    ArrayList<ObjectOutputStream> gameCharacters;

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

//    // send message to all connected clients
    public void broadcastChat(String message) {
        for (PrintWriter writer : clientOutputStreams) {
            writer.println("chat= "+message);
        }

    }

    public void broadcastDist(String message) {
        for (PrintWriter writer : clientOutputStreams) {
            writer.println("distance= "+message);
        }

    }

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
						 String[] parts = message.split("=");
						 if(parts.length!=1){
				                String event = parts[0];
				                String data = parts[1];

				                // Process the received event and data
				                if (event.equals("chat")) {
				                    // Handle Event 1
				                    System.out.println("Received message " + data);
				                    broadcastChat(data);
				                } else if (event.equals("distance")) {
				                    // Handle Event 2
//				                    System.out.println("Received distance: " + data);
				                    broadcastDist(data);
				                }
						 }

					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        }
    }
}
