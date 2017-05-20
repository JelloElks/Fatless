package com.fatless.fatless;


class MetHelper {

    private String activity;
    private double metLevel;

    MetHelper(String activity, double metLevel) {
        this.activity = activity;
        this.metLevel = metLevel;
    }

    MetHelper() {

    }

    String getActivity() {
        return activity;
    }

    void setActivity(String activity) {
        this.activity = activity;
    }

    double getMetLevel() {
        return metLevel;
    }

    void setMetLevel(double metLevel) {
        this.metLevel = metLevel;
    }
}
