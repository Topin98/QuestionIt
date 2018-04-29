package com.dam.ies1.questionit.juego.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dam.ies1.questionit.entidades.Pregunta;
import com.dam.ies1.questionit.R;

import java.util.ArrayList;

public class ItemPreguntaAdapter extends ArrayAdapter<Pregunta>{

    private Context context;
    private ArrayList<Pregunta> datos;

    public ItemPreguntaAdapter(Context context, ArrayList datos) {
        super(context, R.layout.lv_preguntas, datos);

        // Guardamos los parámetros en variables de clase.
        this.context = context;
        this.datos = datos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //inflamos la vista
        LayoutInflater inflater = LayoutInflater.from(context);
        View item = inflater.inflate(R.layout.lv_preguntas, null);

        ImageView imagenPerfil = item.findViewById(R.id.IVLPImagen);

        //obtenemos la ruta de la imagen
        String imagenB64 = datos.get(position).getImagenPerfil();

        //si la ruta de la imagen no es null
        if (!imagenB64.equals("null")){

            byte[] decodedString = Base64.decode(imagenB64, Base64.URL_SAFE);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            //cambiamos el tamaño de la imagen para ajustarla al dialogo
            Bitmap resized = Bitmap.createScaledBitmap(decodedByte, 400, 400, true);

            //asociamos la imagen al ImageView
            imagenPerfil.setImageBitmap(resized);
        } else {
            imagenPerfil.setImageDrawable(context.getResources().getDrawable(R.drawable.fotodefecto));
        }

        TextView titulo = item.findViewById(R.id.TVLPTitulo);
        titulo.setText(datos.get(position).getTitulo());

        TextView dificultad = item.findViewById(R.id.TVLPDificultad);
        dificultad.setText(datos.get(position).getDificultad());

        TextView popularidad = item.findViewById(R.id.TVLPPopularidad);
        popularidad.setText(String.valueOf(datos.get(position).getPopularidad()));

        TextView puntuacion = item.findViewById(R.id.TVLPPuntuacion);
        puntuacion.setText(String.valueOf(datos.get(position).getPuntuacion()));

        ImageView estado = item.findViewById(R.id.IVLPEstado);
        switch (datos.get(position).getEstado()){
            case 0: estado.setImageResource(R.drawable.fallo);
                break;
            case 1: estado.setImageResource(R.drawable.acierto);
                break;
        }

        return item;
    }
}
