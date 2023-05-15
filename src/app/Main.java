package app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import stage.ServerPage;
import stage.StartPage;

// main class
public class Main extends Application {
	private boolean isServer= false;
	//private boolean isServer= true;

	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage stage){
		if (isServer){
			this.createServer(stage);
		}else{
			this.createClient(stage);
		}
	}

	private void createServer(Stage stage){
		 new ServerPage().startServer();
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
