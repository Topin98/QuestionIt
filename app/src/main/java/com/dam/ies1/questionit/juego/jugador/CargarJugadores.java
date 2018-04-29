package com.dam.ies1.questionit.juego.jugador;

import android.content.Context;
import android.os.AsyncTask;

import com.dam.ies1.questionit.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class CargarJugadores extends AsyncTask<String, Void, String> {

    public interface ICargarJugadores {
        void recibirJugadores (String resultado, int codError);
    }

    Context context;
    ICargarJugadores interfaz;
    int codError = 0;

    public CargarJugadores(Context context, ICargarJugadores interfaz) {
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
            URL url = new URL(context.getString(R.string.server) + "/php/cargarJugadores.php");

            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setDoOutput(true);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write("NOMBRE=" + valores[0]);
            wr.flush();

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            respuesta = reader.readLine();
        } catch (Exception ex) {
            respuesta = "No se han podido cargar los jugadores. Inténtelo de nuevo más tarde.";
            codError = -19;
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
        interfaz.recibirJugadores(resultado, codError);
    }
}
