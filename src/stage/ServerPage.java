package stage;


import java.io.*;
import java.net.*;
import java.util.*;

import game.Ship;

public class ServerPage {
	private int numOfPlayer;
	private int maxPlayer;
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
            writer.println("chat= "+ message);
        }

    }

    public void broadcastDist(String message) {
        for (PrintWriter writer : clientOutputStreams) {
            writer.println("distance= "+ message);
        }

    }

    public void broadcastStart(){
    	if (this.numOfPlayer == this.maxPlayer){
    		for (PrintWriter writer : clientOutputStreams) {
                writer.println("START");
            }
    	} else {
            System.out.println("Not all players are ready to start the game.");
        }

    }

    private void setNumOfPlayers(String data) {
    	this.maxPlayer = Integer.parseInt(data);
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
				                    System.out.println("Received distance: " + data);
				                    broadcastDist(data);
				                } else if (event.equals("players")) {
				                    // Handle Event 3
				                	System.out.println("Number of Players: " + data);
				                    setNumOfPlayers(data);
				                }else if (event.equals("status")) {
				                    // Handle Event 3
				                	numOfPlayer++;
				                	System.out.println("Player Status: " + data);
				                    broadcastStart();
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
