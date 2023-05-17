package stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

// menu class for game menu
public class GameMenu {

	private AnchorPane menuRoot;
	private Scene menuScene;
	private Stage menuStage;

	private ImageView view;
	private Image menuBackground;
	private String MENU_BACKGROUND_PATH = "stage/resources/menu_background.png";
	public final static Image icon = new Image("images/logo.png");
	public final static ImageView title = new ImageView("stage/resources/title.png");

	// for organizing menu buttons
	public final static int MENU_BUTTONS_START_X = 50;
	public final static int MENU_BUTTONS_START_Y = 150;
	private ArrayList<GameButton> menuButtons;

	private GameMenuSubScene helpSubScene;
	private GameMenuSubScene aboutSubScene;
	private GameMenuSubScene shownSubScene;  // to help with opening subscenes one at a time

	private String userName;

	// CONSTRUCTOR
	public GameMenu() {
		this.menuRoot = new AnchorPane();
		this.menuScene = new Scene(this.menuRoot, GameStage.WINDOW_WIDTH,GameStage.WINDOW_HEIGHT);
		this.menuStage = new Stage();

		this.menuBackground = new Image(MENU_BACKGROUND_PATH);
		this.menuButtons = new ArrayList<GameButton>();
		this.view = new ImageView();

		this.userName = "";
	}

	// setting the stage
	public void setStage(Stage stage) {
		this.menuStage = stage;
		this.createBackground();

		// MARK: set the buttons and title
		this.createButtons();
		this.createGameTitle();
		this.systemGreetings();
		this.createHelpSubScene();
		this.createAboutSubScenes();

		this.menuStage.setTitle("OUTBREAK!");
		this.menuStage.getIcons().add(GameMenu.icon);	//add icon to stage
		this.menuStage.setResizable(false);
		this.menuStage.setScene(this.menuScene);
		this.menuStage.show();
	}

	void setUserName(String user){
		this.userName = user;
	}

	// create the subscene and its content
	private void createHelpSubScene() {
		this.helpSubScene = new GameMenuSubScene();
		PanelText text = new PanelText(
				"ESCAPE FROM THE OUTBREAK!\n"
				+ "Avoid the viruses, run, and win the race!\n"

				+ "\nFace mask grants you immunity, \n"
				+ "energy drink helps increase your speed, \n"
				+ "and vaccine boost your health.\n"

				+ "\nCONTROLS:\n"
				+ "* Press ARROW KEYS to move.\n"
				);
		this.menuRoot.getChildren().add(helpSubScene);
		this.helpSubScene.getRootOfSubScene().getChildren().add(text);
	}
	private void createAboutSubScenes() {
		this.aboutSubScene = new GameMenuSubScene();
		this.menuRoot.getChildren().add(aboutSubScene);
		PanelText text = new PanelText(
				"ESCAPE FROM THE OUTBREAK!\n"
				+ "Avoid the viruses, run, and win the race!\n"

				+ "\nDeveloper:\n"
				+ "* Mark Lewis S. Damalerio\n"
				+ "* Jramae Gallos\n"
				+ "* Leilanie F. Evora\n"

				+ "\nReferences:\n"
				+ "* CMSC 22 Mini Project Template by UPLB ICS CMSC 22 Professors\n"
				+ "* Character design by Nathaniel Negre\n"
				+ "* UI and models taken from Kenney.nl"
				);
		this.aboutSubScene.getRootOfSubScene().getChildren().add(text);
	}

	// logic for the showing of subscenes (in menu)
	private void showSubScene(GameMenuSubScene subScene) {
		// if there is a shown subscene and that subscene is different from our intended subscene
		if (this.shownSubScene != null && this.shownSubScene != subScene) {
			this.shownSubScene.movePanel(); //move that shown subscene
		}

		// if the current shown subscene is clicked, hide that current subscene
		if (subScene == this.shownSubScene) {
			subScene.movePanel();
			this.shownSubScene = null;
		} else { // else, set the shown to be the current subscene
			subScene.movePanel();
			this.shownSubScene = subScene;
		}
	}

	// setting the menu background
	private void createBackground() {
		this.view.setImage(this.menuBackground);
		this.menuRoot.getChildren().add(this.view);
	}

	// creating the game title
	private void createGameTitle() {
		GameMenu.title.setLayoutX(280);
		GameMenu.title.setLayoutY(30);
		GameMenu.title.setEffect(new DropShadow());
		this.menuRoot.getChildren().add(GameMenu.title);
	}

	private void systemGreetings(){
		Rectangle rectangle = new Rectangle(20, 20, 250, 100);
		rectangle.setFill(Color.WHITE);
		rectangle.setArcWidth(100);
        rectangle.setArcHeight(100);
        Circle circle = new Circle(70, 70, 50);
        circle.setFill(Color.RED);

		Image profile = new Image("stage/resources/doctor_icon.png",80,80,false,false);
		ImageView imageView = new ImageView(profile);
		imageView.setLayoutX(30);
		imageView.setLayoutY(30);

		String userGreetings = "Username:\n" + this.userName;
		Label l = new Label(userGreetings);
		l.setLayoutX(130);
		l.setLayoutY(50);
		try {
			l.setFont(Font.loadFont(new FileInputStream(new File(PanelText.FONT_PATH)), 18));
		} catch (FileNotFoundException e) {
			l.setFont(Font.font("Verdana", 18));
		}
		this.menuRoot.getChildren().addAll(rectangle, circle, imageView, l);
	}

	// adding menu buttons to the root (in a vertical layout)
	private void addMenuButton(GameButton button) {
		// MARK: set button position
		button.setLayoutX(MENU_BUTTONS_START_X);
		button.setLayoutY(MENU_BUTTONS_START_Y + menuButtons.size() * 90);
		menuButtons.add(button);
		this.menuRoot.getChildren().add(button);
	}

	// compiling the create buttons
	private void createButtons() {
		createStartButton();
		createHelpButton();
		createAboutButton();
		createExitButton();
	}

	// methods for creating the menu buttons
	private void createStartButton() {
		GameButton button = new GameButton("PLAY");
		addMenuButton(button);

		button.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				GameStage playGame = new GameStage();
				playGame.setUserName(userName);
				playGame.setStage(menuStage);
			}
		});
	}
	private void createHelpButton() {
		GameButton button = new GameButton("HELP");
		addMenuButton(button);

		button.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				showSubScene(helpSubScene);
			}
		});
	}
	private void createAboutButton() {
		GameButton button = new GameButton("ABOUT");
		addMenuButton(button);

		button.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				showSubScene(aboutSubScene);
			}
		});
	}
	private void createExitButton() {
		GameButton button = new GameButton("EXIT");
		addMenuButton(button);

		button.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				System.exit(0);
				Platform.exit();
			}
		});
	}
}
