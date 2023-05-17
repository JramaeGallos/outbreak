package stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import game.GameTimer;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

// game stage class
public class GameStage {

	public static final int WINDOW_HEIGHT = 500;
	public static final int WINDOW_WIDTH = 800;
	public static final int WINDOW_EXTENDED_WIDTH = 1050;
	private Scene scene;
	public static Stage stage;

	private Group root;
	private Canvas canvas;
	private GraphicsContext gc;
	private Canvas canvas1;
	private GraphicsContext gc1;
	private GameTimer gametimer;

	private ImageView view;
	private Image gameBackground;
	private final static String GAME_BACKGROUND_PATH = "stage/resources/game_background.png";

	// attributes for connection
    private BufferedReader reader;
    private PrintWriter writer;
    private Socket sock;

	//in chat game
	private TextField chat= new TextField();
	private TextArea messages= new TextArea();
	private String message;
	private String userName;

	//the class constructor
	public GameStage() {
		this.root = new Group();
		this.scene = new Scene(this.root, GameStage.WINDOW_EXTENDED_WIDTH,GameStage.WINDOW_HEIGHT,Color.GRAY);
		this.canvas = new Canvas(GameStage.WINDOW_EXTENDED_WIDTH,GameStage.WINDOW_HEIGHT);
		this.gc = canvas.getGraphicsContext2D();
		this.canvas1 = new Canvas(GameStage.WINDOW_WIDTH,GameStage.WINDOW_HEIGHT);
		this.gc1 = canvas.getGraphicsContext2D();

		this.gameBackground = new Image(GameStage.GAME_BACKGROUND_PATH, GameStage.WINDOW_WIDTH, GameStage.WINDOW_HEIGHT, false, false);
		this.view= new ImageView(this.gameBackground);

		//instantiate an animation timer
		this.gametimer = new GameTimer(this.gc, this.gc1, this.canvas1 ,this.scene, this);
	}

	//method to add the stage elements
	public void setStage(Stage stage) {
		GameStage.stage = stage;
		this.root.getChildren().addAll(view, canvas, canvas1);

		GameStage.stage.setTitle("OUTBREAK!");
		GameStage.stage.getIcons().add(GameMenu.icon);	//add icon to stage

		GameStage.stage.setScene(this.scene); // MARK: show elements unique to this stage

		this.createTextField();
		this.mousePressEvent();
		this.createConvo();
		this.startClient();
		//invoke the start method of the animation timer
		this.gametimer.setUserName(this.userName);
		this.gametimer.start();
		GameStage.stage.show();
	}

	public void enableTextField(){
		this.chat.setDisable(false);
		this.chat.setEditable(true);
	}

	public void disableTextField(){
		chat.setDisable(true);
		chat.setEditable(false);
	}

	private void mousePressEvent(){
		this.chat.setOnMouseClicked( event -> {
			this.enableTextField();
        });
		this.chat.setOnMouseExited( event -> {
			this.disableTextField();
        });
	}

	private void createTextField(){
		this.chat.setAlignment(Pos.CENTER);
		this.chat.setLayoutX(850);
		this.chat.setLayoutY(455);
		this.chat.setPromptText("Reply ");
		this.root.getChildren().add(chat);
	}

	private void createConvo(){
		try {
			this.messages.setFont(Font.loadFont(new FileInputStream(new File(PanelText.FONT_PATH)), 12));
		} catch (FileNotFoundException e) {
			this.messages.setFont(Font.font("Verdana", 12));
		}
		this.messages.setEditable(false);
		this.messages.setDisable(true);
		this.messages.setPrefHeight(230);
		this.messages.setLayoutX(800);
		this.messages.setLayoutY(210);
		this.root.getChildren().add(this.messages);
	}
	private void handleChat(){
		this.chat.setOnAction(event ->{
    		this.message = this.userName + ": ";
    		this.message += this.chat.getText() + "\n";
    		this.chat.clear();
    		writer.println(message);
    		this.message = "";
    	});
	}

	public void startClient() {
        try {
            sock = new Socket("127.0.0.1", 5000); // connect to server
            reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            writer = new PrintWriter(sock.getOutputStream(), true);

            // create a new thread to listen for incoming messages from the server
            Thread incomingThread = new Thread(new IncomingReader());
            incomingThread.start();

            this.handleChat();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // class to listen for incoming messages from server
    public class IncomingReader implements Runnable {
        public void run() {
            String reply;
            try {
                while ((reply = reader.readLine()) != null) {
                   messages.appendText(reply + "\n");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    void setUserName(String user){
		this.userName = user;
	}

    public String getUserName(){
    	return this.userName;
    }
}