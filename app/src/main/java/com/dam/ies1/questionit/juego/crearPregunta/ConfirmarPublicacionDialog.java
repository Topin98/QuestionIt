package com.dam.ies1.questionit.juego.crearPregunta;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

public class ConfirmarPublicacionDialog extends DialogFragment{

    public interface IConfirmarPublicacion {
        void publicarPregunta();
    }

    IConfirmarPublicacion interfaz;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //alTags = getArguments().getStringArrayList("TAGSSELEC");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Confirmación");
        builder.setMessage("Estás seguro que quieres publicar la pregunta?");

        builder.setPositiveButton("Publicar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                interfaz.publicarPregunta();
            }
        });


        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            interfaz = (IConfirmarPublicacion) getActivity();
        } catch (ClassCastException e) {
            //si la activity no implementa la interfaz...
            Toast.makeText(context, "Recuerda implementar la interfaz", Toast.LENGTH_SHORT).show();
        }
    }
}
