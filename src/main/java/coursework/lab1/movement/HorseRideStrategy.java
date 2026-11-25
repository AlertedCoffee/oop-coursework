package coursework.lab1.movement;

public class HorseRideStrategy implements MoveStrategy {

    @Override
    public String move(String from, String to) {
        return String.format(
                "Герой скачет на лошади из «%s» в «%s». Путь пройден быстро.",
                from,
                to);
    }

    @Override
    public String getName() {
        return "на лошади";
    }
}
