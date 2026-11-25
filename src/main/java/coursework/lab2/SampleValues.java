package coursework.lab2;

public final class SampleValues {

    private final String text;
    private final int number;
    private final double real;
    private final boolean flag;
    private final Fuel fuel;

    public SampleValues(String text, int number, double real, boolean flag, Fuel fuel) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Строковый аргумент пуст");
        }
        if (!Double.isFinite(real)) {
            throw new IllegalArgumentException("Вещественный аргумент должен быть конечным числом");
        }
        if (fuel == null) {
            throw new IllegalArgumentException("Топливо не задано");
        }
        this.text = text;
        this.number = number;
        this.real = real;
        this.flag = flag;
        this.fuel = fuel;
    }

    public String text() {
        return text;
    }

    public int number() {
        return number;
    }

    public double real() {
        return real;
    }

    public boolean flag() {
        return flag;
    }

    public Fuel fuel() {
        return fuel;
    }
}
