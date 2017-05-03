package com.fatless.fatless;


class FoodItems {

    private String name;
    private int number;
    private int protein;
    private int fat;
    private int energyKcal;


    public FoodItems(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return name;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getProtein() {
        return protein;
    }

    public void setProtein(int protein) {
        this.protein = protein;
    }

    public int getFat() {
        return fat;
    }

    public void setFat(int fat) {
        this.fat = fat;
    }

    public int getEnergyKcal() {
        return energyKcal;
    }

    public void setEnergyKcal(int energyKcal) {
        this.energyKcal = energyKcal;
    }
}
