package ru.ckateptb.abilityslots.energy;

public interface EnergyHolder {
    double getEnergy();

    void setEnergy(double value);

    double getMaxEnergy();

    void updateEnergyBar();

    void enableEnergyBar();

    void disableEnergyBar();

    boolean isEnergyBarEnabled();
}
