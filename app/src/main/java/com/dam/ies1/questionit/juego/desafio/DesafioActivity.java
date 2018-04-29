package com.dam.ies1.questionit.juego.desafio;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dam.ies1.questionit.R;
import com.dam.ies1.questionit.entidades.Pregunta;
import com.dam.ies1.questionit.entidades.Respuesta;
import com.dam.ies1.questionit.juego.JuegoActivity;
import com.dam.ies1.questionit.preguntas.ReportDialog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.games.Games;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class DesafioActivity extends AppCompatActivity implements CargarPreguntasDesafio.ICargarPreguntasDesafio, InsertarDesafio.IInsercionDesafioCorrecta{

    ProgressBar PB;
    TextView TVPreparando;
    ActionBar actionBar;
    String IDUsuario;
    String dificultad;
    String tag;
    static ArrayList<Pregunta> alPreguntas = new ArrayList<>();
    Handler handler = new Handler();
    int respCorrectas = 0;
    boolean isPlaying = true; //variable que indica si aun se esta jugando

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desafio);
        getSupportActionBar().setTitle("Desafío");

        //definimos la ActionBar para luego ponerle el boton de ir a Home
        actionBar = getSupportActionBar();

        IDUsuario = getIntent().getStringExtra("IDUSUARIO");

        PB = findViewById(R.id.PBD);
        TVPreparando = findViewById(R.id.TVDPreparando);

        elegirDificultad();
    }

    public void elegirDificultad(){

        String[] aDificultades = getResources().getStringArray(R.array.dificultades);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        builder.setItems(aDificultades, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dificultad = aDificultades[i];
                elegirTag();
            }
        });

        builder.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int posicion) {
                finish();
            }
        });

        builder.show();
    }

    public void elegirTag(){

        String[] aTags = getResources().getStringArray(R.array.tags2);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        builder.setItems(aTags, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tag = aTags[i];
                cargarPreguntasDesafio();
            }
        });

        builder.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int posicion) {
                finish();
            }
        });

        builder.show();
    }

    public void cargarPreguntasDesafio(){

        TVPreparando.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.cargandodesafios);
        TVPreparando.startAnimation(animation);

        CargarPreguntasDesafio cargarPreguntasDesafio = new CargarPreguntasDesafio(this, this);
        cargarPreguntasDesafio.execute(dificultad, tag);
    }

    @Override
    public void recibirPreguntasDesafio(String resultado, int codError) {

        alPreguntas.clear();

        //si no se produjeron errores
        if (codError == 0) {

            try {

                if (!resultado.equals("null")) {

                    JSONArray json = new JSONArray(resultado);

                    for (int i = 0; i < json.length(); i++) {

                        JSONObject jsonObject = json.getJSONObject(i);

                        Pregunta pregunta = new Pregunta();
                        pregunta.setTitulo(jsonObject.getString("Titulo"));
                        pregunta.setImagenTitulo(jsonObject.getString("ImagenTitulo"));
                        pregunta.setRespuestaCorrecta(jsonObject.getInt("RespuestaCorrecta"));

                        for (int j = 1; j <= 4; j++) {
                            Respuesta respuesta = new Respuesta();

                            respuesta.setTituloRespuesta(jsonObject.getString("Respuesta" + j));
                            respuesta.setImagenRespuesta(jsonObject.getString("Imagen" + j));

                            if (pregunta.getRespuestaCorrecta() == j) {
                                respuesta.setCorrecta(true);
                            }

                            pregunta.getAlRespuestas().add(respuesta);
                        }

                        alPreguntas.add(pregunta);
                    }

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            TVPreparando.clearAnimation();
                            TVPreparando.setText("Empecemos");

                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(DesafioActivity.this, PreguntaDesafioActivity.class);
                                    startActivityForResult(intent, 1);
                                }
                            }, 1000);
                        }
                    }, 1000);


                } else {
                    Toast.makeText(this, "No se han encontrado preguntas con esos criterios. Inténtelo de nuevo más tarde", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Se ha producido un error desconocido", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            //resultado contiene el mensaje de error
            Toast.makeText(this, resultado, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                respCorrectas++;
                alPreguntas.remove(0);

                if (alPreguntas.size() == 0) {
                    TVPreparando.setText("SE ESTAN PREPARANDO MÁS PREGUNTAS");
                    cargarPreguntasDesafio();
                } else {
                    Intent intent = new Intent(DesafioActivity.this, PreguntaDesafioActivity.class);
                    startActivityForResult(intent, 1);
                }
            } else {
                //indicamos que se acabo la partida
                isPlaying = false;
                TVPreparando.setText("Puntuación: " + respCorrectas);
                if (respCorrectas != 0) TVPreparando.setText(TVPreparando.getText() + "\nEnhorabuena!");

                insertarDesafio();

            }
        }
    }

    public void insertarDesafio(){

        InsertarDesafio insertarDesafio = new InsertarDesafio(this, this, PB);
        insertarDesafio.execute(IDUsuario, tag, dificultad, String.valueOf(respCorrectas));
    }

    @Override
    public void recibirResInsercion(boolean resultado, int codError) {

        //ponemos el boton de ir hacia atras
        actionBar.setDisplayHomeAsUpEnabled(true);

        //si no hubo errores
        if (codError == 0){

            //si el resultado es true es que se actualizaron los datos correctamente
            if (resultado){

                Toast.makeText(this, "Se han actualizado las puntuaciones!", Toast.LENGTH_SHORT).show();

                //actualizamos la clasificacion y logros del google play services
                this.actualizarGP();

                //indicamos que se inserto el desafio por lo que hay que actualizar el ListView que los contiene
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);

                //si no es que algo fue mal
            } else {
                Toast.makeText(this, "No se han podido actualizar los resultados. Inténtelo de nuevo más tarde.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No se han podido actualizar los resultados. Inténtelo de nuevo más tarde.", Toast.LENGTH_SHORT).show();
        }
    }

    public void actualizarGP(){

        //obtenemos la cuenta con la que se inicio sesion
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        //actualizamos la clasificacion de la play store
        Games.getLeaderboardsClient(this, account).submitScoreImmediate(getString(R.string.leaderboard_record_desafo), respCorrectas);

        //comprobamos si desbloqueo algun logro
        if (respCorrectas >= 5){
            Games.getAchievementsClient(getApplicationContext(), account).unlock(getString(R.string.achievement_completa_el_desafo_bronce));

            if (respCorrectas >= 20){
                Games.getAchievementsClient(getApplicationContext(), account).unlock(getString(R.string.achievement_completa_el_desafo_plata));

                if (respCorrectas >= 50){
                    Games.getAchievementsClient(getApplicationContext(), account).unlock(getString(R.string.achievement_completa_el_desafo_oro));
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home: finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        //si no se esta jugando dejamos ir hacia atras al usuario
        if (!isPlaying) super.onBackPressed();
    }
}
