package com.example.primeapp.models;

import java.util.Locale;

public class PressureData {


    int _left;
    int _right;
    public PressureData(int left, int right){
        _left=left;
        _right=right;
    }

    public float left() {
        return _left;
    }

    public float right() {
        return _right;
    }



    public String toString() {
        return String.format( "{l: %d, r: %d}",this._left,this._right);
    }

}
