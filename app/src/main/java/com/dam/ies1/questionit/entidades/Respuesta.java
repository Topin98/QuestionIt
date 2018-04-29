package com.dam.ies1.questionit.entidades;

public class Respuesta {

    private String tituloRespuesta;
    private String imagenRespuesta;
    private boolean correcta;

    public String getTituloRespuesta() {
        return tituloRespuesta;
    }

    public void setTituloRespuesta(String tituloRespuesta) {
        this.tituloRespuesta = tituloRespuesta;
    }

    public String getImagenRespuesta() {
        return imagenRespuesta;
    }

    public void setImagenRespuesta(String imagenRespuesta) {
        this.imagenRespuesta = imagenRespuesta;
    }

    public boolean isCorrecta() {
        return correcta;
    }

    public void setCorrecta(boolean correcta) {
        this.correcta = correcta;
    }
}
