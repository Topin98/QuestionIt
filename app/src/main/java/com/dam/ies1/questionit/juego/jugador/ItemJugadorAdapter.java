package com.dam.ies1.questionit.juego.jugador;

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

import com.dam.ies1.questionit.R;
import com.dam.ies1.questionit.entidades.Perfil;

import java.util.ArrayList;

public class ItemJugadorAdapter extends ArrayAdapter<Perfil> {

    private Context context;
    private ArrayList<Perfil> datos;

    public ItemJugadorAdapter(Context context, ArrayList datos) {
        super(context, R.layout.lv_jugadores, datos);

        // Guardamos los parámetros en variables de clase.
        this.context = context;
        this.datos = datos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //inflamos la vista
        LayoutInflater inflater = LayoutInflater.from(context);
        View item = inflater.inflate(R.layout.lv_jugadores, null);

        ImageView imagenPerfil = item.findViewById(R.id.IVLJImagenPerfil);

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

        TextView nombre = item.findViewById(R.id.TVLJNombre);
        nombre.setText(datos.get(position).getNombre());

        TextView popularidadTotal = item.findViewById(R.id.TVLJPopularidadTotal);
        popularidadTotal.setText(String.valueOf(datos.get(position).getPopularidadTotal()));

        TextView victorias = item.findViewById(R.id.TVLJVictorias);
        victorias.setText(String.valueOf(datos.get(position).getVictorias()));

        TextView puntuacionTotal = item.findViewById(R.id.TVLJPuntuacionTotal);
        puntuacionTotal.setText(String.valueOf(datos.get(position).getPuntuacionTotal()));

        return item;
    }
}
