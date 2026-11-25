package coursework.lab1;

import coursework.io.LineWriter;
import coursework.lab1.movement.MoveStrategy;

public class Hero {

    private String name;
    private MoveStrategy moveStrategy;

    public Hero(String name) {
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Имя не задано");
        }
        this.name = name.trim();
    }

    public MoveStrategy getMoveStrategy() {
        return moveStrategy;
    }

    public void setMoveStrategy(MoveStrategy moveStrategy) {
        if (moveStrategy == null) {
            throw new IllegalArgumentException("Способ перемещения не задан");
        }
        this.moveStrategy = moveStrategy;
    }

    public String describe() {
        String strategyName = moveStrategy == null ? "не выбран" : moveStrategy.getName();
        return String.format("Герой: %s | Способ: %s", name, strategyName);
    }

    public void move(String from, String to, LineWriter out) {
        if (moveStrategy == null) {
            out.write("Сначала выберите способ перемещения.");
            return;
        }
        String action = moveStrategy.move(from, to);
        out.write(String.format("%s (%s): %s", name, moveStrategy.getName(), action));
    }
}
