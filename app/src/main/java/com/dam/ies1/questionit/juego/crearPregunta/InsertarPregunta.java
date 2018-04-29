package com.dam.ies1.questionit.juego.crearPregunta;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.dam.ies1.questionit.entidades.Pregunta;
import com.dam.ies1.questionit.R;
import com.dam.ies1.questionit.main.ControlLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class InsertarPregunta extends AsyncTask<Pregunta, Void, Boolean> {

    public interface IInsercionPreguntaCorrecta {
        void recibirResInsercion(boolean resultado, int codError);
    }

    Context context;
    IInsercionPreguntaCorrecta interfaz;
    ProgressBar progressBar;
    int codError = 0;

    public InsertarPregunta(Context context, IInsercionPreguntaCorrecta interfaz, ProgressBar progressBar) {
        this.context = context;
        this.interfaz = interfaz;
        this.progressBar = progressBar;
    }

    @Override
    protected void onPreExecute() {
        ControlLayout.deshabilitarLyt(context);
        this.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected Boolean doInBackground(Pregunta... preguntas) {
        boolean respuesta;
        BufferedReader reader = null;

        String parametros = "ID_USU=" + preguntas[0].getiDUsuario() + "&TITULO=" + preguntas[0].getTitulo() + "&IMAGEN_TITULO=" + preguntas[0].getImagenTitulo() +
                            "&RESPUESTA1=" + preguntas[0].getAlRespuestas().get(0).getTituloRespuesta() + "&IMAGEN1=" + preguntas[0].getAlRespuestas().get(0).getImagenRespuesta() +
                            "&RESPUESTA2=" + preguntas[0].getAlRespuestas().get(1).getTituloRespuesta() + "&IMAGEN2=" + preguntas[0].getAlRespuestas().get(1).getImagenRespuesta();
        try {
            parametros += "&RESPUESTA3=" + preguntas[0].getAlRespuestas().get(2).getTituloRespuesta() + "&IMAGEN3=" + preguntas[0].getAlRespuestas().get(2).getImagenRespuesta();
        } catch (Exception e){
        }

        try{
            parametros += "&RESPUESTA4=" + preguntas[0].getAlRespuestas().get(3).getTituloRespuesta() + "&IMAGEN4=" + preguntas[0].getAlRespuestas().get(3).getImagenRespuesta();
        } catch (Exception e){
        }

        parametros += "&RESPUESTA_CORRECTA=" + preguntas[0].getRespuestaCorrecta() + "&DIFICULTAD=" + preguntas[0].getDificultad();

        JSONObject tags = new JSONObject();
        for (int i = 0; i < preguntas[0].getAlTags().size(); i++){
            try {
                tags.put("TAG" + i, preguntas[0].getAlTags().get(i));
            } catch (JSONException e) {
            }
        }

        parametros += "&TAGS=" + tags.toString();

        try {

            URL url = new URL(context.getString(R.string.server) + "/php/insertarPregunta.php");

            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setDoOutput(true);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(parametros);
            wr.flush();

            // Get the server response
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            respuesta = Boolean.parseBoolean(reader.readLine());

        } catch (Exception ex) {
            respuesta = false; //lo ponemos como boolean, el mensaje de error se muestra en el padre (valdria tambien true)
            codError = -4;
        } finally {
            try {
                reader.close();
            } catch (Exception ex) {
            }
        }

        return respuesta;
    }

    @Override
    protected void onPostExecute(Boolean resultado) {
        ControlLayout.habilitarLyt(context);
        progressBar.setVisibility(View.INVISIBLE);
        interfaz.recibirResInsercion(resultado, codError);
    }
}