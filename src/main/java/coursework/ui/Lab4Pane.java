package coursework.ui;

import coursework.lab4.StreamTasks;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class Lab4Pane extends BorderPane {

    private enum MethodKind {
        AVERAGE(
                "Среднее значение",
                "Целые числа через пробел, запятую или с новой строки. Пустой список даёт 0.0.",
                "1 2 3 4"),
        PREFIX(
                "Верхний регистр и префикс _new_",
                "Строки, по одной на строку.",
                "hello\nworld"),
        SQUARES(
                "Квадраты единственных элементов",
                "Целые числа. В результат входят квадраты только тех чисел, которые встретились один раз.",
                "1 2 2 3 4 4 5"),
        LAST(
                "Последний элемент",
                "Элементы коллекции, по одному на строку. Пустое поле оставляет коллекцию пустой, метод бросает исключение.",
                "a\nb\nc"),
        EVENS(
                "Сумма чётных",
                "Целые числа. Суммируются чётные. Если чётных нет, результат 0. Проверка: 1 3 5.",
                "1 2 3 4 5 6"),
        MAP(
                "Map по первому символу",
                "Непустые строки, по одной на строку. Ключ: первый символ, значение: остаток. Повтор ключа даёт исключение.",
                "apple\nbanana");

        private final String title;
        private final String hint;
        private final String example;

        MethodKind(String title, String hint, String example) {
            this.title = title;
            this.hint = hint;
            this.example = example;
        }

        private String title() {
            return title;
        }

        private String hint() {
            return hint;
        }

        private String example() {
            return example;
        }

        @Override
        public String toString() {
            return title;
        }
    }

    private final ComboBox<MethodKind> methodBox = new ComboBox<>();
    private final Label hint = new Label();
    private final TextArea input = new TextArea();
    private final OutputPane output = new OutputPane();

    public Lab4Pane() {
        methodBox.getItems().addAll(MethodKind.values());
        methodBox.setMaxWidth(Double.MAX_VALUE);
        methodBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, previous, current) -> showMethod(current));
        methodBox.setValue(MethodKind.AVERAGE);

        input.setWrapText(true);
        input.setPrefRowCount(6);

        hint.getStyleClass().add("hint");
        hint.setWrapText(true);

        Button example = new Button("Пример");
        example.setOnAction(event -> input.setText(selected().example()));
        Button run = new Button("Выполнить");
        run.setDefaultButton(true);
        run.setOnAction(event -> runSelected());

        VBox form = new VBox(
                8,
                new Label("Метод"),
                methodBox,
                hint,
                new Label("Входные данные"),
                input,
                new HBox(8, run, example));
        form.getStyleClass().add("form-card");
        form.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(input, Priority.NEVER);

        setPadding(new Insets(12));
        setTop(form);
        setCenter(output);
        BorderPane.setMargin(output, new Insets(12, 0, 0, 0));

        output.write("Задание 4. Методы Stream API.");
        showMethod(MethodKind.AVERAGE);
    }

    private void showMethod(MethodKind kind) {
        if (kind == null) {
            hint.setText("");
            input.setPromptText("");
            return;
        }
        hint.setText(kind.hint());
        input.setPromptText(kind.example());
    }

    private MethodKind selected() {
        MethodKind kind = methodBox.getValue();
        if (kind == null) {
            throw new IllegalArgumentException("Выберите метод.");
        }
        return kind;
    }

    private void runSelected() {
        MethodKind kind;
        try {
            kind = selected();
        } catch (IllegalArgumentException e) {
            output.write(e.getMessage());
            return;
        }
        try {
            String shown;
            String result;
            switch (kind) {
                case AVERAGE:
                    List<Integer> averageNumbers = parseInts(input.getText());
                    shown = averageNumbers.toString();
                    result = Double.toString(StreamTasks.average(averageNumbers));
                    break;
                case PREFIX:
                    List<String> lines = parseLines(input.getText());
                    shown = lines.toString();
                    result = StreamTasks.uppercaseWithPrefix(lines).toString();
                    break;
                case SQUARES:
                    List<Integer> uniqueNumbers = parseInts(input.getText());
                    shown = uniqueNumbers.toString();
                    result = StreamTasks.squaresOfUnique(uniqueNumbers).toString();
                    break;
                case LAST:
                    List<String> items = parseLines(input.getText());
                    shown = items.toString();
                    result = String.valueOf(StreamTasks.lastElement(items));
                    break;
                case EVENS:
                    List<Integer> evens = parseInts(input.getText());
                    int[] array = evens.stream().mapToInt(Integer::intValue).toArray();
                    shown = Arrays.toString(array);
                    result = Integer.toString(StreamTasks.sumOfEvens(array));
                    break;
                case MAP:
                    List<String> words = parseLines(input.getText());
                    shown = words.toString();
                    Map<Character, String> mapped = StreamTasks.toMapByFirstChar(words);
                    result = mapped.toString();
                    break;
                default:
                    throw new IllegalStateException("Неизвестный метод");
            }
            output.write("Метод: " + kind.title());
            output.write("Вход: " + shown);
            output.write("Результат: " + result);
            output.write("");
        } catch (RuntimeException e) {
            output.write("Метод: " + kind.title());
            output.write("Вход: " + oneLine(input.getText()));
            output.write("Ошибка: " + (e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage()));
            output.write("");
        }
    }

    private static List<Integer> parseInts(String text) {
        String trimmed = text == null ? "" : text.trim();
        if (trimmed.isEmpty()) {
            return List.of();
        }
        String[] parts = trimmed.split("[,;\\s]+");
        List<Integer> numbers = new ArrayList<>();
        for (String part : parts) {
            try {
                numbers.add(Integer.parseInt(part));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Нужно целое число, а не «" + part + "».");
            }
        }
        return numbers;
    }

    private static List<String> parseLines(String text) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return lines;
        }
        for (String line : text.split("\\R", -1)) {
            if (!line.isBlank()) {
                lines.add(line);
            }
        }
        return lines;
    }

    private static String oneLine(String text) {
        if (text == null || text.isBlank()) {
            return "[]";
        }
        return text.replace("\r", "").replace("\n", " / ");
    }
}
