package com.dam.ies1.questionit.juego.pvp;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.dam.ies1.questionit.R;
import com.dam.ies1.questionit.main.ControlLayout;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class InsertarPartida extends AsyncTask<String, Void, String> {

    public interface IInsercionPartidaCorrecta {
        void recibirResInsercion(String resultado, int codError);
    }

    Context context;
    IInsercionPartidaCorrecta interfaz;
    int codError = 0;

    public InsertarPartida(Context context, IInsercionPartidaCorrecta interfaz) {
        this.context = context;
        this.interfaz = interfaz;
    }

    @Override
    protected void onPreExecute() {
        ControlLayout.deshabilitarLyt(context);
        //la progress bar no la mostramos porque no hay, esta el TextView parpadeando
    }

    @Override
    protected String doInBackground(String... valores) {
        String respuesta;
        BufferedReader reader = null;

        try {

            URL url = new URL(context.getString(R.string.server) + "/php/insertarPartida.php");

            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setDoOutput(true);

            String parametros = "ID_USU1=" + valores[0];
            try{
                parametros += "&ID_USU2=" + valores[1];
            } catch (Exception e){
                //si se produce excepcion es que se va a buscar un rival random en el script php
            }

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(parametros);
            wr.flush();

            // Get the server response
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            respuesta = reader.readLine();

        } catch (Exception ex) {
            respuesta = "No se ha podido empezar la partida. Inténtelo de nuevo más tarde.";
            codError = -13;
        } finally {
            try {
                reader.close();
            } catch (Exception ex) {
            }
        }

        return respuesta;
    }

    @Override
    protected void onPostExecute(String resultado) {
        ControlLayout.habilitarLyt(context);
        interfaz.recibirResInsercion(resultado, codError);
    }
}