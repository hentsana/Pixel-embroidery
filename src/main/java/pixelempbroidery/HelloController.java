package pixelempbroidery;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    private Button startButton;

    @FXML
    private void onStartClicked() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("mainBanner.fxml")
        );
        Scene scene = new Scene(loader.load());

        Stage stage = (Stage) startButton.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Редактор вишивки");
    }
}
