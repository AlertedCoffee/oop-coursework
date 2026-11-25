module coursework {
    requires javafx.controls;

    exports coursework;
    opens coursework to javafx.graphics;
}
