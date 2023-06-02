package stage;

import java.awt.Insets;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Optional;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LoadingPage {
	private AnchorPane startRoot;
	private Scene startScene;
	private Stage loadingStage;
    private Socket sock;

	private ImageView view;
	private Image startBackground;
	private String START_BACKGROUND_PATH = "stage/resources/menu_background.png";
	private String LOAD_PATH = "stage/resources/loading.gif";
	public final static Image icon = new Image("images/logo.png");
	public final static ImageView title = new ImageView("stage/resources/title.png");

	public final static int START_BUTTONS_START_X = 330;
	public final static int START_BUTTONS_START_Y = 220;
	private String userName;

	//Constructor
	public LoadingPage(){
		this.startRoot = new AnchorPane();
		this.startScene = new Scene(this.startRoot, GameStage.WINDOW_WIDTH,GameStage.WINDOW_HEIGHT);
		this.loadingStage = new Stage();

		this.startBackground = new Image(START_BACKGROUND_PATH);
		this.view = new ImageView();
		this.userName = "";

	}

	// setting the stage
		public void setStage(Stage stage) {
			this.loadingStage = stage;
			this.createBackground();
			this.createGameTitle();
			this.createClient();

			this.loadingStage.setTitle("OUTBREAK!");
			this.loadingStage.getIcons().add(GameMenu.icon);	//add icon to stage
			this.loadingStage.setResizable(false);
			this.loadingStage.setScene(this.startScene);
			this.loadingStage.show();
		}

		// setting the start background
		private void createBackground() {
			this.view.setImage(this.startBackground);
			ImageView viewLoad = new ImageView();
			Image loading = new Image(LOAD_PATH);
			viewLoad.setImage(loading);
			viewLoad.setLayoutX(200);
			viewLoad.setLayoutY(280);
			this.startRoot.getChildren().addAll(this.view, viewLoad);
		}

		// creating the start title
		private void createGameTitle() {
			GameMenu.title.setLayoutX(180);
			GameMenu.title.setLayoutY(30);
			GameMenu.title.setEffect(new DropShadow());
			this.startRoot.getChildren().add(GameMenu.title);
		}

		private void addStartButton(GameButton button) {
			// MARK: set button position
			button.setLayoutX(START_BUTTONS_START_X);
			button.setLayoutY(START_BUTTONS_START_Y);
			this.startRoot.getChildren().add(button);
		}


		private void createClient(){
			GameButton button = new GameButton("READY");
			addStartButton(button);

			button.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent e) {
					try {
						 sock = new Socket("127.0.0.1", 5000); // connect to server
						 BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
						 PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
				         String serverMessage;
				         out.println("status=ready");

			            while ((serverMessage = in.readLine()) != null) {
			            	if (serverMessage.equals("GetNumOfPlayer")){
			            		showAlertWithTextField(out);
			            	}
			            	else if (serverMessage.equals("START")) {
			                	GameStage playGame = new GameStage(sock);
								playGame.setUserName(userName);
								playGame.setStage(loadingStage);
			                    break;
			                }
			            }
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			});
		}

		public static boolean isInteger(String input) {
		    try {
		        Integer.parseInt(input);
		        return true;
		    } catch (NumberFormatException e) {
		        return false;
		    }
		}

		private void showAlertWithTextField(PrintWriter out) {
	        // Create an alert dialog
	        Alert alert = new Alert(Alert.AlertType.NONE);
	        alert.setTitle("Host Player");
	        alert.setHeaderText(null);

	        // Create a TextField
	        TextField numOfPlayerField = new TextField();
	        numOfPlayerField.setPromptText("Enter number of users ");

	        // Calculate the centered position
	        double parentWidth = this.startRoot.getWidth();
		    double parentHeight = this.startRoot.getHeight();
		    double centerX = (parentWidth - numOfPlayerField.getPrefWidth()) / 2;
		    double centerY = (parentHeight - numOfPlayerField.getHeight()) / 2;
		    numOfPlayerField.setLayoutX(centerX);
		    numOfPlayerField.setLayoutY(centerY);
			numOfPlayerField.setStyle(
				"-fx-background-color: #6d6d6d; "  // CF1F1F
				+ "-fx-text-fill: #b3b144;"
				+ "-fx-font-size: 10px;"
				+ "-fx-padding: 5px;"
				+ "-fx-border-color: #CCCCCC;"
				+ "-fx-border-width: 2px;"
				+ "-fx-font-family: 'Sans-serif';"
				+ "-fx-font-weight: bold;"
				+ "-fx-border-radius: 10px;"
				+ "-fx-background-radius: 10px;"
			);

	        // Set the custom dialog content
	        alert.getDialogPane().setContent(numOfPlayerField);

	        // Add OK button
	        alert.getDialogPane().getButtonTypes().add(ButtonType.OK);

	        // check for errors in input, ask user again if there is an error
	        boolean error = true;
	        while (error) {
	            // Show the alert and wait for user input
	            Optional<ButtonType> result = alert.showAndWait();

	            if (result.isPresent() && result.get() == ButtonType.OK) {
	                String input = numOfPlayerField.getText();

	                if (input.isEmpty()) {
	                    // Create another alert for empty input
	                    Alert emptyAlert = new Alert(Alert.AlertType.INFORMATION);
	                    emptyAlert.setTitle("Invalid Input");
	                    emptyAlert.setHeaderText(null);
	                    emptyAlert.setContentText("Number of users cannot be empty!");
	                    emptyAlert.showAndWait();
	                } else if (!isInteger(input)) {
	                	// Create another alert for invalid number
	                    Alert emptyAlert = new Alert(Alert.AlertType.INFORMATION);
	                    emptyAlert.setTitle("Invalid Input");
	                    emptyAlert.setHeaderText(null);
	                    emptyAlert.setContentText("Please enter a valid number!");
	                    emptyAlert.showAndWait();
	                } else {
	                    out.println("players=" + input);
	                    error = false; // Set validInput to true to exit the loop
	                }
	            } else {
	                // User clicked on Cancel or closed the dialog
	                break; // Exit the loop
	            }
	        }
	    }

		public void setUserName(String user){
			this.userName = user;
		}
}
