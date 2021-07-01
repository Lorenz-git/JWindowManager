import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class JWindowsManager extends Application {

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage stage) throws IOException {
		
		//works with exported jar
		//VBox root = (VBox) FXMLLoader.load(getClass().getResource("/resources/fxml/Manager.fxml"));
		
		Parent root = FXMLLoader.load(getClass().getResource("/fxml/Manager.fxml"));

		// Create the Scene
		Scene scene = new Scene(root);
		// Set the Scene to the Stage
		stage.setScene(scene);
		// Set the Title to the Stage
		stage.setTitle("JWinManager");
		// Display the Stage
		stage.show();
	}

}
