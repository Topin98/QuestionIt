package com.dam.ies1.questionit.perfil;

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

public class CargarPerfil extends AsyncTask<String, Void, String> {

    public interface ICargarPerfil {
        void recibirPerfil(String resultado, int codError);
    }

    Context context;
    ICargarPerfil interfaz;
    ProgressBar progressBar;
    int codError = 0;

    public CargarPerfil(Context context, ICargarPerfil interfaz, ProgressBar progressBar) {
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
    protected String doInBackground(String... valores) {
        String respuesta;
        BufferedReader reader = null;

        try {
            URL url = new URL(context.getString(R.string.server) + "/php/cargarPerfil.php");

            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setDoOutput(true);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write("ID_USU=" + valores[0]);
            wr.flush();

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            respuesta = reader.readLine();
        } catch (Exception ex) {
            respuesta = "No se ha podido cargar el perfil. Inténtelo de nuevo más tarde.";
            codError = -17;
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
        progressBar.setVisibility(View.INVISIBLE);
        interfaz.recibirPerfil(resultado, codError);
    }
}
