package com.dam.ies1.questionit.preguntas;

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

public class ActualizarEstadoPregunta extends AsyncTask<String, Void, Boolean> {

    public interface IActualizarEstPreg {
        void recibirActEstado(boolean resultado, int codError);
    }

    Context context;
    IActualizarEstPreg interfaz;
    ProgressBar progressBar;
    int codError = 0;

    public ActualizarEstadoPregunta(Context context, IActualizarEstPreg interfaz, ProgressBar progressBar) {
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
    protected Boolean doInBackground(String... valores) {
        boolean respuesta;
        BufferedReader reader = null;

        String parametros = "ID_USU=" + valores[0] + "&ID_PREG=" + valores[1] + "&PUNTUACION=" + valores[2];

        try {

            URL url = new URL(context.getString(R.string.server) + "/php/actualizarEstadoPregunta.php");

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
            codError = -8;
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
        interfaz.recibirActEstado(resultado, codError);
    }
}