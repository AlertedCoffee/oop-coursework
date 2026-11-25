package coursework;

import coursework.ui.MainWindow;
import javafx.application.Application;
import javafx.stage.Stage;

public final class CourseworkApp extends Application {

    @Override
    public void start(Stage stage) {
        new MainWindow().show(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
