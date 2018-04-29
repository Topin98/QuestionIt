package com.dam.ies1.questionit.juego.pvp;

import android.content.Context;
import android.os.AsyncTask;

import com.dam.ies1.questionit.R;
import com.dam.ies1.questionit.main.ControlLayout;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class IniciarPartida extends AsyncTask<String, Void, Boolean> {

    public interface IIniciarPartidaCorrecta {
        void recibirResInicializacion(boolean resultado, int codError);
    }

    Context context;
    IIniciarPartidaCorrecta interfaz;
    int codError = 0;

    public IniciarPartida(Context context, IIniciarPartidaCorrecta interfaz) {
        this.context = context;
        this.interfaz = interfaz;
    }

    @Override
    protected void onPreExecute() {
        ControlLayout.deshabilitarLyt(context);
        //la progress bar no la mostramos porque no hay, esta el TextView parpadeando
    }

    @Override
    protected Boolean doInBackground(String... valores) {
        boolean respuesta;
        BufferedReader reader = null;

        try {

            URL url = new URL(context.getString(R.string.server) + "/php/iniciarPartida.php");

            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setDoOutput(true);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write("ID_PARTIDA=" + valores[0] + "&ID_USU_CREADOR=" + valores[1]);
            wr.flush();

            // Get the server response
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            respuesta = Boolean.parseBoolean(reader.readLine());

        } catch (Exception ex) {
            respuesta = false; //lo ponemos como boolean, el mensaje de error se muestra en el padre (valdria tambien true)
            codError = -16;
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
        interfaz.recibirResInicializacion(resultado, codError);
    }
}