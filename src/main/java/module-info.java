module com.example.pixelempbroidery {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens pixelempbroidery to javafx.fxml;
    exports pixelempbroidery;
}