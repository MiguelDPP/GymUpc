package com.miguecode.gymupc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.miguecode.gymupc.models.Usuario;
import com.miguecode.gymupc.utils.Database;
import com.miguecode.gymupc.utils.FileManager;

public class RegisterActivity extends AppCompatActivity {

    String[] carreras = {"Ing Sistemas", "Ing. Ambiental", "Administracion de empresas", "Economía", "Contaduria", "Tecnología Agropecuaria", "Ing. AgroIndustrial"};
    String[] semestres = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

    EditText txtCedula, txtName, txtEmail, txtPassword;
    Spinner dropCarreras;
    Spinner dropSemestres;
    ArrayAdapter<String> adapterItems;
    ArrayAdapter<String> adapterSemestres;

    CheckBox showPassword;

    Button btnRegister, btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtCedula = findViewById(R.id.txtCedula);
        txtName = findViewById(R.id.txtNombre);
        txtEmail = findViewById(R.id.txtCorreo);
        txtPassword = findViewById(R.id.txtPasswordRegister);

        showPassword = findViewById(R.id.checkBoxShowPass);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener((v)->{
            finish();
        });

        showPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                txtPassword.setInputType(1);
            }else{
                txtPassword.setInputType(129);
            }
        });


        dropCarreras = findViewById(R.id.dropCarreras);
        dropSemestres = findViewById(R.id.dropSemestres);

        adapterSemestres = new ArrayAdapter<>(this, R.layout.list_item, semestres);
        adapterItems = new ArrayAdapter<>(this, R.layout.list_item, carreras);

        dropCarreras.setAdapter(adapterItems);
        dropSemestres.setAdapter(adapterSemestres);

        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener((v)-> {
            if (checkFields()){
                Usuario newUser = new Usuario(
                        this.txtCedula.getText().toString(),
                        this.txtName.getText().toString(),
                        this.txtEmail.getText().toString(),
                        this.dropCarreras.getSelectedItem().toString(),
                        this.dropSemestres.getSelectedItem().toString(),
                        this.txtPassword.getText().toString(),
                        "user"
                );
                Toast.makeText(this, newUser.toString(), Toast.LENGTH_SHORT).show();


                Database.saveInformationDatabase("users",newUser, newUser.getId())
                        .thenAccept(result -> {
                            if (result){
                                Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                                FileManager.saveInformation(this, "user.txt", newUser.getId()+";"+newUser.getPassword());
                                Usuario.setUsuarioLogueado(newUser);
                                Intent intent = new Intent(RegisterActivity.this, MainMenuActivity.class);
                                RegisterActivity.this.startActivity(intent);
                                RegisterActivity.this.finish();

                            }else{
                                Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .exceptionally(ex -> {
                            Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show();
                            return null;
                        });




            }
        });
        //Saber que item está seleccionado
    }

    /**
     * Verifica que los campos no estén vacíos
     * @return true si los campos no están vacíos
     * @return false si los campos están vacíos
     */
    public boolean checkFields(){
        if(txtCedula.getText().toString().isEmpty()){
            txtCedula.setError("Campo obligatorio");
            txtCedula.requestFocus();
            return false;
        }
        if(txtName.getText().toString().isEmpty()){
            txtName.setError("Campo obligatorio");
            return false;
        }
        //Debe ser un correo válido
        if(!txtEmail.getText().toString().contains("@")){
            txtEmail.setError("Debe ser un correo válido");
            return false;
        }

        if(txtEmail.getText().toString().isEmpty()){
            txtEmail.setError("Campo obligatorio");
            return false;
        }
        if(txtPassword.getText().toString().isEmpty()){
            txtPassword.setError("Campo obligatorio");
            return false;
        }
        if (txtPassword.getText().toString().length() < 6){
            txtPassword.setError("La contraseña debe tener al menos 6 caracteres");
            return false;
        }
        //Caracteres especiales para la contraseña
        String specialChars = "(.*[@,#,$,%].*$)";
        if (!txtPassword.getText().toString().matches(specialChars)){
            txtPassword.setError("La contraseña debe tener al menos un caracter especial "+specialChars);
            return false;
        }
        return true;
    }
}