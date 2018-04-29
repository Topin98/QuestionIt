package com.dam.ies1.questionit.juego.crearPregunta;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.graphics.drawable.shapes.Shape;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import com.dam.ies1.questionit.R;
public class TagTextView extends AppCompatTextView implements View.OnClickListener {

    private boolean selec = false;

    public boolean isSelec() {
        return this.selec;
    }

    public TagTextView(Context context) {
        super(context);
        setOnClickListener(this);
    }

    public TagTextView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(this);
    }

    public TagTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnClickListener(this);
    }

    @Override
    public void onClick(final View view) {

        //drawable que define el estilo del textview
        LayerDrawable layerDrawable = (LayerDrawable) view.getBackground();

        //parte del drawable donde se define el color de los textview
        final GradientDrawable gradientDrawable = (GradientDrawable) layerDrawable.findDrawableByLayerId(R.id.tagstyle);

        //colores que vamos a usar
        int fondoTV = getResources().getColor(R.color.fondoTV);
        int verde = getResources().getColor(R.color.verde);

        //animacion que se va a ejecutar
        ValueAnimator valueAnimator;

        //si no esta seleccionado es que esta en blanco
        if (!selec){

            //cambiamos el color de fondo del original al verde
            valueAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), fondoTV, verde);

            //indicamos que se selecciono
            this.selec = true;

        } else {

            //volvemos a cambiar el color de fondo al original
            valueAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), verde, fondoTV);

            //indicamos que se desmarco
            this.selec = false;
        }

        valueAnimator.setDuration(1500);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                //actualizamos el color de fondo del textview
                gradientDrawable.setColor((int) animator.getAnimatedValue());
            }

        });

        valueAnimator.start();
    }
}