package coursework.ui;

import coursework.lab1.Hero;
import coursework.lab1.movement.HorseRideStrategy;
import coursework.lab1.movement.MoveStrategy;
import coursework.lab1.movement.PigRideStrategy;
import coursework.lab1.movement.WalkStrategy;
import coursework.lab1.movement.WingsFlyStrategy;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public final class Lab1Pane extends BorderPane {

    private final Hero hero = new Hero("Безымянный");
    private final TextField nameField = new TextField(hero.getName());
    private final ComboBox<MoveStrategy> strategyBox = new ComboBox<>();
    private final TextField fromField = new TextField();
    private final TextField toField = new TextField();
    private final Label status = new Label();
    private final OutputPane output = new OutputPane();

    public Lab1Pane() {
        strategyBox.getItems().addAll(
                new WalkStrategy(),
                new HorseRideStrategy(),
                new PigRideStrategy(),
                new WingsFlyStrategy());
        strategyBox.setMaxWidth(Double.MAX_VALUE);
        strategyBox.setCellFactory(list -> strategyCell());
        strategyBox.setButtonCell(strategyCell());
        strategyBox.getSelectionModel().selectFirst();
        hero.setMoveStrategy(strategyBox.getValue());

        fromField.setPromptText("откуда");
        toField.setPromptText("куда");

        status.getStyleClass().add("status-line");
        refreshStatus();

        Button rename = new Button("Задать имя");
        rename.setOnAction(event -> renameHero());
        rename.setMaxWidth(Double.MAX_VALUE);
        Button choose = new Button("Выбрать способ");
        choose.setOnAction(event -> chooseStrategy());
        choose.setMaxWidth(Double.MAX_VALUE);
        Button move = new Button("Переместиться");
        move.setOnAction(event -> moveHero());

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        grid.add(new Label("Имя"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(rename, 2, 0);
        grid.add(new Label("Способ"), 0, 1);
        grid.add(strategyBox, 1, 1);
        grid.add(choose, 2, 1);
        grid.add(new Label("Откуда"), 0, 2);
        grid.add(fromField, 1, 2);
        grid.add(new Label("Куда"), 0, 3);
        grid.add(toField, 1, 3);
        GridPane.setHgrow(nameField, Priority.ALWAYS);
        GridPane.setHgrow(strategyBox, Priority.ALWAYS);
        GridPane.setHgrow(fromField, Priority.ALWAYS);
        GridPane.setHgrow(toField, Priority.ALWAYS);
        GridPane.setFillWidth(rename, true);
        GridPane.setFillWidth(choose, true);

        Label hint = new Label(
                "Способ хранится в герое и подменяется другим объектом MoveStrategy. "
                        + "Следующее перемещение идёт уже новым способом.");
        hint.getStyleClass().add("hint");
        hint.setWrapText(true);

        VBox form = new VBox(8, status, grid, new HBox(move), hint);
        form.getStyleClass().add("form-card");
        form.setMaxWidth(Double.MAX_VALUE);

        setPadding(new Insets(12));
        setTop(form);
        setCenter(output);
        BorderPane.setMargin(output, new Insets(12, 0, 0, 0));

        output.write("Задание 1. Перемещение героя, паттерн «стратегия».");
        output.write(hero.describe());
    }

    private void renameHero() {
        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        if (name.isEmpty()) {
            output.write("Строка не может быть пустой.");
            return;
        }
        hero.setName(name);
        refreshStatus();
        output.write("Имя обновлено.");
        output.write(hero.describe());
    }

    private void chooseStrategy() {
        MoveStrategy strategy = strategyBox.getValue();
        if (strategy == null) {
            output.write("Выберите способ перемещения.");
            return;
        }
        hero.setMoveStrategy(strategy);
        refreshStatus();
        output.write("Выбран способ: " + strategy.getName() + ".");
        output.write(hero.describe());
    }

    private void moveHero() {
        String from = textOf(fromField);
        String to = textOf(toField);
        if (from.isEmpty() || to.isEmpty()) {
            output.write("Строка не может быть пустой.");
            return;
        }
        hero.move(from, to, output);
        refreshStatus();
    }

    private void refreshStatus() {
        status.setText(hero.describe());
    }

    private static String textOf(TextField field) {
        return field.getText() == null ? "" : field.getText().trim();
    }

    private static ListCell<MoveStrategy> strategyCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(MoveStrategy item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        };
    }
}
