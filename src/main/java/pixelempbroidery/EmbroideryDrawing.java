package pixelempbroidery;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EmbroideryDrawing implements Initializable {
    @FXML
    private Canvas drawingCanvas;

    //Left Part
    @FXML private ColorPicker colorPicker;
    @FXML private Spinner<Integer> row;
    @FXML private Spinner<Integer> col;
    @FXML private ToggleButton eraserButton;

    //Right Part
    @FXML private ToggleButton horizontslButton;
    @FXML private ToggleButton verticalButton;
    @FXML private ToggleButton bothButton;


    private int ROW = 25;
    private int COL = 25;
    private int[][] RowAndCol = new int[ROW][COL];

    private int color = 0xFFCC0000; // red for start
    private boolean erase = false;

    public void initialize(URL url, ResourceBundle resourceBundle) {
        row.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 50, 25));
        col.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 50, 25));

        colorPicker.setValue(Color.rgb(204, 0, 0));
        colorPicker.setOnAction(e -> {
            color = colorToARGB(colorPicker.getValue());
            erase = false;
            if (eraserButton != null) eraserButton.setSelected(false);
        });
        draw(); //first - empty grid
        drawingCanvas.setOnMousePressed(e -> handleMouse(e.getX(), e.getY()));
        drawingCanvas.setOnMouseDragged(e -> handleMouse(e.getX(), e.getY()));
    }
    private int colorToARGB(Color c) {
        int r = (int)(c.getRed() * 255);
        int g = (int)(c.getGreen() * 255);
        int b = (int)(c.getBlue() * 255);
        return (0xFF << 24) | (r << 16) | (g << 8) | b;
    }
    private void handleMouse(double x, double y) {
        int col = (int) (x / cellSize());
        int row = (int) (y / cellSize());
        if (!inTheBorder(row, col)) return;
        int paintColor = erase ? 0 : this.color;
        symmetryPaint(row, col, paintColor);
        draw();
    }
    private boolean inTheBorder(int row, int col) {
        return row >= 0 && row < ROW && col >= 0 && col < COL;
    }
    private void symmetryPaint(int row, int col, int color) {
        RowAndCol[row][col] = color;

        if (horizontslButton != null && horizontslButton.isSelected()) RowAndCol[row][COL - 1 - col] = color;
        if (verticalButton != null && verticalButton.isSelected()) RowAndCol[ROW - 1 - row][col] = color;
        if (bothButton != null && bothButton.isSelected()) {
            RowAndCol[row][COL - 1 - col] = color;
            RowAndCol[ROW - 1 - row][col] = color;
            RowAndCol[ROW - 1 - row][COL - 1 - col] = color;
        }
    }



    @FXML
    private void sizeChange() {
        int newRow = row.getValue();
        int newCol = col.getValue();

        int[][] oldGrid = RowAndCol;
        int oldRow = ROW;
        int oldCol = COL;

        ROW = newRow;
        COL = newCol;
        RowAndCol = new int[ROW][COL];

        for (int row = 0; row < Math.min(oldRow, ROW); row++)
            for (int col = 0; col < Math.min(oldCol, COL); col++)
                RowAndCol[row][col] = oldGrid[row][col];

        drawingCanvas.setWidth(COL * cellSize());
        drawingCanvas.setHeight(ROW * cellSize());
        draw();
    }
    private double cellSize() {
        return Math.min(
                drawingCanvas.getWidth() / COL,
                drawingCanvas.getHeight() / ROW
        );
    }

    @FXML
    private void eraserToggle() {
        erase = eraserButton.isSelected();
    }

    @FXML
    private void savePNG() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Зберегти схему вишивки");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG зображення", "*.png"));
        chooser.setInitialFileName("embroidery.png");

        Stage stage = (Stage) drawingCanvas.getScene().getWindow();
        File file = chooser.showSaveDialog(stage);
        if (file == null) return;

        int scale = 10;
        BufferedImage img = new BufferedImage(COL * scale, ROW * scale, BufferedImage.TYPE_INT_ARGB);

        for (int row = 0; row < ROW; row++) {
            for (int col = 0; col < COL; col++) {
                int argb = (RowAndCol[row][col] == 0) ? 0xFFFFFFFF : RowAndCol[row][col];
                for (int deltaY = 0; deltaY < scale; deltaY++)
                    for (int deltaX = 0; deltaX < scale; deltaX++)
                        img.setRGB(col * scale + deltaX, row * scale + deltaY, argb);
            }
        }

        try {
            ImageIO.write(img, "PNG", file);
        } catch (IOException e) {
            errorPNG("Помилка збереження: " + e.getMessage());
        }
    }
    private void errorPNG(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }

    @FXML
    private void loadPNG() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Відкрити схему вишивки");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG зображення", "*.png"));

        Stage stage = (Stage) drawingCanvas.getScene().getWindow();
        File file = chooser.showOpenDialog(stage);
        if (file == null) return;

        try {
            BufferedImage img = ImageIO.read(file);
            int SCALE = 10;
            int newCols = img.getWidth() / SCALE;
            int newRows = img.getHeight() / SCALE;

            ROW = newRows;
            COL = newCols;
            RowAndCol = new int[ROW][COL];

            for (int row = 0; row < ROW; row++) {
                for (int col = 0; col < COL; col++) {
                    int rgb = img.getRGB(col * SCALE + SCALE / 2,
                            row * SCALE + SCALE / 2);
                    RowAndCol[row][col] = (rgb == 0xFFFFFFFF || rgb == -1) ? 0 : rgb;
                }
            }
            drawingCanvas.setWidth(COL * cellSize());
            drawingCanvas.setHeight(ROW * cellSize());
            draw();

        } catch (IOException e) {
            errorPNG("Помилка відкриття: " + e.getMessage());
        }
    }

    @FXML
    private void clearScrin() {
        RowAndCol = new int[ROW][COL];
        draw();
    }

    private void draw() {
        GraphicsContext graphicsContext = drawingCanvas.getGraphicsContext2D();
        double cellSize = cellSize();

        graphicsContext.setFill(Color.WHITE);
        graphicsContext.fillRect(0, 0, drawingCanvas.getWidth(), drawingCanvas.getHeight());

        for (int row = 0; row < ROW; row++) {
            for (int col = 0; col < COL; col++) {
                double x = col * cellSize;
                double y = row * cellSize;

                if (RowAndCol[row][col] != 0) {
                    graphicsContext.setFill(argbToColor(RowAndCol[row][col]));
                    graphicsContext.fillRect(x + 1, y + 1, cellSize - 1, cellSize - 1);
                }

                graphicsContext.setStroke(Color.rgb(200, 200, 200));
                graphicsContext.setLineWidth(0.5);
                graphicsContext.strokeRect(x, y, cellSize, cellSize);
            }
        }
    }

    private Color argbToColor(int argb) {
        double r = ((argb >> 16) & 0xFF) / 255.0;
        double g = ((argb >>  8) & 0xFF) / 255.0;
        double b = ( argb & 0xFF) / 255.0;
        return Color.color(r, g, b);
    }
}
