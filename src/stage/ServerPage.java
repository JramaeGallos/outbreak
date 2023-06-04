package stage;


import java.io.*;
import java.net.*;
import java.util.*;

public class ServerPage {
	private String memberPlayers="";
	private int numOfPlayer = 0;
	private int minPlayer = 4;
    ArrayList<PrintWriter> clientOutputStreams;
    ArrayList<ObjectOutputStream> gameCharacters;
    ArrayList<String> usernames = new ArrayList<>();

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

    public void broadcastGameOver(String message){
    	for (PrintWriter writer : clientOutputStreams) {
            writer.println("gameOver= "+ message);
        }
    }

    public void broadcastReady(String name){
    	System.out.println("Number of players in the lobby: " + this.numOfPlayer);
    	if (this.numOfPlayer == 1){
    		System.out.println("Player " + name + " is the host!");
    		for (PrintWriter writer : clientOutputStreams) {
                writer.println("host=" + name);
            }
    	}else{
    		System.out.println("Player " + name + " joined the game!");
    		this.memberPlayers= this.memberPlayers + name + "*";
    	}
    }

    public void broadcastCheck(){
    	if (this.numOfPlayer >= this.minPlayer){
    		for (PrintWriter writer : clientOutputStreams) {
    			writer.println("READY");
            }
    	} else if (this.numOfPlayer < this.minPlayer){
    		for (PrintWriter writer : clientOutputStreams) {
                writer.println("WAITING");
            }
    		System.out.println("Not all players are ready to start the game.");
    	}
    }

    public void broadcastLobby(){
    	System.out.println("show lobby ..." + this.memberPlayers);
    	if(this.memberPlayers.length()==0){
    		for (PrintWriter writer : clientOutputStreams) {
    			writer.println("emptyLobby="+"--");
            }
    	}else{
    		for (PrintWriter writer : clientOutputStreams) {
    			writer.println("showLobby="+this.memberPlayers);
            }
    	}
    }

    public void broadcastStart(){
    	for (PrintWriter writer : clientOutputStreams) {
			writer.println("START");
        }
    }

    private void checkUsername(String name){
    	if (this.usernames.contains(name)){
    		for (PrintWriter writer : clientOutputStreams) {
                writer.println("DENIED");
            }
    		System.out.println("Username " + name + " exits!" );
    	}else{
    		this.usernames.add(name);
    		for (PrintWriter writer : clientOutputStreams) {
                writer.println("ACCEPTED");
            }
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
				                    System.out.println("Received distance: " + data);
				                    broadcastDist(data);
				                } else if (event.equals("gameOver")) {
				                    // Handle Event 3
				                	System.out.println("GameOver " + data);
				                    broadcastGameOver(data);
				                } else if (event.equals("ready")) {
				                    // Handle Event 4
				                	numOfPlayer++;
				                	broadcastReady(data);
				                } else if (event.equals("username")) {
				                    // Handle Event 5
				                	checkUsername(data);
				                } else if (event.equals("status")) {
				                    // Handle Event 5
				                	broadcastStart();
				                } else if (event.equals("check")) {
				                    // Handle Event 5
				                	if(data.equals("play")){
				                		broadcastCheck();
				                	}else if(data.equals("lobby")){
				                		broadcastLobby();
				                	}
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
