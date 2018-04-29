package com.dam.ies1.questionit.juego;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dam.ies1.questionit.entidades.Desafio;
import com.dam.ies1.questionit.entidades.Partida;
import com.dam.ies1.questionit.entidades.Perfil;
import com.dam.ies1.questionit.entidades.Pregunta;
import com.dam.ies1.questionit.entidades.Respuesta;
import com.dam.ies1.questionit.R;
import com.dam.ies1.questionit.juego.crearPregunta.CrearPreguntaActivity;
import com.dam.ies1.questionit.juego.crearPregunta.FiltroTagsDialog;
import com.dam.ies1.questionit.juego.desafio.CargarDesafios;
import com.dam.ies1.questionit.juego.desafio.DesafioActivity;
import com.dam.ies1.questionit.juego.desafio.ItemDesafioAdapter;
import com.dam.ies1.questionit.juego.home.CargarPregContUsu;
import com.dam.ies1.questionit.juego.home.CargarPreguntas;
import com.dam.ies1.questionit.juego.home.ItemPreguntaAdapter;
import com.dam.ies1.questionit.juego.jugador.CargarJugadores;
import com.dam.ies1.questionit.juego.jugador.ItemJugadorAdapter;
import com.dam.ies1.questionit.juego.pvp.CargarPartidas;
import com.dam.ies1.questionit.juego.pvp.ItemPartidaAdapter;
import com.dam.ies1.questionit.juego.pvp.PartidaActivity;
import com.dam.ies1.questionit.perfil.PerfilActivity;
import com.dam.ies1.questionit.preguntas.ContestarPregunta;
import com.dam.ies1.questionit.preguntas.PreguntasActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.games.Games;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JuegoActivity extends AppCompatActivity implements FiltroTagsDialog.TagsSeleccionados, CargarPreguntas.ICargarPreguntas,
        CargarPregContUsu.ICargarPregContUsu, ContestarPregunta.IContestarPregunta, CargarDesafios.ICargarDesafios,
        CargarPartidas.ICargarPartidas, CargarJugadores.ICargarJugadores {

    static Context context;
    CoordinatorLayout CL;
    FloatingActionButton fab;
    SectionsPagerAdapter sectionsPagerAdapter;
    ViewPager viewPager;
    static ArrayList<String> tagsSelec = new ArrayList<>();
    int nPagina;
    static String IDUsuario;
    static ProgressBar PB;
    static int posicion; //posicion en el ArrayList en la pregunta que va responder el usuario
    public static ArrayList<Pregunta> alPreguntas = new ArrayList<>();
    public static ArrayList<Desafio> alDesafios = new ArrayList<>();
    public static ArrayList<Partida> alPartidas = new ArrayList<>();
    public static ArrayList<Perfil> alJugadores = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;

        IDUsuario = getIntent().getStringExtra("IDUSUARIO");

        CL = findViewById(R.id.main_content);
        PB = findViewById(R.id.PBAJ);
        fab = findViewById(R.id.fabJuego);
        fab.requestFocus();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //dependiendo del numero de pagina hacemos una cosa u otra
                switch (nPagina){
                    case 0: empezarPartidaLyt();
                        break;
                    case 1: crearPreguntaLyt();
                        break;
                    case 2: empezarDesafioLyt();
                        break;
                }
            }
        });

        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        viewPager = findViewById(R.id.container_juego);
        viewPager.setAdapter(sectionsPagerAdapter);

        final TabLayout tabLayout = findViewById(R.id.tabs);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);

                nPagina = tab.getPosition();

                switch (nPagina){
                    case 0: getSupportActionBar().setTitle("PvP");
                        break;
                    case 1: getSupportActionBar().setTitle("Preguntas");
                        break;
                    case 2: getSupportActionBar().setTitle("Desafíos");
                        break;
                    case 3:
                        getSupportActionBar().setTitle("Buscar jugadores");
                        ObjectAnimator animator = ObjectAnimator.ofFloat(fab, View.ALPHA, 1f, 0f);
                        animator.setDuration(300);
                        animator.start();
                        animator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                fab.setVisibility(View.INVISIBLE);
                            }
                        });
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);

                fab.setVisibility(View.VISIBLE);

                if (nPagina == 3) {
                    ObjectAnimator animator = ObjectAnimator.ofFloat(fab, View.ALPHA, 0f, 1f);
                    animator.setDuration(300);
                    animator.start();

                }
            }
        });

        PlaceholderFragment.IDUsuario = IDUsuario;
        viewPager.setCurrentItem(1); //por defecto vamos al apartado home

        //si la cuenta tiene permisos de google play services
        if (GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), Games.SCOPE_GAMES_LITE)) {
            //hacemos que salga el mensaje de bienvenida de la play store
            Games.getGamesClient(this, GoogleSignIn.getLastSignedInAccount(this)).setViewForPopups(CL);
        } else {
            GoogleSignIn.requestPermissions(JuegoActivity.this, 100, GoogleSignIn.getLastSignedInAccount(this), Games.SCOPE_GAMES_LITE);
        }

    }

    public void empezarPartidaLyt(){
        Intent intent = new Intent(this, PartidaActivity.class);
        intent.putExtra("IDUSUARIO", IDUsuario);
        startActivityForResult(intent, 2);
    }

    public void crearPreguntaLyt(){
        Intent intent = new Intent(this, CrearPreguntaActivity.class);
        intent.putExtra("IDUSUARIO", IDUsuario);
        startActivity(intent);
    }

    public void empezarDesafioLyt(){
        Intent intent = new Intent(this, DesafioActivity.class);
        intent.putExtra("IDUSUARIO", IDUsuario);
        startActivityForResult(intent, 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_juego, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.MIPerfil:
                Intent intent = new Intent(this, PerfilActivity.class);
                intent.putExtra("IDUSUARIO", IDUsuario);
                intent.putExtra("IDUSUARIOMOSTRAR",IDUsuario);
                startActivityForResult(intent, 3);
                break;
            case R.id.MIClasificaciones:
                this.mostrarClasificaciones();
                break;
            case R.id.MILogros:
                this.mostrarLogros();
                break;
            case R.id.MICerrarSesion: finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void mostrarClasificaciones(){
        Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .getAllLeaderboardsIntent()
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, 101);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(JuegoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void mostrarLogros(){
        Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .getAchievementsIntent()
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, 102);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //se contesto a la pregunta
        if (requestCode == 0){
            PlaceholderFragment.adapterPreguntas.notifyDataSetChanged();

            //se hizo un desafio
        } else if (requestCode == 1){
            //si se completo el desafio
            if (resultCode == RESULT_OK) {
                PlaceholderFragment.SWLDesafios.setRefreshing(true);
                CargarDesafios cargarDesafios = new CargarDesafios(this, this);
                cargarDesafios.execute(IDUsuario);
            }

            //se creo una partida
        } else if (requestCode == 2){
            //si se completo la partida
            if (resultCode == RESULT_OK) {
                PlaceholderFragment.SWLPartidas.setRefreshing(true);
                CargarPartidas cargarPartidas = new CargarPartidas(this, this);
                cargarPartidas.execute(IDUsuario);
            }
        } if (requestCode == 100) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(this, "No se ha podido iniciar sesión. La cuenta de correo usada no esta registrada en Google Play Services", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        //deshabilitamos el boton de ir hacia atras
    }

    @Override
    public void recibirPreguntas(String resultado, int codError) {

        //si no se produjeron errores
        if (codError == 0) {

            try {
                alPreguntas.clear();

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

                        alPreguntas.add(pregunta);

                    }

                    CargarPregContUsu cargarPregContUsu = new CargarPregContUsu(this, this);
                    cargarPregContUsu.execute(IDUsuario);
                } else {
                    Toast.makeText(this, "No se han encontrado preguntas que mostrar", Toast.LENGTH_SHORT).show();
                    PlaceholderFragment.setAlPreguntas(alPreguntas);
                }

            } catch (Exception e) {
                Toast.makeText(this, "Se ha producido un error desconocido cargando las preguntas (1)", Toast.LENGTH_SHORT).show();
                PlaceholderFragment.SWLPreguntas.setRefreshing(false);
            }
        } else {
            //resultado contiene el mensaje de error
            Toast.makeText(this, resultado, Toast.LENGTH_SHORT).show();
            PlaceholderFragment.SWLPreguntas.setRefreshing(false);
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

                        for (Pregunta pregunta : alPreguntas){
                            if (pregunta.getiDPregunta() == idPregunta){
                                pregunta.setEstado(jsonObject.getInt("Estado"));
                                break;
                            }
                        }
                    }

                } catch (Exception e) {
                    Toast.makeText(this, "Se ha producido un error desconocido cargando las preguntas (2)", Toast.LENGTH_SHORT).show();
                    //el SWL se oculta despues
                }
            }

            PlaceholderFragment.setAlPreguntas(alPreguntas);

        } else {
            //resultado contiene el mensaje de error
            Toast.makeText(this, resultado, Toast.LENGTH_SHORT).show();
            PlaceholderFragment.SWLPreguntas.setRefreshing(false);
        }
    }

    @Override
    public void recibirTags(ArrayList<String> tags) {

        JSONObject jsonObject = new JSONObject();
        for (int i = 0; i < tags.size(); i++){
            try {
                jsonObject.put("TAG" + i, tags.get(i));
            } catch (JSONException e) {
            }
        }

        PlaceholderFragment.jsonTags = jsonObject;
    }

    @Override
    public void recibirResRespuesta(boolean resultado, int codError) {
        //si no hubo errores
        if (codError == 0){

            //si el resultado es true es que la pregunta esta lista para ser contestada
            if (resultado){

                //vamos a la activity para que conteste la pregunta
                Intent intent = new Intent(this, PreguntasActivity.class);
                intent.putExtra("ID_PREG", alPreguntas.get(posicion).getiDPregunta());
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
    public void recibirDesafios(String resultado, int codError) {
        //si no se produjeron errores
        if (codError == 0) {

            try {
                String record = "0"; //inicializada por obligacion
                alDesafios.clear();

                if (!resultado.equals("null")) {

                    JSONArray json = new JSONArray(resultado);

                    //recorremos el array de jsons
                    for (int i = 0; i < json.length(); i++) {

                        //obtenemos cada json dentro del array
                        JSONObject jsonObject = json.getJSONObject(i);

                        //si no es el ultimo json
                        if (i != json.length() - 1){
                            Desafio desafio = new Desafio();
                            desafio.setTag(jsonObject.getString("Tag"));
                            desafio.setDificultad(jsonObject.getString("Dificultad"));
                            desafio.setRespCorrectas(jsonObject.getInt("RespCorrectas"));
                            desafio.setFecha(jsonObject.getString("Fecha"));
                            alDesafios.add(desafio);
                        } else {
                            record = jsonObject.getString("Record");
                            if (record.equals("null")) record = "0";
                        }
                    }

                    PlaceholderFragment.setAlDesafios(alDesafios, record);

                } else {
                    Toast.makeText(this, "No se han encontrado desafios que mostrar", Toast.LENGTH_SHORT).show();
                    PlaceholderFragment.setAlDesafios(alDesafios, record);
                }

            } catch (Exception e) {
                Toast.makeText(this, "Se ha producido un error desconocido cargando los desafios", Toast.LENGTH_SHORT).show();
                PlaceholderFragment.SWLDesafios.setRefreshing(false);
            }
        } else {
            //resultado contiene el mensaje de error
            Toast.makeText(this, resultado, Toast.LENGTH_SHORT).show();
            PlaceholderFragment.SWLDesafios.setRefreshing(false);
        }
    }

    @Override
    public void recibirPartidas(String resultado, int codError) {
        //si no se produjeron errores
        if (codError == 0) {

            try {
                String victorias = "0"; //inicializada por obligacion
                String nombre = ""; //inicializada por obligacion
                String imagenPerfil = ""; //inicializada por obligacion
                alPartidas.clear();

                if (!resultado.equals("null")) {

                    JSONArray json = new JSONArray(resultado);

                    //recorremos el array de jsons
                    for (int i = 0; i < json.length(); i++) {

                        //obtenemos cada json dentro del array
                        JSONObject jsonObject = json.getJSONObject(i);

                        //si es el primer json
                        if (i == 0) {
                            nombre = jsonObject.getString("Nombre");
                            imagenPerfil = jsonObject.getString("ImagenPerfil");

                            //si es antes del ultimo json
                        } else if (i < json.length() - 1) {

                            Partida partida = new Partida();
                            partida.setiDPartida(jsonObject.getInt("IDPartida"));
                            partida.setiDUsuario1(jsonObject.getString("IDUsuario1"));
                            partida.setiDUsuario2(jsonObject.getString("IDUsuario2"));
                            partida.setPuntos1(jsonObject.getInt("Puntos1"));
                            try {
                                //ponemos la puntuacion del segundo usuario
                                partida.setPuntos2(jsonObject.getInt("Puntos2"));
                            } catch (Exception e){
                                //si peto es que aun no contesto, ponemos la puntuacion como -1
                                //este -1 servira posteriormente para ver si se puede clicar en el ListView
                                //este -1 servira posteriormente para comprobar
                                partida.setPuntos2(-1);
                            }

                            //si la partida la creo el usuario que inicio sesion
                            if (partida.getiDUsuario1().equals(IDUsuario)) {
                                partida.setNombre1(nombre);
                                partida.setImagen1(imagenPerfil);
                                partida.setNombre2(jsonObject.getString("Nombre"));
                                partida.setImagen2(jsonObject.getString("ImagenPerfil"));

                                //si no es que la creo otra persona
                            } else {
                                partida.setNombre1(jsonObject.getString("Nombre"));
                                partida.setImagen1(jsonObject.getString("ImagenPerfil"));
                                partida.setNombre2(nombre);
                                partida.setImagen2(imagenPerfil);
                            }

                            partida.setJugando(jsonObject.getInt("Jugando"));

                            //añadimos la partida al ArrayList
                            alPartidas.add(partida);

                            //si no es que es el ultimo json
                        } else {
                            victorias = jsonObject.getString("Victorias");
                        }
                    }

                    PlaceholderFragment.setAlPartidas(alPartidas, victorias);


                } else {
                    Toast.makeText(this, "No se han encontrado partidas que mostrar", Toast.LENGTH_SHORT).show();
                    PlaceholderFragment.setAlPartidas(alPartidas, victorias);
                }

            } catch (Exception e) {
                Toast.makeText(this, "Se ha producido un error desconocido cargando las partidas " + e.getMessage() , Toast.LENGTH_SHORT).show();
                PlaceholderFragment.SWLPartidas.setRefreshing(false);
            }
        } else {
            //resultado contiene el mensaje de error
            Toast.makeText(this, resultado, Toast.LENGTH_SHORT).show();
            PlaceholderFragment.SWLPartidas.setRefreshing(false);
        }
    }

    @Override
    public void recibirJugadores(String resultado, int codError) {

        //si no se produjeron errores
        if (codError == 0) {

            alJugadores.clear();

            if (!resultado.equals("null")){

                try {

                    JSONArray json = new JSONArray(resultado);

                    for (int i = 0; i < json.length(); i++) {

                        JSONObject jsonObject = json.getJSONObject(i);

                        Perfil perfil = new Perfil(jsonObject.getString("IDUsuario"));
                        perfil.setNombre(jsonObject.getString("Nombre"));
                        perfil.setPuntuacionTotal(jsonObject.getInt("PuntuacionTotal"));
                        perfil.setPopularidadTotal(jsonObject.getInt("PopularidadTotal"));
                        perfil.setVictorias(jsonObject.getInt("Victorias"));
                        perfil.setImagenPerfil(jsonObject.getString("ImagenPerfil"));

                        alJugadores.add(perfil);

                    }

                } catch (Exception e) {
                    Toast.makeText(this, "Se ha producido un error desconocido cargando los jugadores", Toast.LENGTH_SHORT).show();
                    //el SWL se oculta despues
                }
            } else {
                Toast.makeText(this, "No se han encontrado usuarios", Toast.LENGTH_SHORT).show();
            }

            PlaceholderFragment.setAlJugadores(alJugadores);

        } else {
            //resultado contiene el mensaje de error
            Toast.makeText(this, resultado, Toast.LENGTH_SHORT).show();
            PlaceholderFragment.SWLJugadores.setRefreshing(false);
        }
    }

    //region Click en ListView partidas
    //el startActivityForResult esta aqui porque si no no recibe bien el result
    public static void cargarActPartida(int i){
        Intent intent = new Intent(context, PartidaActivity.class);
        intent.putExtra("IDUSUARIO", IDUsuario);
        intent.putExtra("IDPARTIDA", alPartidas.get(i).getiDPartida());
        ((Activity)context).startActivityForResult(intent, 2);
    }
    //endregion

    public static class PlaceholderFragment extends Fragment {

        static String IDUsuario;

        //region Partidas
        static ArrayList<Partida> alPartidas = new ArrayList<>();
        static ItemPartidaAdapter adapterPartidas;
        static SwipeRefreshLayout SWLPartidas;
        static TextView TVVictorias;
        static String nVictorias = ""; //string para facilitar el trabajo
        boolean primeraVezPart = true; //variable que indica si es la primera vez se accede al tab layout partidas
        //endregion

        //region Preguntas
        static ArrayList<Pregunta> alPreguntas = new ArrayList<>();
        static ItemPreguntaAdapter adapterPreguntas;
        static SwipeRefreshLayout SWLPreguntas;
        static JSONObject jsonTags = new JSONObject();
        boolean primeraVezPreg = true; //variable que indica si es la primera vez se accede al tab layout preguntas
        //endregion

        //region Desafios
        static ArrayList<Desafio> alDesafios = new ArrayList<>();
        static ItemDesafioAdapter adapterDesafios;
        static SwipeRefreshLayout SWLDesafios;
        static TextView TVMejorPuntuacion;
        static String record = ""; //string para facilitar el trabajo
        boolean primeraVezDes = true;//variable que indica si es la primera vez se accede al tab layout desafios
        //endregion

        //region Jugadores
        static ArrayList<Perfil> alJugadores = new ArrayList<>();
        static ItemJugadorAdapter adapterJugadores;
        static SwipeRefreshLayout SWLJugadores;
        //endregion

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View rootView = null;

            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                case 1:
                    rootView = inflater.inflate(R.layout.fragment_juego_pvp, container, false);
                    cargarJuegoPartidas(rootView);
                    break;
                case 2:
                    rootView = inflater.inflate(R.layout.fragment_juego_home, container, false);
                    cargarJuegoHome(rootView);
                    break;
                case 3:
                    rootView = inflater.inflate(R.layout.fragment_juego_desafios, container, false);
                    cargarJuegoDesafios(rootView);
                    break;
                case 4:
                    rootView = inflater.inflate(R.layout.fragment_juego_jugadores, container, false);
                    cargarJuegoJugadores(rootView);
            }

            return rootView;
        }

        public void cargarJuegoPartidas(View rootView){
            TVVictorias = rootView.findViewById(R.id.TVPVictorias);
            TVVictorias.setText(nVictorias);

            ListView LVPartidas = rootView.findViewById(R.id.LVPartidas);
            LVPartidas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //si el usuario dos tiene -1 es que aun no participo por lo que dejamos clicar en el ListView
                    //si ponemos esto haciendo override al metodo isEnabled en el adaptador se quitan las lineas divisorias del ListView y queda feo
                    if(alPartidas.get(i).getPuntos2() == -1) {
                        //IMPORTANTE: no dejar clicar tampoco al usuario que creo la partida
                        //para ello miramos a ver si el ID del usuario que inicio sesion es el mismo que el que creo la partida
                        if (alPartidas.get(i).getiDUsuario1().equals(IDUsuario)){
                            Toast.makeText(getContext(), "Esperando respuesta del oponente", Toast.LENGTH_SHORT).show();
                        } else {
                            //si no miramos a ver si aun se esta jugando
                            if (alPartidas.get(i).getJugando() == 1){
                                Toast.makeText(getContext(), "Un jugador te esta desafiando!. Vuelve más tarde para jugar contra él", Toast.LENGTH_SHORT).show();

                                //si no es que ya se puede contestar
                            } else {
                                JuegoActivity.cargarActPartida(i);
                            }
                        }
                    } else {
                        Toast.makeText(getContext(), "Ya se ha acabado la partida!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            adapterPartidas = new ItemPartidaAdapter(getContext(), alPartidas);
            LVPartidas.setAdapter(adapterPartidas);

            SWLPartidas = rootView.findViewById(R.id.SWLPartidas);
            if (primeraVezPart){
                cargarPartidas();
                SWLPartidas.setRefreshing(true);
                primeraVezPart = false;
            }
            SWLPartidas.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    cargarPartidas();
                }
            });
        }

        public void cargarPartidas(){
            CargarPartidas cargarPartidas = new CargarPartidas(getActivity().getApplicationContext(), (JuegoActivity)getActivity());
            cargarPartidas.execute(IDUsuario);
        }

        public static void setAlPartidas(ArrayList<Partida> alPartidasAct, String victorias){
            alPartidas.clear();

            nVictorias = victorias;
            TVVictorias.setText(nVictorias);

            //si no se hace este bucle el adaptador no se actualiza
            for (Partida partida: alPartidasAct){
                alPartidas.add(partida);
            }

            adapterPartidas.notifyDataSetChanged();

            SWLPartidas.setRefreshing(false);
        }

        public void cargarJuegoHome(View rootView) {

            final EditText ETFiltroTitulo = rootView.findViewById(R.id.ETBusqueda);
            final Spinner SBusqueda = rootView.findViewById(R.id.SBusqueda);

            Button btnTags = rootView.findViewById(R.id.btnTags);
            btnTags.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    FiltroTagsDialog filtroTagsDialog = new FiltroTagsDialog();

                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("TAGSSELEC", tagsSelec);

                    filtroTagsDialog.setArguments(bundle);

                    filtroTagsDialog.show(getActivity().getFragmentManager(), "FiltroTagsDialog");
                }
            });

            ListView LVPreguntas = rootView.findViewById(R.id.LVPreguntas);
            LVPreguntas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    //si es igual a -1 es que aun no se contesto por lo que dejamos clicar en el item, si no no
                    //tambien dejamos clicar si es -2 (en el caso de que haya contestado pero haya dado error en la base de datos al actualizar el estado)
                    //si ponemos esto haciendo override al metodo isEnabled en el adaptador se quitan las lineas divisorias del ListView y queda feo
                    if(alPreguntas.get(i).getEstado() == -1 || alPreguntas.get(i).getEstado() == -2) {

                        //guardamos en el layout principal la posicion de la pregunta en el ArrayList
                        JuegoActivity.posicion = i;

                        //insertamos la pregunta como fallada, luego si acierta actualizaremos el estado
                        //esto se hace por si el usuario cierra la aplicacion para hacer trampas le cuente como respuesta erronea
                        ContestarPregunta contestarPregunta = new ContestarPregunta(getActivity(), (JuegoActivity) getActivity(), PB);

                        //el alPreguntas.get(i).getiDUsuario() devuelve el creador de la pregunta
                        contestarPregunta.execute(IDUsuario, alPreguntas.get(i).getiDUsuario(), String.valueOf(alPreguntas.get(i).getiDPregunta()));
                    } else {
                        Toast.makeText(getContext(), "Ya has contestado a esta pregunta!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            adapterPreguntas = new ItemPreguntaAdapter(getContext(), alPreguntas);
            LVPreguntas.setAdapter(adapterPreguntas);

            SWLPreguntas = rootView.findViewById(R.id.SWLPreguntas);
            if (primeraVezPreg){
                cargarPreguntas();
                SWLPreguntas.setRefreshing(true);
                primeraVezPreg = false;
            }
            SWLPreguntas.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    cargarPreguntas();
                }
            });

            ImageView buscar = rootView.findViewById(R.id.IVBuscar);
            buscar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SWLPreguntas.setRefreshing(true);

                    String filtro = ""; //inicializada por obligacion
                    switch (SBusqueda.getSelectedItem().toString()){
                        case "Más recientes": filtro = "Preguntas.FechaCreacion DESC";
                            break;
                        case "Más antiguas": filtro = "Preguntas.FechaCreacion ASC";
                            break;
                        case "Más populares": filtro = "Preguntas.Popularidad DESC";
                            break;
                    }

                    CargarPreguntas cargarPreguntas = new CargarPreguntas(getActivity().getApplicationContext(), (JuegoActivity)getActivity());
                    cargarPreguntas.execute(ETFiltroTitulo.getText().toString(), filtro, jsonTags.toString());
                }
            });
        }

        public void cargarPreguntas(){
            CargarPreguntas cargarPreguntas = new CargarPreguntas(getActivity().getApplicationContext(), (JuegoActivity)getActivity());
            cargarPreguntas.execute();
        }

        public static void setAlPreguntas(ArrayList<Pregunta> alPreguntasAct){
            alPreguntas.clear();

            //si no se hace este bucle el adaptador no se actualiza
            for (Pregunta pregunta : alPreguntasAct){
                alPreguntas.add(pregunta);
            }

            adapterPreguntas.notifyDataSetChanged();

            SWLPreguntas.setRefreshing(false);
        }

        public void cargarJuegoDesafios(View rootView){

            TVMejorPuntuacion = rootView.findViewById(R.id.TVDMejorPuntuacion);
            TVMejorPuntuacion.setText(record);

            ListView LVDesafios = rootView.findViewById(R.id.LVDesafios);
            adapterDesafios = new ItemDesafioAdapter(getContext(), alDesafios);
            LVDesafios.setAdapter(adapterDesafios);

            SWLDesafios = rootView.findViewById(R.id.SWLDesafios);
            if (primeraVezDes){
                cargarDesafios();
                SWLDesafios.setRefreshing(true);
                primeraVezDes = false;
            }
            SWLDesafios.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    cargarDesafios();
                }
            });
        }

        public void cargarDesafios(){
            CargarDesafios cargarDesafios = new CargarDesafios(getActivity().getApplicationContext(), (JuegoActivity)getActivity());
            cargarDesafios.execute(IDUsuario);
        }

        public static void setAlDesafios(ArrayList<Desafio> alDesafiosAct, String maxRespCorrectas){
            alDesafios.clear();

            record = maxRespCorrectas;
            TVMejorPuntuacion.setText(record);

            //si no se hace este bucle el adaptador no se actualiza
            for (Desafio desafio: alDesafiosAct){
                alDesafios.add(desafio);
            }

            adapterDesafios.notifyDataSetChanged();

            SWLDesafios.setRefreshing(false);
        }

        public void cargarJuegoJugadores(View rootView){

            ImageView buscarJugador = rootView.findViewById(R.id.IVBuscarJugador);

            EditText ETFiltroNombre = rootView.findViewById(R.id.ETBusquedaJugadores);
            ETFiltroNombre.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                    //si se dio al enter en el teclado
                    if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {

                        //ocultamos el teclado
                        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(((Activity)context).getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

                        //simulamos que se clicaco la imagen de buscar
                        buscarJugador.performClick();
                    }
                    return false;
                }
            });

            ListView LVJugadores = rootView.findViewById(R.id.LVJugadores);
            LVJugadores.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(context, PerfilActivity.class);
                    intent.putExtra("IDUSUARIO", IDUsuario);
                    intent.putExtra("IDUSUARIOMOSTRAR",alJugadores.get(i).getiDUsuario());
                    context.startActivity(intent);
                }
            });
            adapterJugadores = new ItemJugadorAdapter(getContext(), alJugadores);
            LVJugadores.setAdapter(adapterJugadores);
            SWLJugadores = rootView.findViewById(R.id.SWLJugadores);
            SWLJugadores.setEnabled(false);

            buscarJugador.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SWLJugadores.setRefreshing(true);

                    CargarJugadores cargarJugadores = new CargarJugadores(getActivity().getApplicationContext(), (JuegoActivity)getActivity());
                    cargarJugadores.execute(ETFiltroNombre.getText().toString());
                }
            });
        }

        public static void setAlJugadores(ArrayList<Perfil> alJugadoresAct){
            alJugadores.clear();

            //si no se hace este bucle el adaptador no se actualiza
            for (Perfil perfil: alJugadoresAct){
                alJugadores.add(perfil);
            }

            adapterJugadores.notifyDataSetChanged();

            SWLJugadores.setRefreshing(false);
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}
