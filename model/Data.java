package ru.alexsumin.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 15.03.17. Edited by Anton on 18.03.17.
 */
public class Data {
    /*
    На интерфейсе:
    1) не делать сокращений;
    2) указать единицы измерения всех величин;
    3) обеспечить надежность ПО: контроль того, что вводимый символ - цифра (не буква, не символ), полученное число - не ноль;
    4) для графиков: подписать оси, не делать легенды; ожидаемые зависимости: с увеличением длины канала температура должна расти, вязкость - падать;
    5) вывести время расчётов.
     */


    // геометрические параметры
    private double width; // ширина канала
    private double length; // длина канала
    private double depth; // глубина канала

    // параметры свойств материала
    private double density; // плотность материала
    private double capacity; // удельная теплоемкость материала
    private double meltingTemperature; // температура плавления материала

    // режимные параметры
    private double coverSpeed; // скорость движения крышки
    private double coverTemperature; // температура крышки

    // эмпирические коэффициенты математической модели
    private double consFactorWithReduction; // коэффициент консистенции при температуре приведения
    private double viscosityFactor; // температурный коэффициент вязкости
    private double reductionTemperature; // температура приведения
    private double flowIndex; // индекс течения материала
    private double emissionFactor; // коэффициент теплоотдачи от крышки к материалу

    private double lengthStep; // шаг по длине канала
    private double currentLength; // текущая длина канала

    // промежуточные и конечные данные
    private double geomFactor; // коэффициент геометрической формы канала
    private double volumeRate; // объемный расход потока через канал
    private double deformationSpeed; // скорость деформации сдвига
    private double viscosityFlux; // удельный тепловой поток, генерируемый в материале за счет работы сил вязкого трения
    private double fluxFromCover; // удельный тепловой поток от нагретой крышки канала к материалу
    private double numberOfIterations; // количество шагов в цикле для расчетов температуры и вязкости материала
    private double transcalencyFactor; // коэффициент теплопроводности материала
    private double currentTemperature; // текущая температура материала
    private double consistenceFactor; // коэффициент консистенции материала
    private double currentViscosity; // текущая вязкость материала
    private double canalPerformance; // производительность канала


    public Data(double[] input) {
        this.lengthStep = input[0];
        this.width = input[1];
        this.length = input[2];
        this.depth = input[3];
        this.density = input[4];
        this.capacity = input[5];
        this.meltingTemperature = input[6];
        this.coverSpeed = input[7];
        this.coverTemperature = input[8];
        this.consFactorWithReduction = input[13];
        this.viscosityFactor = input[9];
        this.reductionTemperature = input[10];
        this.flowIndex = input[11];
        this.emissionFactor = input[12];
    }


    public void calcGeomFactor(double depth, double width) {
        this.geomFactor = 0.125 * Math.pow((depth / width), 2) - 0.625 * (depth / width) + 1;
    }

    public void calcVolumeRate(double width, double depth, double coverSpeed) {
        this.volumeRate = ((width * depth * coverSpeed) / 2) * geomFactor;
    }

    public void calcDeformationSpeed(double coverSpeed, double depth) {
        this.deformationSpeed = coverSpeed / depth;
    }

    public void calcViscosityFlux(double width, double depth, double consFactorWithReduction, double flowIndex) {
        this.viscosityFlux = width * depth * consFactorWithReduction * Math.pow(deformationSpeed, flowIndex + 1);
    }

    public void calcFluxFromCover(double width, double emissionFactor, double viscosityFactor, double coverTemperature,
                                  double reductionTemperature) {
        this.fluxFromCover = width * emissionFactor * ((1 / viscosityFactor) - coverTemperature + reductionTemperature);
    }

    public void calcNumberOfIterations(double length, double lengthStep) {
        this.numberOfIterations = Math.floor(length / lengthStep);
    }

    public void calcTranscalencyFactor(double viscosityFactor, double width, double emissionFactor, double density,
                                       double capacity, double meltingTemperature, double reductionTemperature,
                                       double currentLength) {
        this.transcalencyFactor = ((viscosityFactor * viscosityFlux + width * emissionFactor) / (viscosityFactor * fluxFromCover)) *
                                  (1 - Math.exp(- ((viscosityFactor * fluxFromCover) / (density * capacity * volumeRate)) * currentLength)) +
                                  Math.exp(viscosityFactor * (meltingTemperature - reductionTemperature - (fluxFromCover / (density * capacity * volumeRate)) * currentLength));
    }

    public void calcCurrentTemperature(double reductionTemperature, double viscosityFactor) {
        this.currentTemperature = reductionTemperature + (1 / viscosityFactor) * Math.log(transcalencyFactor);
    }

    public void calcConsistenceFactor(double consFactorWithReduction, double viscosityFactor, double reductionTemperature) {
        this.consistenceFactor = consFactorWithReduction * Math.exp(- viscosityFactor * (currentTemperature - reductionTemperature));
    }

    public void calcCurrentViscosity(double flowIndex) {
        this.currentViscosity = consistenceFactor * Math.pow(deformationSpeed, flowIndex - 1);
    }

    public void calcCanalPerformance(double density) {
        this.canalPerformance = 3600 * density * volumeRate;
    }

    public double getCurrentTemperature() {
        return this.currentTemperature;
    }

    public double getCurrentViscosity() {
        return this.currentViscosity;
    }

    public double getCanalPerformance() { return  this.canalPerformance; }

    public void calcIntermediate() {
        calcGeomFactor(depth, width);
        calcVolumeRate(width, depth, coverSpeed);
        calcDeformationSpeed(coverSpeed, depth);
        calcViscosityFlux(width, depth, consFactorWithReduction, flowIndex);
        calcFluxFromCover(width, emissionFactor, viscosityFactor, coverTemperature, reductionTemperature);
    }

    public void calcOutput(double currentLength) {
        calcNumberOfIterations(length, lengthStep);
        calcTranscalencyFactor(viscosityFactor, width, emissionFactor, density, capacity, meltingTemperature,
                               reductionTemperature, currentLength);
        calcCurrentTemperature(reductionTemperature, viscosityFactor);
        calcConsistenceFactor(consFactorWithReduction, viscosityFactor, reductionTemperature);
        calcCurrentViscosity(flowIndex);
        calcCanalPerformance(density);
    }

    public ArrayList getValues() {
        List<Result> results = new ArrayList<>();
        calcIntermediate();
        for (int i = 0; i <= numberOfIterations; i++) {
            calcOutput(currentLength);
            Result result = new Result(this.currentLength, getCurrentTemperature(), getCurrentViscosity());
            results.add(result);
            this.currentLength += lengthStep;
        }
        results.forEach(value -> System.out.println(value));
        return (ArrayList) results;
    }

}
