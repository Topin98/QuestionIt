package com.dam.ies1.questionit.entidades;

public class Partida {

    private int iDPartida;
    private String iDUsuario1;
    private String iDUsuario2;
    private int puntos1;
    private int puntos2;
    private String nombre1;
    private String nombre2;
    private String imagen1;
    private String imagen2;
    private int jugando;

    public int getiDPartida() {
        return iDPartida;
    }

    public void setiDPartida(int iDPartida) {
        this.iDPartida = iDPartida;
    }

    public String getiDUsuario1() {
        return iDUsuario1;
    }

    public void setiDUsuario1(String iDUsuario1) {
        this.iDUsuario1 = iDUsuario1;
    }

    public String getiDUsuario2() {
        return iDUsuario2;
    }

    public void setiDUsuario2(String iDUsuario2) {
        this.iDUsuario2 = iDUsuario2;
    }

    public int getPuntos1() {
        return puntos1;
    }

    public void setPuntos1(int puntos1) {
        this.puntos1 = puntos1;
    }

    public int getPuntos2() {
        return puntos2;
    }

    public void setPuntos2(int puntos2) {
        this.puntos2 = puntos2;
    }

    public String getNombre1() {
        return nombre1;
    }

    public void setNombre1(String nombre1) {
        this.nombre1 = nombre1;
    }

    public String getNombre2() {
        return nombre2;
    }

    public void setNombre2(String nombre2) {
        this.nombre2 = nombre2;
    }

    public String getImagen1() {
        return imagen1;
    }

    public void setImagen1(String imagen1) {
        this.imagen1 = imagen1;
    }

    public String getImagen2() {
        return imagen2;
    }

    public void setImagen2(String imagen2) {
        this.imagen2 = imagen2;
    }

    public int getJugando() {
        return jugando;
    }

    public void setJugando(int jugando) {
        this.jugando = jugando;
    }
}
