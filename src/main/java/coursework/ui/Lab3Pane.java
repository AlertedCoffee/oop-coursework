package coursework.ui;

import coursework.lab3.dictionary.Dictionary;
import coursework.lab3.dictionary.DictionaryReader;
import coursework.lab3.exception.FileReadException;
import coursework.lab3.exception.InvalidFileFormatException;
import coursework.lab3.translator.Translator;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Optional;

public final class Lab3Pane extends BorderPane {

    private static final String SAMPLE_SENTENCE = "dog look to the window, dog look forward";

    private final TextField dictionaryField = new TextField();
    private final TextField textFileField = new TextField();
    private final TextArea manualText = new TextArea(SAMPLE_SENTENCE);
    private final RadioButton manualRadio = new RadioButton("Ввести текст");
    private final RadioButton fileRadio = new RadioButton("Файл с текстом");
    private final OutputPane output = new OutputPane();
    private Path lastDirectory = initialDirectory();

    public Lab3Pane() {
        dictionaryField.setPromptText("файл словаря не выбран");
        textFileField.setPromptText("файл текста не выбран");
        textFileField.setEditable(false);
        manualText.setWrapText(true);
        manualText.setPrefRowCount(5);
        manualText.setPromptText("Текст для перевода");

        ToggleGroup source = new ToggleGroup();
        manualRadio.setToggleGroup(source);
        fileRadio.setToggleGroup(source);
        manualRadio.setSelected(true);

        Button dictionaryButton = new Button("Выбрать словарь");
        dictionaryButton.setOnAction(event -> chooseDictionary());
        Button textButton = new Button("Выбрать текст");
        textButton.setOnAction(event -> chooseTextFile());
        Button exampleButton = new Button("Пример");
        exampleButton.setOnAction(event -> loadExample());
        Button translateButton = new Button("Перевести");
        translateButton.setDefaultButton(true);
        translateButton.setOnAction(event -> translate());

        HBox dictionaryRow = new HBox(8, dictionaryField, dictionaryButton);
        HBox.setHgrow(dictionaryField, Priority.ALWAYS);
        HBox textFileRow = new HBox(8, textFileField, textButton);
        HBox.setHgrow(textFileField, Priority.ALWAYS);

        VBox manualBox = new VBox(8, manualText);
        VBox fileBox = new VBox(8, textFileRow);
        manualBox.managedProperty().bind(manualBox.visibleProperty());
        manualBox.visibleProperty().bind(manualRadio.selectedProperty());
        fileBox.managedProperty().bind(fileBox.visibleProperty());
        fileBox.visibleProperty().bind(fileRadio.selectedProperty());

        Label hint = new Label(
                "Словарь: «слово или выражение | перевод». "
                        + "Регистр не учитывается. Если подходят несколько фраз, берётся самая длинная левая часть. "
                        + "Слова без перевода остаются как есть.");
        hint.getStyleClass().add("hint");
        hint.setWrapText(true);

        VBox form = new VBox(
                8,
                new Label("Словарь"),
                dictionaryRow,
                new HBox(16, manualRadio, fileRadio),
                manualBox,
                fileBox,
                new HBox(8, translateButton, exampleButton),
                hint);
        form.getStyleClass().add("form-card");
        form.setMaxWidth(Double.MAX_VALUE);

        setPadding(new Insets(12));
        setTop(form);
        setCenter(output);
        BorderPane.setMargin(output, new Insets(12, 0, 0, 0));

        output.write("Задание 3. Переводчик.");
        output.write("Пример фразы уже стоит в поле текста. Кнопка «Пример» подставляет словарь.");
    }

    private void chooseDictionary() {
        Path path = chooseFile("Словарь");
        if (path != null) {
            dictionaryField.setText(path.toString());
        }
    }

    private void chooseTextFile() {
        Path path = chooseFile("Текст для перевода");
        if (path != null) {
            textFileField.setText(path.toString());
            fileRadio.setSelected(true);
        }
    }

    private Path chooseFile(String title) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Текст", "*.txt"));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Все файлы", "*.*"));
        if (lastDirectory != null && Files.isDirectory(lastDirectory)) {
            chooser.setInitialDirectory(lastDirectory.toFile());
        }
        File file = chooser.showOpenDialog(getScene() == null ? null : getScene().getWindow());
        if (file == null) {
            return null;
        }
        Path path = file.toPath();
        Path parent = path.getParent();
        if (parent != null) {
            lastDirectory = parent;
        }
        return path;
    }

    private void loadExample() {
        Optional<Path> sample = sampleDictionary();
        if (sample.isEmpty()) {
            output.write("Файл примера не найден: src/main/resources/samples/dictionary.txt");
            return;
        }
        dictionaryField.setText(sample.get().toString());
        manualText.setText(SAMPLE_SENTENCE);
        manualRadio.setSelected(true);
        output.write("Подставлен пример словаря: " + sample.get());
    }

    private void translate() {
        try {
            Path dictionaryPath = toPath(dictionaryField.getText(), "Путь к файлу словаря не задан.");
            Dictionary dictionary = new DictionaryReader().read(dictionaryPath);
            String text = readSourceText();
            if (text.isBlank()) {
                output.write("Введите текст для перевода или выберите файл.");
                return;
            }
            String translation = new Translator(dictionary).translate(text);
            output.write("Словарь: " + dictionaryPath);
            output.write("Словарь загружен (" + dictionary.size() + ").");
            if (fileRadio.isSelected()) {
                output.write("Файл текста: " + textFileField.getText().trim());
            } else {
                output.write("Текст введён вручную.");
            }
            output.write(translation.isEmpty() ? "(пустая строка)" : translation);
            output.write("");
        } catch (FileReadException | InvalidFileFormatException e) {
            output.write(e.getMessage());
            output.write("");
        }
    }

    private String readSourceText() throws FileReadException {
        if (manualRadio.isSelected()) {
            return manualText.getText() == null ? "" : manualText.getText();
        }
        Path path = toPath(textFileField.getText(), "Путь к файлу текста не задан.");
        if (!Files.exists(path)) {
            throw new FileReadException("Файл не существует: " + path);
        }
        if (Files.isDirectory(path)) {
            throw new FileReadException("Ожидался файл, но указан каталог: " + path);
        }
        if (!Files.isReadable(path)) {
            throw new FileReadException("Нет доступа к файлу: " + path);
        }
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new FileReadException("Не удалось прочитать файл: " + path, e);
        }
    }

    private static Path toPath(String text, String emptyMessage) throws FileReadException {
        String value = text == null ? "" : text.trim();
        if (value.isEmpty()) {
            throw new FileReadException(emptyMessage);
        }
        try {
            return Path.of(value);
        } catch (InvalidPathException e) {
            throw new FileReadException("Некорректный путь к файлу: " + value, e);
        }
    }

    private static Path initialDirectory() {
        Path lab = Path.of("..", "oop-lab-03");
        if (Files.isDirectory(lab)) {
            return lab.toAbsolutePath();
        }
        Path samples = Path.of("src", "main", "resources", "samples");
        if (Files.isDirectory(samples)) {
            return samples.toAbsolutePath();
        }
        return Path.of("").toAbsolutePath();
    }

    private static Optional<Path> sampleDictionary() {
        Path[] candidates = {
                Path.of("src", "main", "resources", "samples", "dictionary.txt"),
                Path.of("samples", "dictionary.txt")
        };
        for (Path candidate : candidates) {
            if (Files.isRegularFile(candidate)) {
                return Optional.of(candidate.toAbsolutePath());
            }
        }
        return Optional.empty();
    }
}
