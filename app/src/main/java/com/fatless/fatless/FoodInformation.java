package com.fatless.fatless;


class FoodInformation {

    private String name;
    private int number;
    private int protein;
    private int fat;
    private int energyKcal;
    private int sodium;


    FoodInformation(int number, int protein, int fat, int energyKcal, int sodium) {
        this.number = number;
        this.protein = protein;
        this.fat = fat;
        this.energyKcal = energyKcal;
        this.sodium = sodium;
    }

    @Override
    public String toString() {
        return "Food Information " +
                " Protein : " + protein +
                " Fat : " + fat +
                " EnergyKcal : " + energyKcal +
                " Sodium : " + sodium;
    }


    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    int getNumber() {
        return number;
    }

    void setNumber(int number) {
        this.number = number;
    }

    int getProtein() {
        return protein;
    }

    void setProtein(int protein) {
        this.protein = protein;
    }

    int getFat() {
        return fat;
    }

    void setFat(int fat) {
        this.fat = fat;
    }

    int getEnergyKcal() {
        return energyKcal;
    }

    void setEnergyKcal(int energyKcal) {
        this.energyKcal = energyKcal;
    }

    int getSodium() {
        return sodium;
    }

    void setSodium(int sodium) {
        this.sodium = sodium;
    }
}
