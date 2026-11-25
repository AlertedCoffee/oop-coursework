package coursework.lab1.movement;

public class WalkStrategy implements MoveStrategy {

    @Override
    public String move(String from, String to) {
        return String.format(
                "Герой идёт пешком из «%s» в «%s». Это медленно, зато бесплатно.",
                from,
                to);
    }

    @Override
    public String getName() {
        return "пешком";
    }
}
