package pixelempbroidery;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ResourceBundle;
import java.io.IOException;

public class HelloController implements Initializable {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    private Button startButton;
    @FXML
    private Canvas previewCanvas;

    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadStartNameDrawing();
    }

    private void loadStartNameDrawing() {
        try {
            BufferedImage img = ImageIO.read(getClass().getResourceAsStream("image/Violetta.png"));
            int scale = 10;
            int cols = img.getWidth() / scale;
            int rows = img.getHeight() / scale;
            GraphicsContext graphicsContext = previewCanvas.getGraphicsContext2D();

            graphicsContext.setFill(Color.BLACK);
            graphicsContext.fillRect(0, 0, previewCanvas.getWidth(), previewCanvas.getHeight());

            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    int rgb = img.getRGB(col * scale + scale / 2, row * scale + scale / 2);
                    if (rgb != 0xFF000000 && rgb != -16777216) {
                        double a = ((rgb >> 24) & 0xFF) / 255.0;
                        double red = ((rgb >> 16) & 0xFF) / 255.0;
                        double green = ((rgb >> 8) & 0xFF) / 255.0;
                        double blue = (rgb & 0xFF) / 255.0;
                        graphicsContext.setFill(Color.color(red, green, blue, a));
                        double x = col * (previewCanvas.getWidth() / cols);
                        double y = row * (previewCanvas.getHeight() / rows);
                        double sz = previewCanvas.getWidth() / cols;
                        graphicsContext.fillRect(x + 1, y + 1, sz - 1, sz - 1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onStartClicked() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("mainBanner.fxml")
        );
        Scene scene = new Scene(loader.load());

        Stage stage = (Stage) startButton.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Редактор вишивки");
        stage.setResizable(false);
    }
}
