package com.dam.ies1.questionit.juego.pvp;

import android.content.Intent;
import android.os.Handler;
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
import com.dam.ies1.questionit.entidades.Partida;
import com.dam.ies1.questionit.entidades.Pregunta;
import com.dam.ies1.questionit.entidades.Respuesta;
import com.dam.ies1.questionit.juego.JuegoActivity;
import com.dam.ies1.questionit.preguntas.ReportDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class PartidaActivity extends AppCompatActivity implements InsertarPartida.IInsercionPartidaCorrecta, CargarPreguntasPartida.ICargarPreguntasPartida,
        ActualizarPartida.IActualizacionPartidaCorrecta, IniciarPartida.IIniciarPartidaCorrecta {

    ProgressBar PB;
    TextView TVPreparando;
    ActionBar actionBar;
    String IDUsuario;
    String IDUsuarioDesafiar;
    int IDPartida;
    static ArrayList<Pregunta> alPreguntas = new ArrayList<>();
    Handler handler = new Handler();
    int respuestasContestadas = 0;
    int respCorrectas = 0;
    boolean isPlaying = true; //variable que indica si aun se esta jugando
    Partida partida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partida);
        getSupportActionBar().setTitle("Desafío");

        //definimos la ActionBar para luego ponerle el boton de ir a Home
        actionBar = getSupportActionBar();

        IDUsuario = getIntent().getStringExtra("IDUSUARIO"); //ID del usuario que crea la partida
        IDUsuarioDesafiar = getIntent().getStringExtra("IDUSUARIO_DESAFIAR"); //ID del usuario al que se va a desafiar
                                                                              //(se recibe al clicar en la opcion desafiar del menu perfil)
        IDPartida = getIntent().getIntExtra("IDPARTIDA", -1); //ID de la partida que solo se recibe al pulsar en un item del ListView

        PB = findViewById(R.id.PBP);
        TVPreparando = findViewById(R.id.TVPPreparando);

        this.empezarAnimacion();

        //si el id del usuario2 es null es que la partida es nueva ya que se clico en el boton de crear partida
        if (IDPartida == -1){
            //intertamos la nueva pregunta
            insertarPartida();
        } else {
            //metodo que busca la partida en el ArrayList de JuegoActivity
            buscarPartida();

            //metodo que pone a 0 la puntuacion del segundo usuario
            //(por si se sale que no pueda volver a entrar)
            iniciarPartida();
        }
    }

    public void empezarAnimacion() {
        TVPreparando.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.cargandodesafios);
        TVPreparando.startAnimation(animation);
    }

    public void insertarPartida() {
        InsertarPartida insertarPartida = new InsertarPartida(this, this);

        //si aun no se eligio a que usuario desafiar
        if (IDUsuarioDesafiar == null) {
            insertarPartida.execute(IDUsuario);

            //si no es que ya se elegio el usuario desde su propio perfil
        } else {
            insertarPartida.execute(IDUsuario, IDUsuarioDesafiar);
        }
    }

    public void buscarPartida(){

        try{
            //si tiene una api superior o igual a 24
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                //obtenemos la pregunta con una expresion lambda
                partida = JuegoActivity.alPartidas.stream().filter(x -> x.getiDPartida() == IDPartida).findFirst().get();
            } else {
                for (Partida part: JuegoActivity.alPartidas){
                    if (part.getiDPartida() == IDPartida){
                        partida = part;
                        break;
                    }
                }
                if (partida == null) throw new Exception();
            }
        } catch (Exception e){
            Toast.makeText(this, "No se ha podido cargar la pregunta. Inténtelo de nuevo más tarde", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void iniciarPartida(){
        IniciarPartida iniciarPartida = new IniciarPartida(this, this);
        iniciarPartida.execute(String.valueOf(partida.getiDPartida()), String.valueOf(partida.getiDUsuario1()));
    }

    @Override
    public void recibirResInsercion(String resultado, int codError) {

        //si no se produjeron errores
        if (codError == 0) {

            try {

                if (!resultado.equals("null")) {

                    JSONObject jsonObject = new JSONObject(resultado);

                    partida = new Partida();
                    partida.setiDPartida(jsonObject.getInt("IDPartida"));
                    partida.setiDUsuario1(jsonObject.getString("IDUsuario1"));
                    partida.setiDUsuario2(jsonObject.getString("IDUsuario2"));
                    partida.setPuntos1(jsonObject.getInt("Puntos1"));
                    partida.setPuntos2(-1); //va a ser -1 ya que no puedo contestar aun

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            TVPreparando.setText("CARGANDO PREGUNTAS");

                            cargarPreguntas();
                        }
                    }, 1000);

                } else {
                    //este seria el mismos valor que la variable resultado si hubiese petado el hilo pero en este caso no peto, si no que devuelve null
                    Toast.makeText(this, "Se ha producido un error preparando la partida. Inténtelo de nuevo más tarde", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } catch (Exception e){
                Toast.makeText(this, "Se ha producido un error desconocido", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else{
            //resultado contiene el mensaje de error
            Toast.makeText(this, resultado, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void recibirResInicializacion(boolean resultado, int codError) {
        //si no se produjeron errores
        if (codError == 0) {

            try {

                if (resultado) {

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            TVPreparando.setText("CARGANDO PREGUNTAS");

                            cargarPreguntas();
                        }
                    }, 1000);

                } else {
                    //este seria el mismos valor que la variable resultado si hubiese petado el hilo pero en este caso no peto, si no que devuelve null
                    Toast.makeText(this, "Se ha producido un error preparando la partida. Inténtelo de nuevo más tarde", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } catch (Exception e){
                Toast.makeText(this, "Se ha producido un error desconocido", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else{
            //resultado contiene el mensaje de error
            Toast.makeText(this, "Se ha producido un error preparando la partida. Inténtelo de nuevo más tarde", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void cargarPreguntas(){
        CargarPreguntasPartida cargarPreguntasPartida = new CargarPreguntasPartida(this,this);
        cargarPreguntasPartida.execute();
    }

    @Override
    public void recibirPreguntasPartida(String resultado, int codError) {

        //no hace falta limpiar el ArrayList porque siempre estara vacio y pasaremos por aqui solo una vez

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
                                    Intent intent = new Intent(PartidaActivity.this, PreguntaPartidaActivity.class);
                                    startActivityForResult(intent, 1);
                                }
                            }, 1000);
                        }
                    }, 1000);


                } else {
                    Toast.makeText(this, "No se han encontrado preguntas. Inténtelo de nuevo más tarde", Toast.LENGTH_SHORT).show();
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
            alPreguntas.remove(0);

            if (resultCode == RESULT_OK){
                respCorrectas++;
            }

            //si aun no contesto 5 preguntas
            if (++respuestasContestadas != 5){
                //lanzamos la siguiente pregunta
                Intent intent = new Intent(PartidaActivity.this, PreguntaPartidaActivity.class);
                startActivityForResult(intent, 1);

                //si ya contesto 5 preguntas
            } else {
                //indicamos que se acabo la partida
                isPlaying = false;
                TVPreparando.setText("Puntuación: " + respCorrectas);

                //actualizamos la partida
                actualizarPartida();
            }
        }
    }

    public void actualizarPartida(){
        ActualizarPartida actualizarPartida = new ActualizarPartida(this, this, PB);
        //el IDUsuario es el que inicio sesion NO CAMBIAR o producira errores en el script php
        actualizarPartida.execute(String.valueOf(partida.getiDPartida()), IDUsuario, String.valueOf(respCorrectas), String.valueOf(partida.getPuntos1()));
    }

    @Override
    public void recibirActPartida(boolean resultado, int codError) {

        //ponemos el boton de ir hacia atras
        actionBar.setDisplayHomeAsUpEnabled(true);

        //si no hubo errores
        if (codError == 0){

            //si el resultado es true es que se actualizaron los datos correctamente
            if (resultado){

                Toast.makeText(this, "Se han actualizado las puntuaciones!", Toast.LENGTH_SHORT).show();

                //indicamos que se inserto el desafio por lo que hay que actualizar el ListView que los contiene
                Intent intent = getIntent();
                setResult(RESULT_OK, intent);

                //si no es que algo fue mal
            } else {
                Toast.makeText(this, "No se han podido actualizar los resultados. Inténtelo de nuevo más tarde.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No se han podido actualizar los resultados. Inténtelo de nuevo más tarde.", Toast.LENGTH_SHORT).show();
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