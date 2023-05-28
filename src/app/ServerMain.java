package app;

import javafx.application.Application;
import javafx.stage.Stage;
import stage.ServerPage;

public class ServerMain extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage stage){
		new ServerPage().startServer();
	}
}
