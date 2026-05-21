module com.example.pixelempbroidery {
    requires javafx.controls;
    requires javafx.fxml;


    opens pixelempbroidery to javafx.fxml;
    exports pixelempbroidery;
}