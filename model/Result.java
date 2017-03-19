package ru.alexsumin.model;

/**
 * Created by alex on 15.03.17. Edited by Anton on 18.03.17.
 */
public class Result {
    private double step;
    private double temperature;
    private double viscosity;

    public Result(double step, double temperature, double viscosity) {
        this.step = step;
        this.temperature = temperature;
        this.viscosity = viscosity;
    }

    public double getStep() {
        return step;
    }

    public void setStep(double step) {
        this.step = step;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getViscosity() {
        return viscosity;
    }

    public void setViscosity(double viscosity) {
        this.viscosity = viscosity;
    }

    @Override
    public String toString() {
        return "Result {" +
                "currLength = " + Math.rint(step * 10) / 10 +
                ", temperature = " + Math.rint(temperature * 10) / 10 +
                ", viscosity = " + Math.round(viscosity) +
                '}';
    }
}
