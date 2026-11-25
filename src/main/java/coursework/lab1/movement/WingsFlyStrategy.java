package coursework.lab1.movement;

public class WingsFlyStrategy implements MoveStrategy {

    @Override
    public String move(String from, String to) {
        return String.format(
                "Герой расправляет крылья и летит из «%s» в «%s» над препятствиями.",
                from,
                to);
    }

    @Override
    public String getName() {
        return "полёт на крыльях";
    }
}
