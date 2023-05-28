package app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import stage.StartPage;

// main class
public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage stage){
		this.createClient(stage);
	}

	private void createClient(Stage stage){
		StartPage start= new StartPage();
		start.setStage(stage);
		closeWindowListener(stage);
	}

	private void closeWindowListener(Stage stage) {
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		    @Override
		    public void handle(WindowEvent e) {
		    	System.exit(0);
		    	Platform.exit();  // closes threads
		    }
		});
	}

}
