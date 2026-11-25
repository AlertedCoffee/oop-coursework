package coursework.lab2;

import coursework.io.LineWriter;

public class Automobile {

    private final String registration;
    private double fuelLiters;
    private double mileageKm;
    private double speedKmh;
    private int gear;
    private LineWriter output;

    public Automobile(String registration) {
        if (registration == null || registration.isBlank()) {
            throw new IllegalArgumentException("Нужен регистрационный номер");
        }
        this.registration = registration.trim();
        this.fuelLiters = 50;
        this.speedKmh = 60;
        this.gear = 1;
    }

    public void setOutput(LineWriter output) {
        if (output == null) {
            throw new IllegalArgumentException("Вывод не задан");
        }
        this.output = output;
    }

    public void startEngine(String ignitionKey, int attempts) {
        write(String.format("  запуск ключом «%s», попыток: %d", ignitionKey, attempts));
    }

    @Repeat(7)
    public void drive(double distanceKm, int speedKmh) {
        this.speedKmh = speedKmh;
        this.mileageKm += distanceKm;
        write(String.format("  поездка %.1f км на %d км/ч", distanceKm, speedKmh));
    }

    public void refuel(Fuel fuel, double liters) {
        this.fuelLiters += liters;
        write(String.format("  заправка %s на %.1f л, бак %.1f л", fuel, liters, fuelLiters));
    }

    @Repeat(2)
    protected void shiftGear(int gear, double rpm) {
        this.gear = Math.clamp(gear, 1, 5);
        write(String.format("  передача %d, обороты %.1f", this.gear, rpm));
    }

    @Repeat(3)
    protected void brake(double deceleration, boolean absEnabled) {
        speedKmh = Math.max(0, speedKmh - deceleration);
        write(String.format(
                "  торможение %.1f, ABS %s, скорость %.1f км/ч",
                deceleration,
                absEnabled ? "вкл" : "выкл",
                speedKmh));
    }

    protected void warmUp(int seconds, double idleRpm) {
        write(String.format("  прогрев %d с при %.1f об/мин", seconds, idleRpm));
    }

    @Repeat(4)
    private void consumeFuel(double liters, double kmPerLiter) {
        fuelLiters = Math.max(0, fuelLiters - liters);
        mileageKm += liters * kmPerLiter;
        write(String.format(
                "  расход %.1f л, пробег %.1f км, бак %.1f л",
                liters,
                mileageKm,
                fuelLiters));
    }

    @Repeat(1)
    private void registerService(String station, long odometer, Fuel fuel) {
        write(String.format("  ТО: %s, одометр %d, топливо %s", station, odometer, fuel));
    }

    private void checkTirePressure(double pressureBar, int wheelIndex) {
        write(String.format("  давление %.1f бар, колесо %d", pressureBar, wheelIndex));
    }

    @Override
    public String toString() {
        return String.format(
                "Automobile[%s]: бак %.1f л, пробег %.1f км, скорость %.1f км/ч, передача %d",
                registration,
                fuelLiters,
                mileageKm,
                speedKmh,
                gear);
    }

    private void write(String line) {
        if (output == null) {
            throw new IllegalStateException("Вывод не подключён");
        }
        output.write(line);
    }
}
