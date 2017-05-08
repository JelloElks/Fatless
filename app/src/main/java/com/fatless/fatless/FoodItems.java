package com.fatless.fatless;


class FoodItems {

    private String name;
    private int number;


    FoodItems(String name) {
        this.name = name;
    }

    FoodItems() {

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

    int getNumber() {
        return number;
    }

    void setNumber(int number) {
        this.number = number;
    }


}
