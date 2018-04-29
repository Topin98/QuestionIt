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

public class ComprobarNombre extends AsyncTask<String, Void, Boolean> {

    public interface IExisteNombre {
        void recibirNombre(boolean resultado, int codError);
    }

    Context context;
    IExisteNombre interfaz;
    ProgressBar progressBar;
    int codError = 0;

    public ComprobarNombre(Context context, IExisteNombre interfaz, ProgressBar progressBar) {
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
        int respuesta;
        BufferedReader reader = null;

        try {
            URL url = new URL(context.getString(R.string.server) + "/php/comprobarNombre.php");

            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setDoOutput(true);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write("NOMBRE=" + valores[0]);
            wr.flush();

            // Get the server response
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            respuesta = Integer.parseInt(reader.readLine());
        } catch (Exception ex) {
            respuesta = -2; //lo ponemos como numero, el mensaje de error se muestra en el padre (valdria cualquier numero)
            codError = -2;
        } finally {
            try {
                reader.close();
            } catch (Exception ex) {
            }
        }

        return respuesta > 0;
    }

    @Override
    protected void onPostExecute(Boolean resultado) {
        //NO SE VUELVE HABILITAR EL LAYOUT, SE HACE EN EL PADRE SI ES NECESARIO (SOLO SI EL NOMBRE YA EXISTE)
        progressBar.setVisibility(View.INVISIBLE);
        interfaz.recibirNombre(resultado, codError);
    }
}
