package com.smartmux.batterymonitor.utills;

public class CompareFloat implements Comparable{

    private Float val;
    private String id;

    public CompareFloat(Float val, String id){
        this.val = val;
        this.id = id;
    }

    @Override
    public int compareTo(Object o) {

    	CompareFloat f = (CompareFloat)o;

        if (val.floatValue() > f.val.floatValue()) {
            return 1;
        }
        else if (val.floatValue() <  f.val.floatValue()) {
            return -1;
        }
        else {
            return 0;
        }

    }

    @Override
    public String toString(){
        return this.id;
    }
}
