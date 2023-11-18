package com.miguecode.gymupc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miguecode.gymupc.models.Usuario;
import com.miguecode.gymupc.utils.Database;
import com.miguecode.gymupc.utils.FileManager;

public class LoginActivity extends AppCompatActivity {
    Button btnGoToRegister, btnLogin;
    EditText txtCedula, txtPassword;
    CheckBox showPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnGoToRegister = findViewById(R.id.btnGoToRegister);
        btnLogin = findViewById(R.id.btnLogin);
        txtCedula = findViewById(R.id.txtCedula);
        txtPassword = findViewById(R.id.txtPassword);

        showPassword = findViewById(R.id.checkBoxShowPass);

        showPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                txtPassword.setInputType(1);
            }else{
                txtPassword.setInputType(129);
            }
        });

        btnLogin.setOnClickListener((v)->{
            if (checkFields()) {
                checkCredentials();
            }
        });

        btnGoToRegister.setOnClickListener((v)->{
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            LoginActivity.this.startActivity(intent);
        });
    }

    public void checkCredentials() {
        //FirebaseDatabase database = FirebaseDatabase.getInstance();
        //DatabaseReference myref = database.getReference("users");
        //Buscar una conincidencia en la base de datos con numero de cedula y contraseña
        Database.findOnDataBase("users", "cedula", txtCedula.getText().toString(), Usuario.class)
                .thenAccept(u -> {
                    if (u.size() > 0) {
                        Usuario user = u.get(0);
                        if (user.getPassword().equals(txtPassword.getText().toString())) {
                            Usuario.setUsuarioLogueado(user);
                            Toast.makeText(this, "Bienvenido " + user.getNombre(), Toast.LENGTH_SHORT).show();
                            FileManager.saveInformation(this, "user.txt", user.getId() + ";" + user.getPassword());
                            //Usuario.setUsuarioLogueado(user);
                            Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
                            LoginActivity.this.startActivity(intent);
                            LoginActivity.this.finish();
                        } else {
                            txtPassword.setError("Contraseña incorrecta");
                            Toast.makeText(this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                    }
                })
                .exceptionally(ex -> {
                    Toast.makeText(this, "Error al iniciar sesion", Toast.LENGTH_SHORT).show();
                    return null;
                });
    }

    public boolean checkFields() {
        if (txtCedula.getText().toString().isEmpty()) {
            txtCedula.setError("La cedula es requerida");
            return false;
        }
        if (txtPassword.getText().toString().isEmpty()) {
            txtPassword.setError("La contraseña es requerida");
            return false;
        }
        return true;
    }
}