package coursework.ui;

import coursework.io.LineWriter;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public final class OutputPane extends VBox implements LineWriter {

    private final TextArea area = new TextArea();

    public OutputPane() {
        Label title = new Label("Вывод");
        title.getStyleClass().add("section-title");
        Button clear = new Button("Очистить");
        clear.setOnAction(event -> area.clear());

        HBox header = new HBox(8, title, clear);
        header.setAlignment(Pos.CENTER_LEFT);

        area.setEditable(false);
        area.setWrapText(true);
        area.setMinHeight(160);
        area.getStyleClass().add("output-area");
        area.setPromptText("Результат появится здесь. Поле защищено от редактирования.");

        getStyleClass().add("form-card");
        setSpacing(6);
        setMaxWidth(Double.MAX_VALUE);
        setMaxHeight(Double.MAX_VALUE);
        getChildren().addAll(header, area);
        VBox.setVgrow(area, Priority.ALWAYS);
    }

    @Override
    public void write(String line) {
        area.appendText(line == null ? "\n" : line + "\n");
        area.positionCaret(area.getLength());
    }
}
