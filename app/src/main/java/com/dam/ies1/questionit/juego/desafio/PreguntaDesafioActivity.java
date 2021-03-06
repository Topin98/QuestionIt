package com.dam.ies1.questionit.juego.desafio;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.dam.ies1.questionit.R;
import com.dam.ies1.questionit.entidades.Pregunta;
import com.dam.ies1.questionit.preguntas.InterpolationButtonRespuesta;

public class PreguntaDesafioActivity extends AppCompatActivity {

    static Context context;
    static TextView TVResultado;
    static int posicionCorrecta;
    TextView TVTitulo;
    DesafioButton btnRespuesta1;
    DesafioButton btnRespuesta2;
    DesafioButton btnRespuesta3;
    DesafioButton btnRespuesta4;
    static Handler handler = new Handler();
    static DesafioButton[] desafioButtons;
    static Pregunta pregunta = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregunta_desafio);

        context = this;

        TVResultado = findViewById(R.id.TVDResultado);
        TVTitulo = findViewById(R.id.TVDTitulo);
        btnRespuesta1 = findViewById(R.id.btnDRespuesta1);
        btnRespuesta2 = findViewById(R.id.btnDRespuesta2);
        btnRespuesta3 = findViewById(R.id.btnDRespuesta3);
        btnRespuesta4 = findViewById(R.id.btnDRespuesta4);

        //cogemos la primera pregunta
        pregunta = DesafioActivity.alPreguntas.get(0);

        //actualizamos el layout con los datos de la pregunat
        actualizarlyt();
    }

    public void actualizarlyt(){

        TVTitulo.setText(pregunta.getTitulo());
        if (!pregunta.getImagenTitulo().equals("null")){
            TVTitulo.setText(TVTitulo.getText() + "  \nVer más...");

            TVTitulo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View TVTit) {

                    //creamos el dialogo
                    AlertDialog.Builder builder = new AlertDialog.Builder(PreguntaDesafioActivity.this);

                    //inflamos el layout
                    LayoutInflater layoutInflater = getLayoutInflater();
                    View view = layoutInflater.inflate(R.layout.dialogoimagen, null);

                    //ImageView donde vamos a poner la imagen
                    ImageView imagen = view.findViewById(R.id.IVDialogoImagen);

                    byte[] decodedString = Base64.decode(pregunta.getImagenTitulo(), Base64.URL_SAFE);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    Bitmap resized = Bitmap.createScaledBitmap(decodedByte, 1500, 1500, true);

                    imagen.setImageBitmap(resized);

                    builder.setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

                    //asociamos el layout con el dialogo
                    builder.setView(view);

                    builder.show();
                }
            });
        }

        //Array de botones de respuesta, incluimos los 4
        desafioButtons = new DesafioButton[] {btnRespuesta1, btnRespuesta2, btnRespuesta3, btnRespuesta4};

        boolean mostrar;
        //recorremos el ArrayList con las respuestas
        for (int i = 0; i < 4; i++){

            mostrar = false;

            //si la respuesta tiene titulo o imagen
            if (!pregunta.getAlRespuestas().get(i).getTituloRespuesta().equals("null")) {

                desafioButtons[i].setText(pregunta.getAlRespuestas().get(i).getTituloRespuesta());
                mostrar = true;
            }

            if (!pregunta.getAlRespuestas().get(i).getImagenRespuesta().equals("null")) {
                desafioButtons[i].setImagen(pregunta.getAlRespuestas().get(i).getImagenRespuesta());

                desafioButtons[i].setText(desafioButtons[i].getText() + " \nVer más...");
                mostrar = true;
            }

            if (mostrar) {

                if (pregunta.getRespuestaCorrecta() == i + 1) {
                    desafioButtons[i].setCorrecta(true);
                    posicionCorrecta = i;
                }

                mostrarRespuesta(desafioButtons[i], Integer.parseInt(desafioButtons[i].getTag().toString()));
            }
        }
    }

    public void mostrarRespuesta(final DesafioButton btn, int tiempo){

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                btn.setVisibility(View.VISIBLE);
            }
        }, tiempo);
    }

    public static void mostrarRespuesta(boolean estado){

        for (DesafioButton desafioButton : desafioButtons){
            desafioButton.setEnabled(false);
        }

        Intent intent = new Intent();

        if (estado) {
            //mostramos mensaje de acierto
            TVResultado.setText("CORRECTO");
            TVResultado.setTextColor(context.getResources().getColor(R.color.verde));

            //indicamos que se acerto la pregunta
            ((Activity)context).setResult(RESULT_OK, intent);

        } else {
            //mostramos mensaje de acierto
            TVResultado.setText("INCORRECTO");
            TVResultado.setTextColor(context.getResources().getColor(R.color.rojo));

            //animamos la respuesta correcta
            desafioButtons[posicionCorrecta].animar();
        }

        TVResultado.setVisibility(View.VISIBLE);

        Animation animation = AnimationUtils.loadAnimation(context, R.anim.bote);

        InterpolationButtonRespuesta interpolator = new InterpolationButtonRespuesta(0.2, 20);
        animation.setInterpolator(interpolator);

        TVResultado.startAnimation(animation);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                RotateAnimation rotate = new RotateAnimation(0, 1080, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotate.setDuration(1000);
                TVResultado.startAnimation(rotate);
            }
        }, 1000);


        //esperamos un rato para que se anime la respuesta correcta y volvemos a la activity llamante
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((Activity)context).finish();
            }
        }, 2000);
    }

    @Override
    public void onBackPressed() {
        //no hacemos nada para no dejar ir hacia atras al usuario
    }
}
