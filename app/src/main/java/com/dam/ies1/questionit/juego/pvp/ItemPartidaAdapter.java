package com.dam.ies1.questionit.juego.pvp;

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
import com.dam.ies1.questionit.entidades.Partida;

import java.util.ArrayList;

public class ItemPartidaAdapter extends ArrayAdapter<Partida> {

    private Context context;
    private ArrayList<Partida> datos;

    public ItemPartidaAdapter(Context context, ArrayList datos) {
        super(context, R.layout.lv_partidas, datos);

        // Guardamos los parámetros en variables de clase.
        this.context = context;
        this.datos = datos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //inflamos la vista
        LayoutInflater inflater = LayoutInflater.from(context);
        View item = inflater.inflate(R.layout.lv_partidas, null);

        //region ImagenPerfil1 (izquierda)
        ImageView imagenPerfil1 = item.findViewById(R.id.IVPPerfil1);

        //obtenemos la ruta de la imagen
        String imagenB64 = datos.get(position).getImagen1();

        //si la ruta de la imagen no es null
        if (!imagenB64.equals("null")){

            byte[] decodedString = Base64.decode(imagenB64, Base64.URL_SAFE);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            //cambiamos el tamaño de la imagen para ajustarla al dialogo
            Bitmap resized = Bitmap.createScaledBitmap(decodedByte, 400, 400, true);

            //asociamos la imagen al ImageView
            imagenPerfil1.setImageBitmap(resized);
        } else {
            imagenPerfil1.setImageDrawable(context.getResources().getDrawable(R.drawable.fotodefecto));
        }
        //endregion

        //region ImagenPerfil2 (derecha)
        ImageView imagenPerfil2 = item.findViewById(R.id.IVPPerfil2);

        //obtenemos la ruta de la imagen
        imagenB64 = datos.get(position).getImagen2();

        //si la ruta de la imagen no es null
        if (!imagenB64.equals("null")){

            byte[] decodedString = Base64.decode(imagenB64, Base64.URL_SAFE);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            //cambiamos el tamaño de la imagen para ajustarla al dialogo
            Bitmap resized = Bitmap.createScaledBitmap(decodedByte, 400, 400, true);

            //asociamos la imagen al ImageView
            imagenPerfil2.setImageBitmap(resized);
        } else {
            imagenPerfil2.setImageDrawable(context.getResources().getDrawable(R.drawable.fotodefecto));
        }
        //endregion

        TextView nombre1 = item.findViewById(R.id.TVPNombre1);
        nombre1.setText(datos.get(position).getNombre1());

        TextView nombre2 = item.findViewById(R.id.TVPNombre2);
        nombre2.setText(datos.get(position).getNombre2());

        TextView puntos1 = item.findViewById(R.id.TVPPuntos1);
        puntos1.setText(String.valueOf(datos.get(position).getPuntos1()));

        TextView puntos2 = item.findViewById(R.id.TVPPuntos2);
        if (datos.get(position).getPuntos2() == -1) puntos2.setText("X");
        else puntos2.setText(String.valueOf(datos.get(position).getPuntos2()));

        return item;
    }
}
