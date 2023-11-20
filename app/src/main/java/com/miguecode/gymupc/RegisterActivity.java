package com.miguecode.gymupc;

import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationRequest;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.miguecode.gymupc.models.Location;
import com.miguecode.gymupc.models.Usuario;
import com.miguecode.gymupc.utils.Database;
import com.miguecode.gymupc.utils.FileManager;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

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
    Location location;

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

        btnBack.setOnClickListener((v) -> {
            finish();
        });

        showPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                txtPassword.setInputType(1);
            } else {
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

        btnRegister.setOnClickListener((v) -> {
            if (checkFields()) {
                Usuario newUser = new Usuario(
                        this.txtCedula.getText().toString(),
                        this.txtName.getText().toString(),
                        this.txtEmail.getText().toString(),
                        this.dropCarreras.getSelectedItem().toString(),
                        this.dropSemestres.getSelectedItem().toString(),
                        this.txtPassword.getText().toString(),
                        "user",
                        location
                );
                Toast.makeText(this, newUser.toString(), Toast.LENGTH_SHORT).show();


                Database.saveInformationDatabase("users", newUser, newUser.getId())
                        .thenAccept(result -> {
                            if (result) {
                                Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                                FileManager.saveInformation(this, "user.txt", newUser.getId() + ";" + newUser.getPassword());
                                Usuario.setUsuarioLogueado(newUser);
                                Intent intent = new Intent(RegisterActivity.this, MainMenuActivity.class);
                                RegisterActivity.this.startActivity(intent);
                                RegisterActivity.this.finish();

                            } else {
                                Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .exceptionally(ex -> {
                            Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show();
                            return null;
                        });


            }
        });

        obtenerCoordenadas();
        //Saber que item está seleccionado
    }

    public void obtenerCoordenadas() {
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

        } else if (checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
        } else {
            getCoordenadas();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCoordenadas();
            } else {
                Toast.makeText(this, "Se requieren permisos de ubicación", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void getCoordenadas() {
        try {
            //obtener coordenadas
            FusedLocationProviderClient fusedLocationProviderClient = new FusedLocationProviderClient(this);

            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null) {
                        obtenerCiudadDesdeCoordenadas(location.getLatitude(), location.getLongitude());
                    } else {
                        Toast.makeText(this, "Error al obtener coordenadas", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            } else {
                Toast.makeText(this, "Error al obtener coordenadas", Toast.LENGTH_SHORT).show();
                finish();
            }



        } catch (Exception e) {
            Toast.makeText(this, "Error al obtener coordenadas", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void obtenerCiudadDesdeCoordenadas(double latitud, double longitud) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> direcciones = geocoder.getFromLocation(latitud, longitud, 1);

            if (direcciones != null && !direcciones.isEmpty()) {
                String ciudad = direcciones.get(0).getLocality();
                //Departamento
                String departamento = direcciones.get(0).getAdminArea();
                //Pais
                String pais = direcciones.get(0).getCountryName();
                location = new Location(latitud, longitud, ciudad, departamento, pais);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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