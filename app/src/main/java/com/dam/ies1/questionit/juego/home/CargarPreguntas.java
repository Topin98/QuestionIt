package com.dam.ies1.questionit.juego.home;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dam.ies1.questionit.R;
import com.dam.ies1.questionit.main.ControlLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class CargarPreguntas extends AsyncTask<String, Void, String> {

    public interface ICargarPreguntas {
        void recibirPreguntas (String resultado, int codError);
    }

    Context context;
    ICargarPreguntas interfaz;
    int codError = 0;

    public CargarPreguntas(Context context, ICargarPreguntas interfaz) {
        this.context = context;
        this.interfaz = interfaz;
    }

    @Override
    protected void onPreExecute() {
        //no hace falta ni desactivar el layout ni mostrar la progress bar ya que se sale solo el circulo de carga
    }

    @Override
    protected String doInBackground(String... valores) {
        String respuesta;
        BufferedReader reader = null;

        try {
            URL url = new URL(context.getString(R.string.server) + "/php/cargarPreguntas.php");

            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setDoOutput(true);

            if (valores.length >= 3){
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write("TITULO=" + valores[0] + "&ORDEN=" + valores[1] + "&TAGS=" + valores[2]);
                wr.flush();
            }

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            respuesta = reader.readLine();
        } catch (Exception ex) {
            respuesta = "No se han podido cargar las preguntas. Inténtelo de nuevo más tarde.";
            codError = -5;
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
        interfaz.recibirPreguntas(resultado, codError);
    }
}
