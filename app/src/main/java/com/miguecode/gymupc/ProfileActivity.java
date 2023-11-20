package com.miguecode.gymupc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.miguecode.gymupc.models.Usuario;
import com.miguecode.gymupc.utils.Database;

public class ProfileActivity extends AppCompatActivity {
    String[] carreras = {"Ing Sistemas", "Ing. Ambiental", "Administracion de empresas", "Economía", "Contaduria", "Tecnología Agropecuaria", "Ing. AgroIndustrial"};
    String[] semestres = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    Spinner dropCarreras;
    Spinner dropSemestres;

    CheckBox showPassword;

    EditText txtCedula, txtName, txtEmail, txtPassword;

    Button btnRegister, btnBack;

    ArrayAdapter<String> adapterItems;
    ArrayAdapter<String> adapterSemestres;
    TextView textLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        txtCedula = findViewById(R.id.txtCedula);
        txtName = findViewById(R.id.txtNombre);
        txtEmail = findViewById(R.id.txtCorreo);
        txtPassword = findViewById(R.id.txtPasswordProfile);
        btnRegister = findViewById(R.id.btnRegister);
        btnBack = findViewById(R.id.btnBack);
        textLocation = findViewById(R.id.textLocation);


        showPassword = findViewById(R.id.checkBoxShowPass);

        dropCarreras = findViewById(R.id.dropCarreras);
        dropSemestres = findViewById(R.id.dropSemestres);

        adapterSemestres = new ArrayAdapter<>(this, R.layout.list_item, semestres);
        adapterItems = new ArrayAdapter<>(this, R.layout.list_item, carreras);

        dropCarreras.setAdapter(adapterItems);
        dropSemestres.setAdapter(adapterSemestres);

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

        Database.getInformationDatabase("users/"+ Usuario.getUsuarioLogueado().getId(), Usuario.class)
                .thenAccept((user)->{
                    Usuario.setUsuarioLogueado(user);
                    dropCarreras.setSelection(adapterItems.getPosition(user.getCarrera()));
                    dropSemestres.setSelection(adapterSemestres.getPosition(user.getSemestre()));
                    txtCedula.setText(user.getCedula());
                    txtName.setText(user.getNombre());
                    txtEmail.setText(user.getCorreo());
                    txtPassword.setText(user.getPassword());
                    textLocation.setText(""+user.getLocation().getCiudad()+" - "+user.getLocation().getDepartamento() + ", "+user.getLocation().getPais());

                    btnRegister.setOnClickListener((v)->{
                        if (checkFields()){
                            user.setCedula(txtCedula.getText().toString());
                            user.setNombre(txtName.getText().toString());
                            user.setCorreo(txtEmail.getText().toString());
                            user.setCarrera(dropCarreras.getSelectedItem().toString());
                            user.setSemestre(dropSemestres.getSelectedItem().toString());
                            user.setPassword(txtPassword.getText().toString());
                            UpdateElements();
                            finish();
                        }
                    });
                });
    }

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

    public void UpdateElements(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myref = database.getReference("users");

        myref.child(Usuario.getUsuarioLogueado().getId()).child("cedula").setValue(txtCedula.getText().toString());
        myref.child(Usuario.getUsuarioLogueado().getId()).child("nombre").setValue(txtName.getText().toString());
        myref.child(Usuario.getUsuarioLogueado().getId()).child("correo").setValue(txtEmail.getText().toString());
        myref.child(Usuario.getUsuarioLogueado().getId()).child("carrera").setValue(dropCarreras.getSelectedItem().toString());
        myref.child(Usuario.getUsuarioLogueado().getId()).child("semestre").setValue(dropSemestres.getSelectedItem().toString());
        myref.child(Usuario.getUsuarioLogueado().getId()).child("password").setValue(txtPassword.getText().toString());
    }
}