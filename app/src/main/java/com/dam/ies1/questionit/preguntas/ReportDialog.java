package com.dam.ies1.questionit.preguntas;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.dam.ies1.questionit.R;

public class ReportDialog extends DialogFragment{

    public interface IConfirmarReport {
        void recibirConfirmacion(String causa);
    }

    IConfirmarReport listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //creamos el dialogo
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //inflamos el layout
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();

        View view = layoutInflater.inflate(R.layout.dialogoreport, null);

        EditText ETCausa = view.findViewById(R.id.ETDialogoReport);

        builder.setView(view);

        //le ponemos un boton de aceptar al dialogo
        builder.setPositiveButton("Enviar",new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id) {
                listener.recibirConfirmacion(ETCausa.getText().toString());
            }
        });

        //le ponemos un boton de cancelar al dialogo
        builder.setNegativeButton("Cancelar",new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (IConfirmarReport) getActivity();
        } catch (ClassCastException e) {
            //si la activity no implementa la interfaz...
            throw new ClassCastException(getActivity().toString() + "debes implementar DialogListener");
        }
    }
}
