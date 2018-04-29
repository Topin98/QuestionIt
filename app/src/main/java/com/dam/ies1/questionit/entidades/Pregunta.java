package com.dam.ies1.questionit.entidades;

import java.util.ArrayList;

public class Pregunta {

    private int iDPregunta;
    private String iDUsuario;
    private String titulo;
    private String imagenTitulo;
    private int estado = -1;
    private ArrayList<Respuesta> alRespuestas = new ArrayList<>();
    private int respuestaCorrecta;
    private String dificultad;
    private int popularidad;
    private int aciertos;
    private int puntuacion;
    private ArrayList<String> alTags = new ArrayList<>();
    private String imagenPerfil;

    public Pregunta(){
    }

    public Pregunta(String iDUsuario){
        this.iDUsuario = iDUsuario;
    }

    public int getiDPregunta(){
        return this.iDPregunta;
    }

    public void setiDPregunta(int iDPregunta){
        this.iDPregunta = iDPregunta;
    }

    public String getiDUsuario() {
        return iDUsuario;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getImagenTitulo() {
        return imagenTitulo;
    }

    public void setImagenTitulo(String imagenTitulo) {
        this.imagenTitulo = imagenTitulo;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public ArrayList<Respuesta> getAlRespuestas() {
        return alRespuestas;
    }

    public int getRespuestaCorrecta() {
        return respuestaCorrecta;
    }

    public void setRespuestaCorrecta(int respuestaCorrecta) {
        this.respuestaCorrecta = respuestaCorrecta;
    }

    public String getDificultad() {
        return dificultad;
    }

    public void setDificultad(String dificultad) {
        this.dificultad = dificultad;
    }

    public int getPopularidad() {
        return popularidad;
    }

    public void setPopularidad(int popularidad) {
        this.popularidad = popularidad;
    }

    public void setAciertos(int aciertos) {
        this.aciertos = aciertos;
    }

    public int getAciertos() {
        return aciertos;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion() {

        int valor = 0; //inicializado por obligacion

        switch (this.dificultad){
            case "Fácil": valor = 1;
                break;
            case "Intermedia": valor = 2;
                break;
            case "Difícil": valor = 3;
                break;
        }

        try{
            this.puntuacion = (this.popularidad / this.aciertos) * valor;
        } catch (Exception e){
            this.puntuacion = (this.popularidad + 1) * valor; //el + 1 es por si nadie la jugo nunca
        }
    }

    public ArrayList<String> getAlTags() {
        return alTags;
    }

    public String getImagenPerfil() {
        return imagenPerfil;
    }

    public void setImagenPerfil(String imagenPerfil) {
        this.imagenPerfil = imagenPerfil;
    }
}
