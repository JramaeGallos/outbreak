package stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javafx.event.EventHandler;
import javafx.scene.Scene;
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

		public void setUserName(String user){
			this.userName = user;
		}

		public void setSocket(Socket sock){
			this.sock = sock;
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
						BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
						PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
				        String serverMessage;
				        out.println("ready=" + userName);
			            while ((serverMessage = in.readLine()) != null) {
			            	String[] parts = serverMessage.split("=");
							if(parts.length!=1){
					                String event = parts[0];
					                String data = parts[1];
					                if (event.equals("host") && userName.equals(data)){
					            		HostPage hostPage = new HostPage();
					            		hostPage.setUserName(userName);
					            		hostPage.setSocket(sock);
					            		hostPage.setStage(loadingStage);
					    				break;
					            	}
							}else{
								if (serverMessage.equals("START")) {
				                	GameStage playGame = new GameStage(sock);
									playGame.setUserName(userName);
									playGame.setStage(loadingStage);
				                    break;
				                }
							}
			            }
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			});
		}
}
