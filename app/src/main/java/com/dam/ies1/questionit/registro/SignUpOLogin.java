package com.dam.ies1.questionit.registro;

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

public class SignUpOLogin extends AsyncTask<String, Void, String> {

    public interface IExisteIDUsuario {
        void recibirIDUsuario(String resultado, int codError);
    }

    Context context;
    IExisteIDUsuario interfaz;
    ProgressBar progressBar;
    int codError = 0;

    public SignUpOLogin(Context context, IExisteIDUsuario interfaz, ProgressBar progressBar) {
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
            URL url = new URL(context.getString(R.string.server) + "/php/comprobarUsuario.php");

            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setDoOutput(true);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write("ID_USU=" + valores[0]);
            wr.flush();

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            respuesta = reader.readLine();
        } catch (Exception ex) {
            respuesta = "No se ha podido iniciar sesión. Inténtelo de nuevo más tarde.";
            codError = -1;
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
        interfaz.recibirIDUsuario(resultado, codError);
    }
}
