package coursework.ui;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public final class MainWindow {

    public void show(Stage stage) {
        Lab1Pane lab1 = new Lab1Pane();
        Lab2Pane lab2 = new Lab2Pane();
        Lab3Pane lab3 = new Lab3Pane();
        Lab4Pane lab4 = new Lab4Pane();

        ListView<TaskItem> tasks = new ListView<>();
        tasks.getStyleClass().add("task-list");
        tasks.setPrefWidth(240);
        tasks.setMinWidth(200);
        tasks.getItems().addAll(
                new TaskItem("1. Перемещение героя", lab1),
                new TaskItem("2. Аннотации", lab2),
                new TaskItem("3. Переводчик", lab3),
                new TaskItem("4. Stream API", lab4));

        BorderPane root = new BorderPane();
        root.setTop(header());
        root.setLeft(tasks);
        root.setCenter(lab1);

        tasks.getSelectionModel().selectedItemProperty().addListener((observable, previous, current) -> {
            if (current != null) {
                root.setCenter(current.pane());
            }
        });
        tasks.getSelectionModel().selectFirst();

        Scene scene = new Scene(root, 980, 680);
        java.net.URL css = MainWindow.class.getResource("/app.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }
        stage.setTitle("Курсовая работа");
        stage.setMinWidth(860);
        stage.setMinHeight(560);
        stage.setScene(scene);
        stage.show();
    }

    private static VBox header() {
        Label title = new Label("Курсовая работа");
        title.getStyleClass().add("header-title");
        Label subtitle = new Label("Выбор задания, ввод данных, вывод в защищённое поле");
        subtitle.getStyleClass().add("header-subtitle");
        VBox box = new VBox(title, subtitle);
        box.getStyleClass().add("header-bar");
        return box;
    }

    private record TaskItem(String title, Parent pane) {
        @Override
        public String toString() {
            return title;
        }
    }
}
