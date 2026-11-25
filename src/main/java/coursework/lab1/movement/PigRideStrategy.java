package coursework.lab1.movement;

public class PigRideStrategy implements MoveStrategy {

    @Override
    public String move(String from, String to) {
        return String.format(
                "Герой едет на свинье из «%s» в «%s». Свинья хрюкает, но довозит.",
                from,
                to);
    }

    @Override
    public String getName() {
        return "на свинье";
    }
}
