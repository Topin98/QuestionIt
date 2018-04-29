package com.dam.ies1.questionit.perfil;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.dam.ies1.questionit.R;
import com.dam.ies1.questionit.entidades.Perfil;
import com.dam.ies1.questionit.juego.JuegoActivity;
import com.dam.ies1.questionit.juego.crearPregunta.CrearPreguntaActivity;
import com.dam.ies1.questionit.juego.crearPregunta.SeleccionarImagenDialog;
import com.dam.ies1.questionit.juego.pvp.PartidaActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.games.Games;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PerfilActivity extends AppCompatActivity implements InsertarImagenPerfil.IInsercionImagenPerfilCorrecta, CargarPerfil.ICargarPerfil{

    final int PERMISSION_READ_EXTERNAL_STORAGE = 1;

    String rutaImagen = null; //ruta de las imagenes que se van a añadir a las preguntas
    Intent data; //datos que se reciben de la imagen seleccionada de la galeria

    MenuItem MICambiarImagen;
    MenuItem MIDesafiar;

    ScrollView SVIP;
    ImageView IVFotoPerfil;
    TextView TVNombre;
    TextView TVPuntTotal;
    TextView TVPregCont;
    TextView TVPregAcert;
    TextView TVTasaAcierto;
    TextView TVPopularidad;
    TextView TVVictorias;
    TextView TVRecord;
    ProgressBar PB;
    ActionBar actionBar;
    String IDUsuario;
    String IDUsuarioMostrar;

    Perfil perfil; //perfil que vamos a mostrar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //definimos la ActionBar y le ponemos el boton de ir a Home
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //ID del usuario que lanzo la activity
        IDUsuario = getIntent().getStringExtra("IDUSUARIO");

        //ID del usuario que se va a mostrar el perfil
        IDUsuarioMostrar = getIntent().getStringExtra("IDUSUARIOMOSTRAR");

        SVIP = findViewById(R.id.SVIP);
        IVFotoPerfil = findViewById(R.id.IVFotoPerfil);
        TVNombre = findViewById(R.id.TVNombrePerfil);
        TVPuntTotal = findViewById(R.id.TVIPPuntuacion);
        TVPregCont = findViewById(R.id.TVIPPregCont);
        TVPregAcert = findViewById(R.id.TVIPPregAcertadas);
        TVTasaAcierto = findViewById(R.id.TVIPTasaAcierto);
        TVPopularidad = findViewById(R.id.TVIPPopularidad);
        TVVictorias = findViewById(R.id.TVIPVictorias);
        TVRecord = findViewById(R.id.TVIPRecordDesafio);
        PB = findViewById(R.id.PBIP);

        this.cargarPerfil();
    }

    public void cargarPerfil(){
        CargarPerfil cargarPerfil = new CargarPerfil(this,this, PB);
        cargarPerfil.execute(IDUsuarioMostrar);
    }

    @Override
    public void recibirPerfil(String resultado, int codError) {
        //si no se produjeron errores
        if (codError == 0) {

            try {
                if (!resultado.equals("null")) {

                    JSONArray json = new JSONArray(resultado);

                    perfil = new Perfil(IDUsuarioMostrar);

                    for (int i = 0; i < json.length(); i++) {

                        JSONObject jsonObject = json.getJSONObject(i);

                        switch (i) {
                            case 0:
                                perfil.setNombre(jsonObject.getString("Nombre"));
                                perfil.setPuntuacionTotal(jsonObject.getInt("PuntuacionTotal"));
                                perfil.setPopularidadTotal(jsonObject.getInt("PopularidadTotal"));
                                perfil.setVictorias(jsonObject.getInt("Victorias"));
                                perfil.setImagenPerfil(jsonObject.getString("ImagenPerfil"));
                                    break;
                            case 1:
                                perfil.setNumPregContestadas(jsonObject.getInt("NumPregContestadas"));
                                    break;
                            case 2:
                                perfil.setNumPregAcertadas(jsonObject.getInt("NumPregAcertadas"));
                                System.out.println(jsonObject.getInt("NumPregAcertadas") + " asdfasdf");
                                    break;
                            case 3:
                                try{
                                    perfil.setRecord(jsonObject.getInt("Record"));
                                } catch (Exception e){
                                    //si peta es que nunca jugo
                                    //(ponemos -1 para luego actualizar el layout)
                                    perfil.setRecord(-1);
                                }
                                    break;
                            case 4:
                                perfil.setNumPartidasJugadas(jsonObject.getInt("NumPartidasJugadas"));
                                    break;
                        }
                    }

                    this.actualizarLyt();
                } else {
                    Toast.makeText(this, "No se ha podido cargar el perfil.", Toast.LENGTH_SHORT).show();
                    finish();
                }

            } catch (Exception e) {
                Toast.makeText(this, "Se ha producido un error cargando el perfil " + e.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            //resultado contiene el mensaje de error
            Toast.makeText(this, resultado, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void actualizarLyt(){

        //region ImagenPerfil
        //obtenemos la ruta de la imagen
        String imagenB64 = perfil.getImagenPerfil();

        //si la ruta de la imagen no es null
        if (!imagenB64.equals("null")){

            byte[] decodedString = Base64.decode(imagenB64, Base64.URL_SAFE);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            //cambiamos el tamaño de la imagen para ajustarla al dialogo
            Bitmap resized = Bitmap.createScaledBitmap(decodedByte, 400, 400, true);

            //asociamos la imagen al ImageView
            IVFotoPerfil.setImageBitmap(resized);
        } else {
            IVFotoPerfil.setImageDrawable(getResources().getDrawable(R.drawable.fotodefecto));
        }
        //endregion

        TVNombre.setText(perfil.getNombre());

        TVPuntTotal.setText(String.valueOf(perfil.getPuntuacionTotal()));

        TVPregCont.setText(String.valueOf(perfil.getNumPregContestadas()));
        TVPregAcert.setText(String.valueOf(perfil.getNumPregAcertadas()));
        if (perfil.getNumPregContestadas() == 0 && perfil.getNumPregAcertadas() == 0)TVTasaAcierto.setText("0%");
        else{
            String cadena;
            try{
                //el double hace que no pete al dividir entre 0 y que siempre nos ponga un . (parte decimal)
                cadena = String.valueOf((double)perfil.getNumPregAcertadas() / perfil.getNumPregContestadas() * 100);

                //quitamos la parte decimal
                cadena = cadena.substring(0, cadena.indexOf("."));
                TVTasaAcierto.setText(cadena + "%");
            } catch (Exception e){

                //por aqui nunca deberia entrar pero por si acaso...
                TVTasaAcierto.setText("0%");
            }
        }


        TVPopularidad.setText(String.valueOf(perfil.getPopularidadTotal()));
        TVVictorias.setText(String.valueOf(perfil.getVictorias()));

        //si alguna vez jugo un desafio
        if (perfil.getRecord() != -1){
            TVRecord.setText(String.valueOf(perfil.getRecord()));
        } else {
            TVRecord.setText("-");
        }

        //si ambos IDs son el mismo es que el usuario esta mirando su perfil
        if (IDUsuario.equals(IDUsuarioMostrar)){

            //actualizamos la clasificacion y logros del google play services
            actualizarGP();
        }
    }

    public void actualizarGP(){

        //obtenemos la cuenta con la que se inicio sesion
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        //region Clasificaciones
        //actualizamos las clasificaciones de la play store
        //(lo normal seria hacerlo al acabar de contestar una pregunta o al acabar una partida pero como la puntuacion no siempre esta cargada si no que se suma
        // 1 en la base de datos es mucho mas comodo asi)
        Games.getLeaderboardsClient(this, account).submitScore(getString(R.string.leaderboard_puntuacin), perfil.getPuntuacionTotal());
        Games.getLeaderboardsClient(this, account).submitScore(getString(R.string.leaderboard_victorias), perfil.getVictorias());
        Games.getLeaderboardsClient(this, account).submitScore(getString(R.string.leaderboard_popularidad), perfil.getPopularidadTotal());
        //endregion

        //region Logros
        //comprobamos si desbloqueo algun logro
        if (perfil.getNumPregAcertadas() >= 1){
            Games.getAchievementsClient(getApplicationContext(), account).unlock(getString(R.string.achievement_responde_preguntas_bronce));

            if (perfil.getNumPregAcertadas() >= 50){
                Games.getAchievementsClient(getApplicationContext(), account).unlock(getString(R.string.achievement_responde_preguntas_plata));

                if (perfil.getNumPregAcertadas() >= 500){
                    Games.getAchievementsClient(getApplicationContext(), account).unlock(getString(R.string.achievement_responde_preguntas_oro));
                }
            }
        }

        if (perfil.getVictorias() >= 1){
            Games.getAchievementsClient(getApplicationContext(), account).unlock(getString(R.string.achievement_genio_del_pvp_bronce));

            if (perfil.getVictorias() >= 50){
                Games.getAchievementsClient(getApplicationContext(), account).unlock(getString(R.string.achievement_genio_del_pvp_plata));

                if (perfil.getVictorias() >= 500){
                    Games.getAchievementsClient(getApplicationContext(), account).unlock(getString(R.string.achievement_genio_del_pvp_oro));
                }
            }
        }

        //endregion
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_perfil, menu);

        MICambiarImagen = menu.findItem(R.id.MICambiarImagen);
        MIDesafiar = menu.findItem(R.id.MIDesafiar);

        //si ambos IDs son el mismo es que el usuario esta mirando su perfil
        if (IDUsuario.equals(IDUsuarioMostrar)){
            //hacemos que no se pueda desafiar a si mismo
            MIDesafiar.setVisible(false);

            //si no es que esta mirando el perfil de otra persona
        } else {
            //hacemos que no pueda cambiar de imagen de perfil
            //(cambiaria la suya ya que mandamos el ID de usuario, pero no tiene mucho sentido
            // dejar cambiar de imagen de perfil al usuario mientras mira el perfil de otra persona)
            MICambiarImagen.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                volverMenuPrincipal();
                break;
            case R.id.MICambiarImagen: cambiarImagen();
                break;
            case R.id.MIPreguntas:
                Intent intentPregunta = new Intent(this, PreguntasPerfilActivity.class);
                intentPregunta.putExtra("IDUSUARIO", IDUsuario);
                intentPregunta.putExtra("IDUSUARIOMOSTRAR",IDUsuarioMostrar);
                startActivity(intentPregunta);
                break;
            case R.id.MIDesafiar:
                Intent intentPartida = new Intent(this, PartidaActivity.class);
                intentPartida.putExtra("IDUSUARIO", IDUsuario);
                intentPartida.putExtra("IDUSUARIO_DESAFIAR", IDUsuarioMostrar);
                startActivityForResult(intentPartida, 2);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        volverMenuPrincipal();
    }

    public void volverMenuPrincipal(){
        Intent intent = new Intent(this, JuegoActivity.class);
        intent.putExtra("IDUSUARIO", IDUsuario);
        startActivity(intent);
    }

    public void cambiarImagen(){
        SeleccionarImagenDialog seleccionarImagenDialog = new SeleccionarImagenDialog();
        seleccionarImagenDialog.show(getFragmentManager(), "5");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //si se cambio la imagen de perfil
        if (requestCode == 5){
            if (resultCode == Activity.RESULT_OK) {

                //guardamos los datos recibidos
                this.data = data;

                //si no tenemos permisos para leer los datos
                if (ContextCompat.checkSelfPermission(PerfilActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    //pedimos permisos
                    PedirPermisos(Manifest.permission.READ_EXTERNAL_STORAGE, PERMISSION_READ_EXTERNAL_STORAGE);

                    //si ya tenemos permisos
                else {
                    //actualizamos la foto
                    actualizarFoto();
                }
            }
            //si se empezo una partida no hacemos nada porque al volver al activity principal (JuegoActivity siempre se va a actualizar el ListView de partidas)
            //(podriamos mirar a ver si se completo el desafio correctamente pero igual hace mas de uno y solo guardaria el resultado del ultimo)
        }
    }

    //metodo para pedir los permisos de READ_EXTERNAL_STORAGE
    public void PedirPermisos(final String permisos, final int codigo) {

        //si no tiene permisos de lectura
        if (ContextCompat.checkSelfPermission(this, permisos) != PackageManager.PERMISSION_GRANTED) {

            //si ya rechazo los permisos una vez
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permisos)) {

                //mostramos snackbar
                Snackbar.make(SVIP, "Se requiere aprobar un permiso", Snackbar.LENGTH_LONG).setAction("APROBAR", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //se realiza la solicitud de permisos otra vez
                        ActivityCompat.requestPermissions(PerfilActivity.this, new String[]{permisos}, codigo);
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
            IVFotoPerfil.setImageBitmap(resized);

            //el catch se produce si la imagen esta almacenada en la carpeta de "Fotos" de google
        } catch (Exception e){
            Toast.makeText(this, "Advertencia! Usar imagenes que no estan guardadas en el dispositivo conllevarán a que la pregunta lleve gran cantidad de tiempo en publicarse", Toast.LENGTH_LONG).show();

            //ponemos la imagen temporalmente en el ImagenView
            IVFotoPerfil.setImageURI(selectedImage);

            //obtenemos la imagen en forma de Bitmap del ImageView
            Bitmap nuevo = ((BitmapDrawable)IVFotoPerfil.getDrawable()).getBitmap();

            //le damos el nuevo tamaño para que sea posible subirla a la base de datos
            Bitmap resized = Bitmap.createScaledBitmap(nuevo, 400, 400, true); //cuidado con las medidas que cambian en funcion del movil

            //la ponemos de nuevo en el ImageView
            IVFotoPerfil.setImageBitmap(resized);
        }

        this.insertarImagen();

    }

    public void insertarImagen(){

        InsertarImagenPerfil insertarImagenPerfil = new InsertarImagenPerfil(this, this, PB);
        insertarImagenPerfil.execute(IDUsuario, decodeImagen(((BitmapDrawable)IVFotoPerfil.getDrawable()).getBitmap()));
    }

    public String decodeImagen(Bitmap bitmap){

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream); //comprimimos la imagen sin perder calidad
        byte[] byteArray = stream.toByteArray();
        String imagen = Base64.encodeToString(byteArray, Base64.URL_SAFE);

        return imagen;
    }

    @Override
    public void recibirResInsercion(boolean resultado, int codError) {

        //si no hubo errores
        if (codError == 0){

            //si el resultado es true es que se inserto correctamente
            if (resultado){

                Toast.makeText(this, "Se ha guardado la nueva imagen!", Toast.LENGTH_SHORT).show();

                //si no es que no se pudo insertar
            } else {
                Toast.makeText(this, "No se ha podido guardar la imagen. Inténtelo de nuevo más tarde.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No se ha podido guardar la imagen. Inténtelo de nuevo más tarde.", Toast.LENGTH_SHORT).show();
        }
    }

    private String obtieneRuta(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
