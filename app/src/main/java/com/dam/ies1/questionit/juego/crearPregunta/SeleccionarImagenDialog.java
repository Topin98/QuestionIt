package com.dam.ies1.questionit.juego.crearPregunta;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

public class SeleccionarImagenDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final CharSequence[] items = {"Seleccionar de la galería"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Seleccionar una foto");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        Intent intentSeleccionarImagen = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                        intentSeleccionarImagen.setType("image/*");

                        //lanzamos la actividad para obtener un resultado en la actividad que invoca este dialogo
                        getActivity().startActivityForResult(intentSeleccionarImagen, Integer.parseInt(getTag()));
                        break;
                }
            }
        });
        AlertDialog alert = builder.create();
        return alert;
    }
}
