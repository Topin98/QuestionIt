package com.dam.ies1.questionit.entidades;

public class Perfil {

    private String iDUsuario;
    private String nombre;
    private int puntuacionTotal;
    private int popularidadTotal;
    private int victorias;
    private String imagenPerfil;
    private int numPregContestadas;
    private int numPregAcertadas;
    private int record;
    private int numPartidasJugadas;

    public Perfil(String iDUsuario){
        this.iDUsuario = iDUsuario;
    }

    public String getiDUsuario() {
        return iDUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPuntuacionTotal() {
        return puntuacionTotal;
    }

    public void setPuntuacionTotal(int puntuacionTotal) {
        this.puntuacionTotal = puntuacionTotal;
    }

    public int getPopularidadTotal() {
        return popularidadTotal;
    }

    public void setPopularidadTotal(int popularidadTotal) {
        this.popularidadTotal = popularidadTotal;
    }

    public int getVictorias() {
        return victorias;
    }

    public void setVictorias(int victorias) {
        this.victorias = victorias;
    }

    public String getImagenPerfil() {
        return imagenPerfil;
    }

    public void setImagenPerfil(String imagenPerfil) {
        this.imagenPerfil = imagenPerfil;
    }

    public int getNumPregContestadas() {
        return numPregContestadas;
    }

    public void setNumPregContestadas(int numPregContestadas) {
        this.numPregContestadas = numPregContestadas;
    }

    public int getNumPregAcertadas() {
        return numPregAcertadas;
    }

    public void setNumPregAcertadas(int numPregAcertadas) {
        this.numPregAcertadas = numPregAcertadas;
    }

    public int getRecord() {
        return record;
    }

    public void setRecord(int record) {
        this.record = record;
    }

    public int getNumPartidasJugadas() {
        return numPartidasJugadas;
    }

    public void setNumPartidasJugadas(int numPartidasJugadas) {
        this.numPartidasJugadas = numPartidasJugadas;
    }

}
