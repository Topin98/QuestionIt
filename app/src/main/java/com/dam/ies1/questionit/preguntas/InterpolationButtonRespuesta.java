package com.dam.ies1.questionit.preguntas;

import android.view.animation.Interpolator;

public class InterpolationButtonRespuesta implements Interpolator {

    private double amplitud = 1;
    private double frecuencia = 10;

    public InterpolationButtonRespuesta(double amplitude, double frequency) {
        amplitud = amplitude;
        frecuencia = frequency;
    }

    public float getInterpolation(float time) {
        return (float) (-1 * Math.pow(Math.E, -time / amplitud) * Math.cos(frecuencia * time) + 1);
    }
}
