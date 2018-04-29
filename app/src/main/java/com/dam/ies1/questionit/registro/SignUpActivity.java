package com.dam.ies1.questionit.registro;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dam.ies1.questionit.R;
import com.dam.ies1.questionit.juego.JuegoActivity;
import com.dam.ies1.questionit.main.ControlLayout;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.gson.Gson;

public class SignUpActivity extends AppCompatActivity implements ComprobarNombre.IExisteNombre, InsertarUsuario.IInsercionUsuarioCorrecta {

    ProgressBar PB;
    EditText ETEmail;
    EditText ETNombre;
    Button btnRegistrarse;
    GoogleSignInAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Toast.makeText(this, "Parece que es tu primera vez aquí... Registrate!", Toast.LENGTH_SHORT).show();

        Gson gson = new Gson();
        String cuenta = getIntent().getStringExtra("ACCOUNT");
        account = gson.fromJson(cuenta, GoogleSignInAccount.class);

        PB = findViewById(R.id.PBRegistro);
        ETEmail = findViewById(R.id.ETEmail);
        ETNombre = findViewById(R.id.ETNombre);
        btnRegistrarse = findViewById(R.id.btnRegistrarse);

        ETEmail.setText(account.getEmail());

        btnRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarUsuario();
            }
        });
    }

    public void registrarUsuario(){

        if (ETNombre.getText().length() > 0 && ETNombre.getText().length() < 20){
            ComprobarNombre comprobarNombre = new ComprobarNombre(this, this, PB);
            comprobarNombre.execute(ETNombre.getText().toString());
        } else {
            ETNombre.setError("Rellene el campo con un nombre de hasta 20 caracteres");
        }
    }

    @Override
    public void recibirNombre(boolean resultado, int codError) {

        //si no hubo errores
        if (codError == 0){

            //si el resultado es falso es que NO existe el usuario
            if (!resultado){

                InsertarUsuario insertarUsuario = new InsertarUsuario(this, this, PB);
                insertarUsuario.execute(account.getId(), ETNombre.getText().toString(), account.getEmail());

                //si no es que ya existe un usuario en la base de datos con ese nombre
            } else {
                //habilitamos el layout
                ControlLayout.habilitarLyt(this);
                ETNombre.setError("Este nombre de usuario ya está en uso");
            }
        } else {
            Toast.makeText(this, "No se ha podido realizar el registro. Inténtelo de nuevo más tarde.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void recibirResInsercion(boolean resultado, int codError) {

        //si no hubo errores
        if (codError == 0){

            //si el resultado es true es que se inserto correctamente
            if (resultado){

                Toast.makeText(this, "Registro completado!", Toast.LENGTH_SHORT).show();

                //vamos a la pantalla principal del juego pasandole el ID del usuario que inicio sesion
                Intent intent = new Intent(this, JuegoActivity.class);
                intent.putExtra("IDUSUARIO", account.getId());
                startActivityForResult(intent, 0);

                //si no es que no se pudo insertar
            } else {
                Toast.makeText(this, "No se ha podido realizar el registro. Inténtelo de nuevo más tarde.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No se ha podido realizar el registro. Inténtelo de nuevo más tarde.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //se contesto a la pregunta
        if (requestCode == 0){
            finish();

            //se hizo un desafio
        }
    }
}
