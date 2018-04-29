package com.dam.ies1.questionit.juego.crearPregunta;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.dam.ies1.questionit.entidades.Pregunta;
import com.dam.ies1.questionit.entidades.Respuesta;
import com.dam.ies1.questionit.R;
import com.dam.ies1.questionit.main.ControlLayout;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.games.Games;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class CrearPreguntaActivity extends AppCompatActivity implements ConfirmarPublicacionDialog.IConfirmarPublicacion, InsertarPregunta.IInsercionPreguntaCorrecta {

    //region Definicion de variables

    final int PERMISSION_READ_EXTERNAL_STORAGE = 1;

    //controles del titulo
    ImageView IVMostrarTitulo;
    CardView CVTitulo;

    EditText ETTitulo;
    Button btnTitulo;
    ImageView IVTitulo;

    //controles de las repuestas
    ImageView IVMostrarRespuestas;
    CardView CVRespuestas;

    //respuesta1
    EditText ETRespuesta1;
    Button btnRespuesta1;
    ImageView IVRespuesta1;
    CheckBox CBRespuesta1;

    //respuesta2
    EditText ETRespuesta2;
    Button btnRespuesta2;
    ImageView IVRespuesta2;
    CheckBox CBRespuesta2;

    //respuesta3
    EditText ETRespuesta3;
    Button btnRespuesta3;
    ImageView IVRespuesta3;
    CheckBox CBRespuesta3;

    //respuesta4
    EditText ETRespuesta4;
    Button btnRespuesta4;
    ImageView IVRespuesta4;
    CheckBox CBRespuesta4;

    //controles de la dificultad
    Spinner SDificultad;

    //controles de los tags
    ImageView IVMostrarTags;
    CardView CVTags;

    //resto de controles
    ScrollView SVCP;
    ProgressBar PB;
    RelativeLayout RLTags;
    Button btnPublicar;
    ActionBar actionBar;

    //resto de variables necesarias
    Handler handler; //se usa para la animacion de ocultar los CardView
    String rutaImagen = null; //ruta de las imagenes que se van a añadir a las preguntas
    Intent data; //datos que se reciben de la imagen seleccionada de la galeria
    ImageView imagenCambiar; //ImageView que se va a actualizar su imagen

    Pregunta pregunta; //pregunta que se va a publicar
    String IDUsuario; //ID del usuario que va a crear la pregunta

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_pregunta);
        getSupportActionBar().setTitle("Nueva Pregunta");

        //definimos la ActionBar y le ponemos el boton de ir a Home
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        IDUsuario = getIntent().getStringExtra("IDUSUARIO");

        //controles titutlo
        IVMostrarTitulo = findViewById(R.id.IVMostrarTitulo);
        CVTitulo = findViewById(R.id.CVCPTitulo);
        ETTitulo = findViewById(R.id.ETCPTitulo);
        btnTitulo = findViewById(R.id.btnAnyadirImagenTitulo);
        IVTitulo = findViewById(R.id.IVCPTitulo);

        //controles de las respuetas
        IVMostrarRespuestas = findViewById(R.id.IVMostrarRespuestas);
        CVRespuestas = findViewById(R.id.CVCPRespuestas);

        //respuesta1
        ETRespuesta1 = findViewById(R.id.ETCPRespuesta1);
        btnRespuesta1 = findViewById(R.id.btnAnyadirImagenR1);
        IVRespuesta1 = findViewById(R.id.IVCPRespuesta1);
        CBRespuesta1 = findViewById(R.id.CBRespuesta1);

        //respuesta2
        ETRespuesta2 = findViewById(R.id.ETCPRespuesta2);
        btnRespuesta2 = findViewById(R.id.btnAnyadirImagenR2);
        IVRespuesta2 = findViewById(R.id.IVCPRespuesta2);
        CBRespuesta2 = findViewById(R.id.CBRespuesta2);

        //respuesta3
        ETRespuesta3 = findViewById(R.id.ETCPRespuesta3);
        btnRespuesta3 = findViewById(R.id.btnAnyadirImagenR3);
        IVRespuesta3 = findViewById(R.id.IVCPRespuesta3);
        CBRespuesta3 = findViewById(R.id.CBRespuesta3);

        //respuesta4
        ETRespuesta4 = findViewById(R.id.ETCPRespuesta4);
        btnRespuesta4 = findViewById(R.id.btnAnyadirImagenR4);
        IVRespuesta4 = findViewById(R.id.IVCPRespuesta4);
        CBRespuesta4 = findViewById(R.id.CBRespuesta4);

        //controles de la dificultad
        SDificultad = findViewById(R.id.SCPDificultad);

        //controles de los tags
        IVMostrarTags = findViewById(R.id.IVMostrarTags);
        CVTags = findViewById(R.id.CVCPTags);

        //resto de controles
        SVCP = findViewById(R.id.SVCP);
        PB = findViewById(R.id.PBCP);
        RLTags = findViewById(R.id.RLCPTags);
        btnPublicar = findViewById(R.id.btnPublicar);
        handler = new Handler();

        IVMostrarTitulo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualizarLyt(IVMostrarTitulo, CVTitulo);
            }
        });

        IVMostrarRespuestas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualizarLyt(IVMostrarRespuestas, CVRespuestas);
            }
        });

        IVMostrarTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualizarLyt(IVMostrarTags, CVTags);
            }
        });

        btnTitulo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleccionarImagen("0", IVTitulo);
            }
        });

        btnRespuesta1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleccionarImagen("1", IVRespuesta1);
            }
        });

        btnRespuesta2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleccionarImagen("2", IVRespuesta2);
            }
        });

        btnRespuesta3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleccionarImagen("3", IVRespuesta3);
            }
        });

        btnRespuesta4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleccionarImagen("4", IVRespuesta4);
            }
        });

        btnPublicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pregunta = new Pregunta(IDUsuario);
                String mError = preguntaValida();

                if (mError.equals("")) {
                    confirmarPublicarPregunta();
                } else {
                    Toast.makeText(CrearPreguntaActivity.this, "Pregunta no válida: " + mError, Toast.LENGTH_SHORT).show();
                }
            }
        });

        CVTitulo.setVisibility(View.GONE);
        CVRespuestas.setVisibility(View.GONE);
        CVTags.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                //resetamos los tags para que no aparezcan más en verde
                this.resetearTags();
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void actualizarLyt(final ImageView IVFlecha, final CardView CV){

        if (!Boolean.parseBoolean(IVFlecha.getTag().toString())){

            //deshabilitamos la interaccion del usuario con el layout
            //(da igual cuantas views pongas en enabled false que se va poder seguir clicando asi que mejor arreglarlo con una linea)
            ControlLayout.deshabilitarLyt(this);

            //actualizamos la posicion del CardView desde donde empezara la animacion
            CV.setY(CV.getY() - IVFlecha.getHeight());

            //ponemos el CardView visible
            CV.setVisibility(View.VISIBLE);

            //ejecutamos la animacion
            CV.animate().translationYBy(IVFlecha.getHeight());

            //le cambiamos el tag
            IVFlecha.setTag(true);

            //ponemos la nueva imagen
            IVFlecha.setImageResource(R.drawable.flecha_abajo);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //volvemos a activar la interaccion del usuario con el layout
                    ControlLayout.habilitarLyt(CrearPreguntaActivity.this);
                }
            }, 400);


        } else {

            //deshabilitamos la interaccion del usuario con el layout
            //(da igual cuantas views pongas en enabled false que se va poder seguir clicando asi que mejor arreglarlo con una linea)
            ControlLayout.deshabilitarLyt(this);

            //ocultamos el teclado
            try {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            } catch (Exception e){
                //excepcion que se produce cuando se spamea el click en el boton de la flecha
            }

            //ejecutamos la animacion
            CV.animate().translationYBy(-IVFlecha.getHeight());

            //actualizamos el tag
            IVFlecha.setTag(false);

            //esperamos 0.4 segundos a que se realice la animacion
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    //establecemos que el CardView no ocupe tamaño en el layout
                    CV.setVisibility(View.GONE);

                    //lo volvemos a poner en la posicion original
                    CV.setY(CV.getY() + IVFlecha.getHeight());

                    //ponemos la nueva imagen
                    IVFlecha.setImageResource(R.drawable.flecha_der);

                    //volvemos a activar la interaccion del usuario con el layout
                    ControlLayout.habilitarLyt(CrearPreguntaActivity.this);
                }
            },400);
        }
    }

    public void seleccionarImagen(String tag, ImageView imageView){
        SeleccionarImagenDialog seleccionarImagenDialog = new SeleccionarImagenDialog();
        seleccionarImagenDialog.show(getFragmentManager(), tag);

        this.imagenCambiar = imageView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            //guardamos los datos recibidos
            this.data = data;

            //si no tenemos permisos para leer los datos
            if (ContextCompat.checkSelfPermission(CrearPreguntaActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                //pedimos permisos
                PedirPermisos(Manifest.permission.READ_EXTERNAL_STORAGE, PERMISSION_READ_EXTERNAL_STORAGE);

            //si ya tenemos permisos
            else {
                //actualizamos la foto
                actualizarFoto();
            }
        }
    }

    //metodo para pedir los permisos de READ_EXTERNAL_STORAGE
    public void PedirPermisos(final String permisos, final int codigo) {

        //si no tiene permisos de lectura
        if (ContextCompat.checkSelfPermission(this, permisos) != PackageManager.PERMISSION_GRANTED) {

            //si ya rechazo los permisos una vez
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permisos)) {

                //mostramos snackbar
                Snackbar.make(SVCP, "Se requiere aprobar un permiso", Snackbar.LENGTH_LONG).setAction("APROBAR", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //se realiza la solicitud de permisos otra vez
                        ActivityCompat.requestPermissions(CrearPreguntaActivity.this, new String[]{permisos}, codigo);
                    }
                }).show();

                //si aun no los rechazo nunca
            } else {

                //si piden los permisos directamente
                ActivityCompat.requestPermissions(this, new String[]{permisos}, codigo);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_READ_EXTERNAL_STORAGE: {

                //si tenemos permisos
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //actualizamos la foto
                    actualizarFoto();
                } else {
                    //si no se dieron los permisos mostramos mensaje
                    Toast.makeText(this, "Permiso de lectura denegado. No se ha podido cargar la imagen", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void actualizarFoto() {
        Uri selectedImage = data.getData();
        rutaImagen = obtieneRuta(selectedImage);
        Bitmap bitmap = BitmapFactory.decodeFile(rutaImagen);

        try{
            Bitmap resized = Bitmap.createScaledBitmap(bitmap, 400, 400, true); //cuidado con las medidas que cambian en funcion del movil
            imagenCambiar.setImageBitmap(resized);

            //el catch se produce si la imagen esta almacenada en la carpeta de "Fotos" de google
        } catch (Exception e){
            Toast.makeText(this, "Advertencia! Usar imagenes que no estan guardadas en el dispositivo conllevarán a que la pregunta lleve gran cantidad de tiempo en publicarse", Toast.LENGTH_LONG).show();

            //ponemos la imagen temporalmente en el ImagenView
            imagenCambiar.setImageURI(selectedImage);

            //obtenemos la imagen en forma de Bitmap del ImageView
            Bitmap nuevo = ((BitmapDrawable)imagenCambiar.getDrawable()).getBitmap();

            //le damos el nuevo tamaño para que sea posible subirla a la base de datos
            Bitmap resized = Bitmap.createScaledBitmap(nuevo, 400, 400, true); //cuidado con las medidas que cambian en funcion del movil

            //la ponemos de nuevo en el ImageView
            imagenCambiar.setImageBitmap(resized);
        }

    }

    private String obtieneRuta(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public String preguntaValida(){

        String mError = "";

        if (!ETTitulo.getText().toString().trim().equals("")) {

            //le ponemos un titulo
            pregunta.setTitulo(ETTitulo.getText().toString().trim());

            //le ponemos la imagen si la tiene
            if (IVTitulo.getDrawable() != null) {
                pregunta.setImagenTitulo(decodeImagen(((BitmapDrawable) IVTitulo.getDrawable()).getBitmap()));
            } else {
                pregunta.setImagenTitulo("");
            }

            //le añadimos las respuestas que no sean nulas
            Respuesta respuesta = crearRespuesta(ETRespuesta1, IVRespuesta1, CBRespuesta1);
            if (respuesta != null) pregunta.getAlRespuestas().add(respuesta);

            respuesta = crearRespuesta(ETRespuesta2, IVRespuesta2, CBRespuesta2);
            if (respuesta != null) pregunta.getAlRespuestas().add(respuesta);

            respuesta = crearRespuesta(ETRespuesta3, IVRespuesta3, CBRespuesta3);
            if (respuesta != null) pregunta.getAlRespuestas().add(respuesta);

            respuesta = crearRespuesta(ETRespuesta4, IVRespuesta4, CBRespuesta4);
            if (respuesta != null) pregunta.getAlRespuestas().add(respuesta);

            if (pregunta.getAlRespuestas().size() < 2) mError += "\nLa pregunta debe al menos contener 2 respuestas.";

            int iCorrectas = 0; //numero para comprobar si se marco solo 1 respuesta como correcta
            int posicion = -1; //posicion de la respuesta correcta (esta inicializada por obligacion)

            //recorremos las respuestas de la pregunta
            for (int i = 0; i < pregunta.getAlRespuestas().size(); i++){

                //si la respuesta esta marcada como correcta
                if (pregunta.getAlRespuestas().get(i).isCorrecta()){

                    //sumamos 1 al numero de respuestas correctas
                    iCorrectas++;

                    //indicamos que la respuesta correcta es la posicion en la que estamos mas 1
                    //en el caso de que haya mas de una respuesta correcta luego no deja insertarla por lo que no ocasiona bug
                    posicion = ++i;
                }
            }

            if (iCorrectas != 1) mError += "\nLa pregunta solo puede tener una respuesta correcta.";
            else pregunta.setRespuestaCorrecta(posicion);

        } else {
            mError += "\nEl título de la pregunta no puede estar vacío.";
        }

        return mError;

    }

    public Respuesta crearRespuesta(EditText editText, ImageView imageView, CheckBox checkBox){

        Respuesta respuesta = new Respuesta();

        if (!editText.getText().toString().trim().equals("") || imageView.getDrawable() != null){

            respuesta.setTituloRespuesta(editText.getText().toString().trim());
            respuesta.setCorrecta(checkBox.isChecked());

            try{
                respuesta.setImagenRespuesta(decodeImagen(((BitmapDrawable)imageView.getDrawable()).getBitmap()));
            } catch (Exception e){
                respuesta.setImagenRespuesta("");
            }

        } else {
            respuesta = null;
        }

        return respuesta;
    }

    public String decodeImagen(Bitmap bitmap){

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream); //comprimimos la imagen sin perder calidad
        byte[] byteArray = stream.toByteArray();
        String imagen = Base64.encodeToString(byteArray, Base64.URL_SAFE);

        return imagen;
    }

    public void confirmarPublicarPregunta(){

        this.crearPregunta();

        ConfirmarPublicacionDialog confirmarPublicacionDialog = new ConfirmarPublicacionDialog();
        confirmarPublicacionDialog.show(getFragmentManager(), "ConfirmarPublicarPregunta");
    }

    public void crearPregunta(){

        //añadimos la dificultad a la pregunta
        pregunta.setDificultad(SDificultad.getSelectedItem().toString());

        //añadimos los tags a la pregunta
        //recorremos el relative layout que contiene los TextView con los tags
        for (int i = 0; i < RLTags.getChildCount(); i++) {

            //casteamos el control
            View view = RLTags.getChildAt(i);

            //si es un TextView que contiene un tag
            if (view instanceof TagTextView){

                //casteamos el control
                TagTextView tagTextView = (TagTextView) view;

                //si esta seleccionado
                if (tagTextView.isSelec()){

                    //añadimos el tag a la pregunta
                    pregunta.getAlTags().add(tagTextView.getText().toString());
                }
            }
        }
    }

    @Override
    public void publicarPregunta() {
        InsertarPregunta insertarPregunta = new InsertarPregunta(this, this, PB);
        insertarPregunta.execute(pregunta);
    }

    @Override
    public void recibirResInsercion(boolean resultado, int codError) {

        //si no hubo errores
        if (codError == 0){

            //si el resultado es true es que se inserto correctamente
            if (resultado){

                Toast.makeText(this, "Se ha publicado la pregunta!", Toast.LENGTH_SHORT).show();

                //resetamos los tags para que no aparezcan más en verde
                this.resetearTags();

                //desbloqueamos el logro de publicar una pregunta
                Games.getAchievementsClient(getApplicationContext(), GoogleSignIn.getLastSignedInAccount(this)).unlock(getString(R.string.achievement_participa_en_la_comunidad));

                finish();

                //si no es que no se pudo insertar
            } else {
                Toast.makeText(this, "No se ha podido publicar la pregunta. Inténtelo de nuevo más tarde.\nRecuerde que caracteres reservados como \' pueden ocasionar problemas.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "No se ha podido publicar la pregunta. Inténtelo de nuevo más tarde.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {

        //reseteamos los TextView con los tags para que al entrar otra vez en la activity no aparezcan verdes
        this.resetearTags();

        //volvemos para atras
        super.onBackPressed();
    }

    public void resetearTags() {

        //recorremos el relative layout que contiene los TextView con los tags
        for (int i = 0; i < RLTags.getChildCount(); i++) {
            View view = RLTags.getChildAt(i);

            if (view instanceof TagTextView){
                //drawable que define el estilo del textview
                LayerDrawable layerDrawable = (LayerDrawable) view.getBackground();

                //parte del drawable donde se define el color de los textview
                final GradientDrawable gradientDrawable = (GradientDrawable) layerDrawable.findDrawableByLayerId(R.id.tagstyle);

                gradientDrawable.setColor(getResources().getColor(R.color.fondoTV));

                //el tag se pone solo a false
            }
        }
    }
}
