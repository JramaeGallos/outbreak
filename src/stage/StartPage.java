package stage;

import javafx.scene.layout.*;

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
			this.userNameField.setLayoutX(320);
			this.userNameField.setLayoutY(250);
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
					GameMenu menu = new GameMenu();
					menu.setUserName(userNameField.getText());
					menu.setStage(startStage);
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
