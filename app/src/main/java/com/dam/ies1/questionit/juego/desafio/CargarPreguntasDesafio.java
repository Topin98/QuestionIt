package com.dam.ies1.questionit.juego.desafio;

import android.content.Context;
import android.os.AsyncTask;

import com.dam.ies1.questionit.R;
import com.dam.ies1.questionit.main.ControlLayout;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class CargarPreguntasDesafio extends AsyncTask<String, Void, String> {

    public interface ICargarPreguntasDesafio {
        void recibirPreguntasDesafio (String resultado, int codError);
    }

    Context context;
    ICargarPreguntasDesafio interfaz;
    int codError = 0;

    public CargarPreguntasDesafio(Context context, ICargarPreguntasDesafio interfaz) {
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
            URL url = new URL(context.getString(R.string.server) + "/php/cargarPreguntasDesafios.php");

            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setDoOutput(true);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            String parametros = "DIFICULTAD=" + valores[0];
            if (!valores[1].equals("Cualquiera"))parametros += "&TAG=" + valores[1];

            wr.write(parametros);
            wr.flush();

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            respuesta = reader.readLine();

        } catch (Exception ex) {
            respuesta = "No se han podido cargar las preguntas. Inténtelo de nuevo más tarde.";
            codError = -10;
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
        interfaz.recibirPreguntasDesafio(resultado, codError);
    }
}
