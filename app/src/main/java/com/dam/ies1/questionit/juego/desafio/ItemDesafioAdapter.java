package com.dam.ies1.questionit.juego.desafio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dam.ies1.questionit.R;
import com.dam.ies1.questionit.entidades.Desafio;

import java.util.ArrayList;

public class ItemDesafioAdapter extends ArrayAdapter<Desafio> {

    private Context context;
    private ArrayList<Desafio> datos;

    public ItemDesafioAdapter(Context context, ArrayList datos) {
        super(context, R.layout.lv_desafios, datos);

        // Guardamos los parámetros en variables de clase.
        this.context = context;
        this.datos = datos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //inflamos la vista
        LayoutInflater inflater = LayoutInflater.from(context);
        View item = inflater.inflate(R.layout.lv_desafios, null);

        TextView tag = item.findViewById(R.id.TVDTag);
        if (datos.get(position).getTag().equals("null")) tag.setText("Cualquiera");
        else tag.setText(datos.get(position).getTag());

        TextView dificultad = item.findViewById(R.id.TVDDificultad);
        dificultad.setText(datos.get(position).getDificultad());

        TextView fecha = item.findViewById(R.id.TVDFecha);
        fecha.setText(datos.get(position).getFecha());

        TextView resCorrectas = item.findViewById(R.id.TVDRespCorrectas);
        resCorrectas.setText(String.valueOf(datos.get(position).getRespCorrectas()));

        return item;
    }
}
