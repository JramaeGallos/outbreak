package stage;

import javafx.scene.layout.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class StartPage {
	private AnchorPane startRoot;
	private Scene startScene;
	private Stage startStage;
	private Socket sock;

	private ImageView view;
	private Image startBackground;
	private String START_BACKGROUND_PATH = "stage/resources/menu_background.png";
	public final static Image icon = new Image("images/logo.png");
	public final static ImageView title = new ImageView("stage/resources/title.png");

	public final static int START_BUTTONS_START_X = 200;
	public final static int START_BUTTONS_START_Y = 350;
	private ArrayList<GameButton> startButtons;
	private TextField userNameField;


	//Constructor
	public StartPage(){
		this.startRoot = new AnchorPane();
		this.startScene = new Scene(this.startRoot, GameStage.WINDOW_WIDTH,GameStage.WINDOW_HEIGHT);
		this.startStage = new Stage();

		this.startBackground = new Image(START_BACKGROUND_PATH);
		this.view = new ImageView();

		this.startButtons = new ArrayList<GameButton>();
		this.userNameField= new TextField();
	}

	// setting the stage
		public void setStage(Stage stage) {
			this.startStage = stage;
			this.createBackground();
			this.createGameTitle();
			this.createTextField();
			this.createButtons();

			this.startStage.setTitle("OUTBREAK!");
			this.startStage.getIcons().add(GameMenu.icon);	//add icon to stage
			this.startStage.setResizable(false);
			this.startStage.setScene(this.startScene);
			this.startStage.show();
		}

		private void createTextField(){
			this.userNameField.setAlignment(Pos.CENTER);
		    double parentWidth = this.startRoot.getWidth();
		    double parentHeight = this.startRoot.getHeight();

		    this.userNameField.setPrefWidth(300);

		    // Calculate the centered position
		    double centerX = (parentWidth - this.userNameField.getPrefWidth()) / 2;
		    double centerY = (parentHeight - this.userNameField.getHeight()) / 2;
		    this.userNameField.setLayoutX(centerX);
		    this.userNameField.setLayoutY(centerY);


			this.userNameField.setStyle(
				"-fx-background-color: #6d6d6d; "  // CF1F1F
				+ "-fx-text-fill: #b3b144;"
				+ "-fx-font-size: 20px;"
				+ "-fx-padding: 5px;"
				+ "-fx-border-color: #CCCCCC;"
				+ "-fx-border-width: 2px;"
				+ "-fx-font-family: 'Sans-serif';"
				+ "-fx-font-weight: bold;"
				+ "-fx-border-radius: 10px;"
				+ "-fx-background-radius: 10px;"
			);

			this.userNameField.setPromptText("Enter your user name ");
			this.startRoot.getChildren().add(this.userNameField);
		}

		// setting the start background
		private void createBackground() {
			this.view.setImage(this.startBackground);
			this.startRoot.getChildren().add(this.view);
		}

		// creating the start title
		private void createGameTitle() {
			GameMenu.title.setLayoutX(180);
			GameMenu.title.setLayoutY(30);
			GameMenu.title.setEffect(new DropShadow());
			this.startRoot.getChildren().add(GameMenu.title);
		}

		// adding menu buttons to the root (in a vertical layout)
		private void addStartButton(GameButton button) {
			// MARK: set button position
			button.setLayoutX(START_BUTTONS_START_X + startButtons.size() * 200);
			button.setLayoutY(START_BUTTONS_START_Y);
			startButtons.add(button);
			this.startRoot.getChildren().add(button);
		}

		private void createButtons(){
			createMenuButton();
			createExitButton();
		}

		// methods for creating the menu buttons
		private void createMenuButton() {
			GameButton button = new GameButton("Start");
			addStartButton(button);

			button.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent e) {
					if (userNameField.getText().isEmpty()) {
						// send an alert
						Alert alert = new Alert(Alert.AlertType.INFORMATION);
				        alert.setTitle("Error");
				        alert.setHeaderText(null);
				        alert.setContentText("User name must not be empty!");
				        alert.showAndWait();
					} else if (userNameField.getText().length() >= 10) {
						Alert alert = new Alert(Alert.AlertType.INFORMATION);
				        alert.setTitle("Error");
				        alert.setHeaderText(null);
				        alert.setContentText("User name must be less than 10 characters!");
				        alert.showAndWait();
					} else {
						// Check if user name is unique
						try {
							 sock = new Socket("127.0.0.1", 5000); // connect to server
							 BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
							 PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
					         String serverMessage;
					         // send the user name to the server for checking
					         out.println("username=" + userNameField.getText());

				            while ((serverMessage = in.readLine()) != null) {
				            	if (serverMessage.equals("ACCEPTED")){
				            		GameMenu menu = new GameMenu();
									menu.setUserName(userNameField.getText());
									menu.setSocket(sock);
									menu.setStage(startStage);
									break;
				            	} else if (serverMessage.equals("DENIED")) {
				            		Alert alert = new Alert(Alert.AlertType.INFORMATION);
							        alert.setTitle("Error");
							        alert.setHeaderText(null);
							        alert.setContentText("Username already in use!");
							        alert.showAndWait();
				                    break;
				                }
				            }
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
			});
		}

		private void createExitButton() {
			GameButton button = new GameButton("EXIT");
			addStartButton(button);

			button.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent e) {
					System.exit(0);
					Platform.exit();
				}
			});
		}
}
