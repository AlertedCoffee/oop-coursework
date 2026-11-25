package coursework.ui;

import coursework.lab2.Automobile;
import coursework.lab2.Fuel;
import coursework.lab2.MethodInvoker;
import coursework.lab2.SampleValues;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public final class Lab2Pane extends BorderPane {

    private final TextField registrationField = new TextField("А000АА178");
    private final TextField textField = new TextField("значение");
    private final TextField numberField = new TextField("2");
    private final TextField realField = new TextField("11.7");
    private final CheckBox flagBox = new CheckBox("boolean");
    private final ComboBox<Fuel> fuelBox = new ComboBox<>();
    private final OutputPane output = new OutputPane();

    public Lab2Pane() {
        flagBox.setSelected(true);
        fuelBox.getItems().addAll(Fuel.values());
        fuelBox.setValue(Fuel.PETROL);
        fuelBox.setMaxWidth(Double.MAX_VALUE);

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        grid.add(new Label("Регистрационный номер"), 0, 0);
        grid.add(registrationField, 1, 0);
        grid.add(new Label("String"), 0, 1);
        grid.add(textField, 1, 1);
        grid.add(new Label("int и long"), 0, 2);
        grid.add(numberField, 1, 2);
        grid.add(new Label("double"), 0, 3);
        grid.add(realField, 1, 3);
        grid.add(new Label("Fuel"), 0, 4);
        grid.add(fuelBox, 1, 4);
        grid.add(flagBox, 1, 5);
        GridPane.setHgrow(registrationField, Priority.ALWAYS);
        GridPane.setHgrow(textField, Priority.ALWAYS);
        GridPane.setHgrow(numberField, Priority.ALWAYS);
        GridPane.setHgrow(realField, Priority.ALWAYS);
        GridPane.setHgrow(fuelBox, Priority.ALWAYS);

        Button run = new Button("Выполнить");
        run.setDefaultButton(true);
        run.setOnAction(event -> runInvocation());

        Label hint = new Label(
                "MethodInvoker не знает имён методов. Образцы подставляются по типу параметра. "
                        + "Вызываются только protected и private методы с @Repeat. "
                        + "public-метод drive аннотирован и будет пропущен.");
        hint.getStyleClass().add("hint");
        hint.setWrapText(true);

        VBox form = new VBox(8, grid, new HBox(run), hint);
        form.getStyleClass().add("form-card");
        form.setMaxWidth(Double.MAX_VALUE);

        setPadding(new Insets(12));
        setTop(form);
        setCenter(output);
        BorderPane.setMargin(output, new Insets(12, 0, 0, 0));

        output.write("Задание 2. Аннотация @Repeat и вызов скрытых методов.");
    }

    private void runInvocation() {
        try {
            String registration = registrationField.getText() == null ? "" : registrationField.getText().trim();
            SampleValues samples = new SampleValues(
                    textField.getText(),
                    parseInt(numberField.getText()),
                    parseDouble(realField.getText()),
                    flagBox.isSelected(),
                    fuelBox.getValue());
            Automobile car = new Automobile(registration);
            car.setOutput(output);
            output.write("Создан " + registration + ".");
            MethodInvoker.invokeAnnotatedHidden(car, output, samples);
            output.write(car.toString());
            output.write("");
        } catch (RuntimeException e) {
            output.write("Ошибка: " + messageOf(e));
            output.write("");
        }
    }

    private static int parseInt(String text) {
        String value = text == null ? "" : text.trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Введите целое число.");
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Нужно целое число, а не «" + value + "».");
        }
    }

    private static double parseDouble(String text) {
        String value = text == null ? "" : text.trim().replace(',', '.');
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Введите вещественное число.");
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Нужно вещественное число, а не «" + text.trim() + "».");
        }
    }

    private static String messageOf(RuntimeException e) {
        return e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
    }
}
