package com.fatless.fatless;


 class MetHelper {

    String activity;
    double metLevel;

   MetHelper(String activity, double metLevel) {
        this.activity = activity;
        this.metLevel = metLevel;
    }

    public MetHelper() {

    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public double getMetLevel() {
        return metLevel;
    }

    public void setMetLevel(double metLevel) {
        this.metLevel = metLevel;
    }
}
