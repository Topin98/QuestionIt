package com.dam.ies1.questionit.juego.crearPregunta;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.dam.ies1.questionit.R;

import java.util.ArrayList;

public class FiltroTagsDialog extends DialogFragment{

    public interface TagsSeleccionados {
        void recibirTags(ArrayList<String> tags);
    }

    TagsSeleccionados interfaz;
    String[] aTags; //array con todos los tags permitidos
    boolean[] iTagsSelec; //boolean que indica si la posicion del tag se selecciono
    ArrayList<String> alTags;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        aTags = getResources().getStringArray(R.array.tags);
        iTagsSelec = new boolean[aTags.length];
        alTags = getArguments().getStringArrayList("TAGSSELEC");

        for (int i = 0; i < iTagsSelec.length; i++){
            if (alTags.contains(aTags[i])){
                iTagsSelec[i] = true;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMultiChoiceItems(aTags, iTagsSelec, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                if(isChecked){
                    iTagsSelec[position] = true;
                } else {
                    iTagsSelec[position] = false;
                }
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

                //limpiamos los tags que habia marcado antes
                alTags.clear();

                //recorremos las posiciones de los tags
                for (int i = 0; i < iTagsSelec.length; i++) {

                    //si la posicion esta marcada
                    if (iTagsSelec[i]) {

                        //añadimos el valor del tag al ArrayList
                        alTags.add(aTags[i]);
                    }
                }

                //los mandamos a la activity
                interfaz.recibirTags(alTags);
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.setNeutralButton("Limpiar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                //limpiamos los tags marcados
                alTags.clear();

                //mandamos al activity el ArrayList vacio
                interfaz.recibirTags(alTags);
            }
        });


        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            interfaz = (TagsSeleccionados) getActivity();
        } catch (ClassCastException e) {
            //si la activity no implementa la interfaz...
            //Toast.makeText(context, "Recuerda implementar la interfaz", Toast.LENGTH_SHORT).show();
        }
    }
}