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
        gameCharacters = new ArrayList();

        try {
            ServerSocket serverSock = new ServerSocket(5000); // listen on port 5000

            while (true) {
                Socket clientSock = serverSock.accept();
                PrintWriter writer = new PrintWriter(clientSock.getOutputStream(), true);
                clientOutputStreams.add(writer);

//                ObjectOutputStream out = new ObjectOutputStream(clientSock.getOutputStream());
//                ObjectInputStream in = new ObjectInputStream(clientSock.getInputStream());
//                gameCharacters.add(out);


                // create a new thread to handle incoming messages from this client
                Thread clientThread = new Thread(new ClientHandler(clientSock));
                clientThread.start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

//    // send message to all connected clients
    public void broadcast(String message) {
        for (PrintWriter writer : clientOutputStreams) {
            writer.println(message);
        }

    }
//
//    // class to handle incoming messages from a client

//    public void assignCharacter(Ship character){
//    	for (ObjectOutputStream out: gameCharacters){
//    		try {
//				out.writeObject(character);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//    	}
//
//    }
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
//            Ship character =new Ship(100, 100);
//            assignCharacter(character);
                try {
					while ((message = reader.readLine()) != null) {
					    broadcast(message);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        }
    }
}
