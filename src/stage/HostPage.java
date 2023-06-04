package stage;

import javafx.scene.layout.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class HostPage{
	private AnchorPane startRoot;
	private Scene startScene;
	private Stage startStage;
	private Socket sock;
	private BufferedReader reader;
    private PrintWriter writer;

	private ImageView view;
	private Image startBackground;
	private String START_BACKGROUND_PATH = "stage/resources/menu_background.png";
	public final static Image icon = new Image("images/logo.png");
	public final static ImageView title = new ImageView("stage/resources/title.png");

	public final static int START_BUTTONS_START_X = 320;
	public final static int START_BUTTONS_START_Y = 320;
	public final static int LOBBY_BUTTONS_START_Y = 380;
	private String userName;

	//Constructor
	public HostPage(){
		this.startRoot = new AnchorPane();
		this.startScene = new Scene(this.startRoot, GameStage.WINDOW_WIDTH,GameStage.WINDOW_HEIGHT);
		this.startStage = new Stage();

		this.startBackground = new Image(START_BACKGROUND_PATH);
		this.view = new ImageView();
	}

	// setting the stage
		public void setStage(Stage stage) {
			this.startStage = stage;
			this.createBackground();
			this.createGameTitle();
			this.createPlayButton();
			this.createLobbyButton();

			this.startStage.setTitle("OUTBREAK!");
			this.startStage.getIcons().add(GameMenu.icon);	//add icon to stage
			this.startStage.setResizable(false);
			this.startStage.setScene(this.startScene);
			this.startStage.show();
		}

		public void setUserName(String user){
			this.userName = user;
		}

		public void setSocket(Socket sock){
			this.sock = sock;
		}

		// setting the start background
		private void createBackground() {
			this.view.setImage(this.startBackground);
			this.startRoot.getChildren().add(this.view);
		}

		// creating the start title
		private void createGameTitle() {
			GameMenu.title.setLayoutX(180);
			GameMenu.title.setLayoutY(50);
			GameMenu.title.setEffect(new DropShadow());
			this.startRoot.getChildren().add(GameMenu.title);
		}

		// adding menu buttons to the root (in a vertical layout)
		private void addStartButton(GameButton button, int type) {
			// MARK: set button position
			button.setLayoutX(START_BUTTONS_START_X);
			if (type == 0){
				button.setLayoutY(START_BUTTONS_START_Y);
			}else{
				button.setLayoutY(LOBBY_BUTTONS_START_Y);
			}
			this.startRoot.getChildren().add(button);
		}

		private void createPlayButton() {
			GameButton button = new GameButton("PLAY");
			addStartButton(button,0);

			button.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent e) {
					try {
			        	reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			            writer = new PrintWriter(sock.getOutputStream(), true);
			            String serverMessage;
			            writer.println("check=play");


			            while ((serverMessage = reader.readLine()) != null) {
			           	 if (serverMessage.equals("WAITING")) {
			            		Alert alert = new Alert(Alert.AlertType.INFORMATION);
						        alert.setTitle("Waiting");
						        alert.setHeaderText(null);
						        alert.setContentText("Waiting for players...");
						        alert.showAndWait();
			                    break;
			              }else if (serverMessage.equals("READY")) {
			                	writer.println("status=play");
			              }else if (serverMessage.equals("START")) {
			                	GameStage playGame = new GameStage(sock);
		    					playGame.setUserName(userName);
		    					playGame.setStage(startStage);
			                    break;
				         }

			            }
			        } catch (IOException ex) {
			            ex.printStackTrace();
			        }
				}
			});
	    }

		private void createLobbyButton() {
			GameButton button = new GameButton("Lobby");
			addStartButton(button,1);

			button.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent e) {
					try {
			        	reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			            writer = new PrintWriter(sock.getOutputStream(), true);
			            String serverMessage;
			            writer.println("check=lobby");


			            while ((serverMessage = reader.readLine()) != null) {
			            	 String[] parts = serverMessage.split("=");
				             if(parts.length!=1){
				            	 String event = parts[0];
					             String data = parts[1];
					             if(event.equals("emptyLobby")) {
					            	 Alert alert = new Alert(Alert.AlertType.INFORMATION);
								     alert.setTitle("Lobby");
								     alert.setHeaderText(null);
								     alert.setContentText("No player in the lobby yet ...");
								     alert.showAndWait();
					                 break;
					             }
					             else if(event.equals("showLobby")){
					            	 Alert alert = new Alert(Alert.AlertType.INFORMATION);
								     alert.setTitle("Lobby");
								     alert.setHeaderText(null);
								     String[] players= data.split("\\*");
								     String content="";
								     for(String player: players){
								    	 content= content + "Player "+player+ " has joined the game. \n";
								     }
								     alert.setContentText(content);
								     alert.showAndWait();
					                 break;
					             }
				             }else{
				            	 break;
				             }
			            }
			        } catch (IOException ex) {
			            ex.printStackTrace();
			        }
				}
			});
	    }
}
