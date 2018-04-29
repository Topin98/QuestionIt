package com.dam.ies1.questionit.juego.desafio;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.dam.ies1.questionit.R;
import com.dam.ies1.questionit.preguntas.InterpolationButtonRespuesta;

public class DesafioButton extends AppCompatTextView implements View.OnClickListener {

    private boolean correcta = false;
    private String imagen = null;

    public void setCorrecta(boolean correcta) {
        this.correcta = correcta;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public DesafioButton(Context context) {
        super(context);
        setOnClickListener(this);
    }

    public DesafioButton(final Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(this);
    }

    public DesafioButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnClickListener(this);
    }

    @Override
    public void setVisibility(int visibility){

        if (visibility == VISIBLE) {

            final Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.bote);

            InterpolationButtonRespuesta interpolator = new InterpolationButtonRespuesta(0.2, 20);
            animation.setInterpolator(interpolator);

            this.startAnimation(animation);
        }

        super.setVisibility(visibility);
    }

    @Override
    public void onClick(final View view) {

        if (imagen != null){
            this.mostrarImagen();
        } else {
            comprobarRespuesta();
        }
    }

    public void mostrarImagen(){

        //creamos el dialogo
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        //inflamos el layout
        LayoutInflater layoutInflater = ((Activity)getContext()).getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialogoimagen, null);

        //ImageView donde vamos a poner la imagen
        ImageView imagen = view.findViewById(R.id.IVDialogoImagen);

        byte[] decodedString = Base64.decode(this.imagen, Base64.URL_SAFE);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        Bitmap resized = Bitmap.createScaledBitmap(decodedByte, 1500, 1500, true);

        imagen.setImageBitmap(resized);

        //asociamos el layout con el dialogo
        builder.setView(view);

        builder.setPositiveButton("Marcar como respuesta", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                comprobarRespuesta();
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.show();
    }

    public void comprobarRespuesta(){

        animar();

        if (this.correcta){

            PreguntaDesafioActivity.mostrarRespuesta(true);

        } else {

            PreguntaDesafioActivity.mostrarRespuesta(false);

        }
    }

    public void animar(){

        //colores que vamos a usar
        int fondo =  getResources().getColor(R.color.primary_dark);

        //animacion que se va a ejecutar
        ValueAnimator valueAnimator;

        if (correcta){

            int verde = getResources().getColor(R.color.verde);

            valueAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), fondo, verde);

        } else {

            //region Animacion de vibrar
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.error);

            InterpolationButtonRespuesta interpolator = new InterpolationButtonRespuesta(0.2, 20);
            animation.setInterpolator(interpolator);

            this.startAnimation(animation);
            //endregion

            int rojo = Color.RED;
            valueAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), fondo, rojo);
        }

        valueAnimator.setDuration(500);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                //actualizamos el color de fondo del textview
                setBackgroundColor((int) animator.getAnimatedValue());
            }

        });

        valueAnimator.start();
    }
}