package com.dam.ies1.questionit.main;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dam.ies1.questionit.R;
import com.dam.ies1.questionit.registro.SignUpOLogin;
import com.dam.ies1.questionit.registro.SignUpActivity;
import com.dam.ies1.questionit.juego.JuegoActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.SignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, SignUpOLogin.IExisteIDUsuario {

    SignInButton btnSignIn;
    ProgressBar PB;

    private static final int RC_SIGN_IN = 9000;

    GoogleSignInOptions signInOptions;
    GoogleSignInAccount account;
    GoogleSignInClient signInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PB = findViewById(R.id.PBAM);

        btnSignIn = findViewById(R.id.signin);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        //indicamos que queremos obtener el id y la contraseña de la cuenta
        signInOptions = new GoogleSignInOptions.Builder().requestId().requestEmail().build();

        //obtenemos el cliente de google a partir de las opciones que indicamos antes
        signInClient = GoogleSignIn.getClient(this,signInOptions);

        //si ya se habia iniciado sesion anteriormente la autoiniciamos con esa cuenta
        Task<GoogleSignInAccount> task = signInClient.silentSignIn();

        //si el logueo fue correcto
        if (task.isSuccessful()) {
            //obtenemos la cuenta e iniciamos sesion / nos registramos
            account = task.getResult();
            this.comprobarUsuario();
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "No se ha podido conectar con la cuenta", Toast.LENGTH_SHORT).show();
    }

    private void signIn(){
        //si clico es que no habia aun una cuenta establecida
        Intent intent = signInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //se pidio un correo
        if (requestCode == RC_SIGN_IN){
            //obtenemos el resultado y lo comprobamos
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);

            //se pulso en cerrar sesion
        } else if (requestCode == 0) {
            signout();
        }

    }

    private void handleResult(GoogleSignInResult result){
        if (result.isSuccess()){
            account = result.getSignInAccount();

            this.comprobarUsuario();

        } else {
            Toast.makeText(MainActivity.this, "No se ha podido iniciar sesión. Inténtelo de nuevo más tarde", Toast.LENGTH_SHORT).show();
        }
    }

    public void comprobarUsuario(){
        SignUpOLogin signUpOLogin = new SignUpOLogin(this, this, PB);
        signUpOLogin.execute(account.getId());
    }

    @Override
    public void recibirIDUsuario(String resultado, int codError) {

        try{
            //si no se produjeron errores
            if (codError == 0){

                //si no es null es que ya esta registrado
                if (!resultado.equals("null")) {
                    JSONObject json = new JSONObject(resultado);

                    //iniciamos el juego
                    this.iniciarJuego();

                    //si no es que es un usuario nuevo
                } else {

                    //"comprimimos" la cuenta
                    Gson gson = new Gson();
                    String cuenta = gson.toJson(account);

                    //vamos a la pantalla de registro pasandole la cuenta
                    Intent intent = new Intent(this, SignUpActivity.class);
                    intent.putExtra("ACCOUNT", cuenta);
                    startActivityForResult(intent, 0);

                }
            } else {
                Toast.makeText(this, resultado, Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e){
            Toast.makeText(this, "Error: " + resultado, Toast.LENGTH_SHORT).show();
        }
    }

    public void iniciarJuego(){

        if (!account.getId().equals("")) {
            //vamos a la pantalla principal del juego pasandole el ID del usuario que inicio sesion
            Intent intent = new Intent(this, JuegoActivity.class);
            intent.putExtra("IDUSUARIO", account.getId());
            startActivityForResult(intent, 0);
        } else {
            Toast.makeText(this, "No se ha podido iniciar sesión. Intentelo de nuevo más tarde (ID)", Toast.LENGTH_SHORT).show();
        }
    }

    public void signout(){
        signInClient = GoogleSignIn.getClient(this, signInOptions);
        signInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                    }
                });
    }
}