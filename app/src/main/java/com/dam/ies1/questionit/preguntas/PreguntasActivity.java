package com.dam.ies1.questionit.preguntas;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dam.ies1.questionit.R;
import com.dam.ies1.questionit.entidades.Pregunta;
import com.dam.ies1.questionit.juego.JuegoActivity;
import com.dam.ies1.questionit.perfil.PerfilActivity;
import com.dam.ies1.questionit.perfil.PreguntasPerfilActivity;

public class PreguntasActivity extends AppCompatActivity implements ActualizarEstadoPregunta.IActualizarEstPreg, ReportDialog.IConfirmarReport, ReportarPregunta.IReportarPregunta{

    static Context context;
    static TextView TVResultado;
    static ProgressBar PB;
    static int posicionCorrecta;
    TextView TVTitulo;
    RespuestaButton btnRespuesta1;
    RespuestaButton btnRespuesta2;
    RespuestaButton btnRespuesta3;
    RespuestaButton btnRespuesta4;
    static MenuItem MIPerfilCreador;
    static MenuItem MIReport;
    static ActionBar actionBar;
    static Handler handler = new Handler();
    static RespuestaButton[] respuestaButtons;
    static Pregunta pregunta = null;
    int IDPregunta; //simplemente lo usamos para buscar los datos completos de la pregunta
                    //al tener como static la pregunta no hace poner como static esta variable
    static int IDPreguntaPerfil;//este si porque se usa para ver que activity llamo a esta activity
    static String IDUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preguntas);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;

        //definimos la ActionBar para luego ponerle el boton de ir a Home
        actionBar = getSupportActionBar();

        IDPregunta = getIntent().getIntExtra("ID_PREG", -1);
        IDPreguntaPerfil = getIntent().getIntExtra("ID_PREG_PERFIL", -1);
        IDUsuario = getIntent().getStringExtra("ID_USU");

        PB = findViewById(R.id.PBJP);
        TVResultado = findViewById(R.id.TVJPResultado);
        TVTitulo = findViewById(R.id.TVJPTitulo);
        btnRespuesta1 = findViewById(R.id.btnJPRespuesta1);
        btnRespuesta2 = findViewById(R.id.btnJPRespuesta2);
        btnRespuesta3 = findViewById(R.id.btnJPRespuesta3);
        btnRespuesta4 = findViewById(R.id.btnJPRespuesta4);

        //buscamos la pregunta que va a contestar el usuario
        if (IDPreguntaPerfil == -1) buscarPreguntaPrincipal();
        else buscarPreguntaPerfil();

        //actualizamos el layout con los datos de la pregunat
        actualizarlyt();
    }

    public void buscarPreguntaPrincipal(){

        try{
            //si tiene una api superior o igual a 24
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                //obtenemos la pregunta con una expresion lambda
                pregunta = JuegoActivity.alPreguntas.stream().filter(x -> x.getiDPregunta() == IDPregunta).findFirst().get();
            } else {
                for (Pregunta preg: JuegoActivity.alPreguntas){
                    if (preg.getiDPregunta() == IDPregunta){
                        pregunta = preg;
                        break;
                    }
                }
                if (pregunta == null) throw new Exception();
            }
        } catch (Exception e){
            Toast.makeText(this, "No se ha podido cargar la pregunta. Inténtelo de nuevo más tarde", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void buscarPreguntaPerfil(){

        try{
            //si tiene una api superior o igual a 24
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                //obtenemos la pregunta con una expresion lambda
                pregunta = PreguntasPerfilActivity.alPreguntasPerfil.stream().filter(x -> x.getiDPregunta() == IDPreguntaPerfil).findFirst().get();
            } else {
                for (Pregunta preg: PreguntasPerfilActivity.alPreguntasPerfil){
                    if (preg.getiDPregunta() == IDPreguntaPerfil){
                        pregunta = preg;
                        break;
                    }
                }
                if (pregunta == null) throw new Exception();
            }
        } catch (Exception e){
            Toast.makeText(this, "No se ha podido cargar la pregunta. Inténtelo de nuevo más tarde", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void actualizarlyt(){

        TVTitulo.setText(pregunta.getTitulo());
        if (!pregunta.getImagenTitulo().equals("null")){
            TVTitulo.setText(TVTitulo.getText() + "  \nVer más...");

            TVTitulo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View TVTit) {

                    //creamos el dialogo
                    AlertDialog.Builder builder = new AlertDialog.Builder(PreguntasActivity.this);

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
        respuestaButtons = new RespuestaButton[] {btnRespuesta1, btnRespuesta2, btnRespuesta3, btnRespuesta4};

        boolean mostrar;
        //recorremos el ArrayList con las respuestas
        for (int i = 0; i < 4; i++){

             mostrar = false;

            //si la respuesta tiene titulo o imagen
            if (!pregunta.getAlRespuestas().get(i).getTituloRespuesta().equals("null")) {

                respuestaButtons[i].setText(pregunta.getAlRespuestas().get(i).getTituloRespuesta());
                mostrar = true;
            }

            if (!pregunta.getAlRespuestas().get(i).getImagenRespuesta().equals("null")) {
                respuestaButtons[i].setImagen(pregunta.getAlRespuestas().get(i).getImagenRespuesta());

                respuestaButtons[i].setText(respuestaButtons[i].getText() + " \nVer más...");
                mostrar = true;
            }

            if (mostrar) {

                if (pregunta.getRespuestaCorrecta() == i + 1) {
                    respuestaButtons[i].setCorrecta(true);
                    posicionCorrecta = i;
                }

                mostrarRespuesta(respuestaButtons[i], Integer.parseInt(respuestaButtons[i].getTag().toString()));
            }
        }
    }

    public void mostrarRespuesta(final RespuestaButton btn, int tiempo){

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                btn.setVisibility(View.VISIBLE);
            }
        }, tiempo);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pregunta, menu);

        MIReport = menu.findItem(R.id.MIReportarPregunta);
        MIPerfilCreador = menu.findItem(R.id.MIConsultarPerfil);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home: finish();
                break;
            case R.id.MIConsultarPerfil:
                Intent intent = new Intent(context, PerfilActivity.class);
                intent.putExtra("IDUSUARIO", IDUsuario);
                intent.putExtra("IDUSUARIOMOSTRAR",pregunta.getiDUsuario());
                context.startActivity(intent);
                break;
            case R.id.MIReportarPregunta:
                ReportDialog reportDialog = new ReportDialog();
                reportDialog.show(getFragmentManager(), "ReportDialog");
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //si se ha contestado a la pregunta dejamos que vuelva hacia atras
        if (pregunta.getEstado() != -1) super.onBackPressed();
    }

    public static void mostrarResultado(int estado){

        for (RespuestaButton respuestaButton : respuestaButtons){
            respuestaButton.setEnabled(false);
        }

        if (estado != 0) {
            //actualizamos el estado de la pregunta en la base de datos
            actualizarEstPreg();

            //mostramos mensaje de acierto
            TVResultado.setText("CORRECTO");
            TVResultado.setTextColor(context.getResources().getColor(R.color.verde));

        } else {
            //mostramos mensaje de acierto
            TVResultado.setText("INCORRECTO");
            TVResultado.setTextColor(context.getResources().getColor(R.color.rojo));

            //animamos la respuesta correcta
            respuestaButtons[posicionCorrecta].animar();

            //actualizamos el estado de la pregunta para indicar que se ha fallado
            //(no hace falta contactar con la base de datos ya que ya se hizo al clicar en la pregunta)
            if (IDPreguntaPerfil == -1)JuegoActivity.alPreguntas.get(JuegoActivity.alPreguntas.indexOf(pregunta)).setEstado(0);
            else PreguntasPerfilActivity.alPreguntasPerfil.get(PreguntasPerfilActivity.alPreguntasPerfil.indexOf(pregunta)).setEstado(0);

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

        //ponemos el boton de ir hacia atras
        actionBar.setDisplayHomeAsUpEnabled(true);

        //mostramos la opcion del menu de reportar
        MIReport.setVisible(true);

        //si no se entro a la pregunta desde un perfil mostramos la opcion de que consulte el perfil
        //(para que no cree activities hasta el infinito)
        if (IDPreguntaPerfil == -1) MIPerfilCreador.setVisible(true);
    }

    public static void actualizarEstPreg(){
        ActualizarEstadoPregunta actualizarEstadoPregunta = new ActualizarEstadoPregunta(context, (ActualizarEstadoPregunta.IActualizarEstPreg) context, PB);

        //el pregunta.getiDUsuario devuelve el creador de la pregunta
        actualizarEstadoPregunta.execute(IDUsuario, String.valueOf(pregunta.getiDPregunta()), String.valueOf(pregunta.getPuntuacion()));
    }
    @Override
    public void recibirActEstado(boolean resultado, int codError) {

        //si no hubo errores
        if (codError == 0){

            //si el resultado es true es que se actualizaron los datos correctamente
            if (resultado){

                Toast.makeText(this, "Se han actualizado las puntuaciones!", Toast.LENGTH_SHORT).show();

                //indicamos que la pregunta se ha acertado
                //(esto hace que no tengamos que volver a leer de la base de datos porque al hacer el notifyDataSetChanged en el adaptador ya no deja volver a clicar)
                if (IDPreguntaPerfil == -1)JuegoActivity.alPreguntas.get(JuegoActivity.alPreguntas.indexOf(pregunta)).setEstado(1);
                else PreguntasPerfilActivity.alPreguntasPerfil.get(PreguntasPerfilActivity.alPreguntasPerfil.indexOf(pregunta)).setEstado(1);

                //si no es que algo fue mal
            } else {
                Toast.makeText(this, "No se han podido actualizar los resultados. Inténtelo de nuevo más tarde.", Toast.LENGTH_SHORT).show();

                //actualizamos el estado de la pregunta para que deje volver hacia atras
                if (IDPreguntaPerfil == -1)JuegoActivity.alPreguntas.get(JuegoActivity.alPreguntas.indexOf(pregunta)).setEstado(-2);
                else PreguntasPerfilActivity.alPreguntasPerfil.get(PreguntasPerfilActivity.alPreguntasPerfil.indexOf(pregunta)).setEstado(-2);
            }
        } else {
            Toast.makeText(this, "No se han podido actualizar los resultados. Inténtelo de nuevo más tarde.", Toast.LENGTH_SHORT).show();

            //actualizamos el estado de la pregunta para que deje volver hacia atras
            if (IDPreguntaPerfil == -1)JuegoActivity.alPreguntas.get(JuegoActivity.alPreguntas.indexOf(pregunta)).setEstado(-2);
            else PreguntasPerfilActivity.alPreguntasPerfil.get(PreguntasPerfilActivity.alPreguntasPerfil.indexOf(pregunta)).setEstado(-2);
        }
    }

    @Override
    public void recibirConfirmacion(String causa) {
        ReportarPregunta reportarPregunta = new ReportarPregunta(this ,this, PB);
        reportarPregunta.execute(IDUsuario, pregunta.getiDUsuario(), String.valueOf(pregunta.getiDPregunta()), causa);
    }

    @Override
    public void recibirResReport(boolean resultado, int codError) {

        //si no hubo errores
        if (codError == 0){

            //si el resultado es true es que se actualizaron los datos correctamente
            if (resultado){

                Toast.makeText(this, "Se ha emitido el report!", Toast.LENGTH_SHORT).show();

                //ocultamos la opcion de reportar para que no se pueda volver a hacer
                MIReport.setVisible(false);

                //si no es que algo fue mal
            } else {
                Toast.makeText(this, "No se ha podido emitir el error. Sentimos las molestias.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No se ha podido emitir el error. Sentimos las molestias.", Toast.LENGTH_SHORT).show();
        }
    }
}
