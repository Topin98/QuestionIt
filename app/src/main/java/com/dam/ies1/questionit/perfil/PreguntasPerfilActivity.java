package com.dam.ies1.questionit.perfil;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dam.ies1.questionit.R;
import com.dam.ies1.questionit.entidades.Pregunta;
import com.dam.ies1.questionit.entidades.Respuesta;
import com.dam.ies1.questionit.juego.JuegoActivity;
import com.dam.ies1.questionit.juego.home.CargarPregContUsu;
import com.dam.ies1.questionit.juego.home.ItemPreguntaAdapter;
import com.dam.ies1.questionit.preguntas.ContestarPregunta;
import com.dam.ies1.questionit.preguntas.PreguntasActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class PreguntasPerfilActivity extends AppCompatActivity implements CargarPreguntasPerfil.ICargarPreguntasPerfil, CargarPregContUsu.ICargarPregContUsu,
        ContestarPregunta.IContestarPregunta{

    String IDUsuario;
    String IDUsuarioMostrar;
    SwipeRefreshLayout SWLPreguntasPerfil;
    ListView LVPreguntasPerfil;
    ProgressBar PB;
    ActionBar actionBar;
    ItemPreguntaAdapter adapterPreguntasPerfil;
    public static ArrayList<Pregunta> alPreguntasPerfil = new ArrayList<>();
    int posicion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preguntas_perfil);
        getSupportActionBar().setTitle("Preguntas perfil");

        //definimos la ActionBar y le ponemos el boton de ir a Home
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //ID del usuario que lanzo la activity
        IDUsuario = getIntent().getStringExtra("IDUSUARIO");

        //ID del usuario del que se van a mostrar las preguntas
        IDUsuarioMostrar = getIntent().getStringExtra("IDUSUARIOMOSTRAR");

        PB = findViewById(R.id.PBPP);

        LVPreguntasPerfil = findViewById(R.id.LVPreguntasPerfil);
        LVPreguntasPerfil.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //si es igual a -1 es que aun no se contesto por lo que dejamos clicar en el item, si no no
                //tambien dejamos clicar si es -2 (en el caso de que haya contestado pero haya dado error en la base de datos al actualizar el estado)
                //si ponemos esto haciendo override al metodo isEnabled en el adaptador se quitan las lineas divisorias del ListView y queda feo
                if(alPreguntasPerfil.get(i).getEstado() == -1 || alPreguntasPerfil.get(i).getEstado() == -2) {

                    //guardamos en el layout principal la posicion de la pregunta en el ArrayList
                    posicion = i;

                    //insertamos la pregunta como fallada, luego si acierta actualizaremos el estado
                    //esto se hace por si el usuario cierra la aplicacion para hacer trampas le cuente como respuesta erronea
                    ContestarPregunta contestarPregunta = new ContestarPregunta(PreguntasPerfilActivity.this, PreguntasPerfilActivity.this, PB);

                    //el alPreguntas.get(i).getiDUsuario() devuelve el creador de la pregunta
                    contestarPregunta.execute(IDUsuario, alPreguntasPerfil.get(i).getiDUsuario(), String.valueOf(alPreguntasPerfil.get(i).getiDPregunta()));
                } else {
                    Toast.makeText(PreguntasPerfilActivity.this, "Ya has contestado a esta pregunta!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        alPreguntasPerfil.clear(); //este clear es por si visito un perfil anteriormente no se muestren las preguntas de otro usuario mientras se cargan las del nuevo
        adapterPreguntasPerfil = new ItemPreguntaAdapter(this, alPreguntasPerfil);
        LVPreguntasPerfil.setAdapter(adapterPreguntasPerfil);
        Toast.makeText(this, "Advertencia! Si el usuario posee una gran cantidad de pregunta la carga puede llevar algún tiempo", Toast.LENGTH_LONG).show();
        cargarPreguntasPerfil();

        SWLPreguntasPerfil = findViewById(R.id.SWLPreguntasPerfil);
        SWLPreguntasPerfil.setRefreshing(true);
        SWLPreguntasPerfil.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cargarPreguntasPerfil();
            }
        });

    }

    public void cargarPreguntasPerfil(){
        CargarPreguntasPerfil cargarPreguntasPerfil = new CargarPreguntasPerfil(this,this);
        cargarPreguntasPerfil.execute(IDUsuarioMostrar); //usuario del que queremos mostrar las preguntas
    }

    @Override
    public void recibirPreguntasPerfil(String resultado, int codError) {

        //si no se produjeron errores
        if (codError == 0) {

            try {
                alPreguntasPerfil.clear();

                if (!resultado.equals("null")) {

                    JSONArray json = new JSONArray(resultado);

                    for (int i = 0; i < json.length(); i++) {

                        JSONObject jsonObject = json.getJSONObject(i);

                        Pregunta pregunta = new Pregunta(jsonObject.getString("IDUsuario"));
                        pregunta.setiDPregunta(jsonObject.getInt("IDPregunta"));
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

                        pregunta.setDificultad(jsonObject.getString("Dificultad"));
                        pregunta.setPopularidad(jsonObject.getInt("Popularidad"));
                        pregunta.setAciertos(jsonObject.getInt("Aciertos"));
                        pregunta.setPuntuacion(); //importante que esta sea la ultima en ejecutar el set ya que se calcula a partir de la dificultad, popularidad y aciertos de la pregunta
                        pregunta.setImagenPerfil(jsonObject.getString("ImagenPerfil"));

                        alPreguntasPerfil.add(pregunta);

                    }

                    CargarPregContUsu cargarPregContUsu = new CargarPregContUsu(this, this);
                    cargarPregContUsu.execute(IDUsuario); //este usuario es el que llamo al activity
                } else {
                    Toast.makeText(this, "No se han encontrado preguntas que mostrar", Toast.LENGTH_SHORT).show();
                    SWLPreguntasPerfil.setRefreshing(false);
                }

            } catch (Exception e) {
                Toast.makeText(this, "Se ha producido un error desconocido cargando las preguntas del perfil", Toast.LENGTH_SHORT).show();
                SWLPreguntasPerfil.setRefreshing(false);
            }
        } else {
            //resultado contiene el mensaje de error
            Toast.makeText(this, resultado, Toast.LENGTH_SHORT).show();
            SWLPreguntasPerfil.setRefreshing(false);
        }
    }

    @Override
    public void recibirPregContUsu(String resultado, int codError) {
        //si no se produjeron errores
        if (codError == 0) {

            if (!resultado.equals("null")){

                try {

                    JSONArray json = new JSONArray(resultado);

                    int idPregunta;
                    for (int i = 0; i < json.length(); i++) {

                        JSONObject jsonObject = json.getJSONObject(i);
                        idPregunta = jsonObject.getInt("IDPregunta");

                        for (Pregunta pregunta : alPreguntasPerfil){
                            if (pregunta.getiDPregunta() == idPregunta){
                                pregunta.setEstado(jsonObject.getInt("Estado"));
                                break;
                            }
                        }
                    }

                } catch (Exception e) {
                    Toast.makeText(this, "Se ha producido un error desconocido cargando las preguntas del perfil", Toast.LENGTH_SHORT).show();
                    //el SWL se oculta despues
                }
            }

            adapterPreguntasPerfil.notifyDataSetChanged();
            SWLPreguntasPerfil.setRefreshing(false);

        } else {
            //resultado contiene el mensaje de error
            Toast.makeText(this, resultado, Toast.LENGTH_SHORT).show();
            SWLPreguntasPerfil.setRefreshing(false);
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
    public void recibirResRespuesta(boolean resultado, int codError) {
        //si no hubo errores
        if (codError == 0){

            //si el resultado es true es que la pregunta esta lista para ser contestada
            if (resultado){

                //vamos a la activity para que conteste la pregunta
                Intent intent = new Intent(this, PreguntasActivity.class);
                intent.putExtra("ID_PREG_PERFIL", alPreguntasPerfil.get(posicion).getiDPregunta());
                intent.putExtra("ID_USU", IDUsuario);

                startActivityForResult(intent, 0);

                //si no es que algo fue mal
            } else {
                Toast.makeText(this, "No se han podido actualizar los resultados. Inténtelo de nuevo más tarde.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No se han podido actualizar los resultados. Inténtelo de nuevo más tarde.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //se contesto a la pregunta
        if (requestCode == 0) {
            adapterPreguntasPerfil.notifyDataSetChanged();
        }
    }
}
