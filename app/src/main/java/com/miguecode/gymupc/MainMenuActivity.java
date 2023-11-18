package com.miguecode.gymupc;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.encoder.QRCode;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.miguecode.gymupc.models.Schedule;
import com.miguecode.gymupc.models.Training;
import com.miguecode.gymupc.models.Usuario;
import com.miguecode.gymupc.utils.Database;
import com.miguecode.gymupc.utils.FileManager;

import java.util.Date;
import java.util.UUID;

public class MainMenuActivity extends AppCompatActivity {
    Button btnLogout, btnApartar, btnHistory, btnProfile;
    ImageButton btnScan, btnAdmin;
    TextView txtBienvenido, txtOcurrencyThisMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        btnLogout = findViewById(R.id.btnLogout);
        btnApartar = findViewById(R.id.btnApartar);
        btnProfile = findViewById(R.id.btnProfile);
        txtBienvenido = findViewById(R.id.textViewBienvenida);
        btnHistory = findViewById(R.id.btnHistory);
        txtOcurrencyThisMonth = findViewById(R.id.txtOcurrencyThisMonth);
        btnScan = findViewById(R.id.btnScan);
        btnAdmin = findViewById(R.id.btnAdmin);


        try {
            if(!Usuario.getUsuarioLogueado().getRole().equals("admin")) {
                //Eliminar boton de administrador
                btnAdmin.setVisibility(android.view.View.GONE);
            }
        } catch (Exception e) {
            btnAdmin.setVisibility(android.view.View.GONE);
            e.printStackTrace();
        }

        setNombre();

        btnLogout.setOnClickListener((v)->{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Cerrar sesion");
            builder.setMessage("¿Estas seguro de cerrar la sesion?")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            FileManager.deleteFile(MainMenuActivity.this,"user.txt");
                            Usuario.setUsuarioLogueado(null);
                            Intent intent = new Intent(MainMenuActivity.this, LoginActivity.class);
                            MainMenuActivity.this.startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                            //finish();
                        }
                    }).show();
        });

        btnApartar.setOnClickListener((v)->{
            Database.getInformationDatabaseList("horarios", String.class)
                    .thenAccept((horarios)->{
                        Intent intent = new Intent(MainMenuActivity.this, ChooseScheduleActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putStringArrayList("horarios", horarios);
                        intent.putExtras(bundle);
                        MainMenuActivity.this.startActivity(intent);
                    });
        });

        btnHistory.setOnClickListener((v)->{
            Intent intent = new Intent(MainMenuActivity.this, HistoryActivity.class);
            MainMenuActivity.this.startActivity(intent);
        });

        btnProfile.setOnClickListener((v)->{
            Intent intent = new Intent(MainMenuActivity.this, ProfileActivity.class);
            MainMenuActivity.this.startActivity(intent);
        });

        checkDaysThisMonth();

        btnScan.setOnClickListener((v)->{
            /*
            ScanOptions options = new ScanOptions();
            //Escanear un codigo QR
            options.setPrompt("Escanea el codigo QR");
            options.setOrientationLocked(false);
            options.setBeepEnabled(true);
            options.setBarcodeImageEnabled(true);
            options.setCameraId(0);

            options.setDesiredBarcodeFormats("QR_CODE");

            ScanContract scanContract = new ScanContract();
            Intent scan = scanContract.createIntent(this, options);
            scan.setAction("com.google.zxing.client.android.SCAN");
            //Orientacion vertical
            scan.putExtra("SCAN_ORIENTATION", 1);
            startActivity(scan);



             */

            ScanOptions options = new ScanOptions();
            options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
            options.setPrompt("Escanea el codigo QR");
            options.setCameraId(0);  // Use a specific camera of the device
            options.setBeepEnabled(false);
            options.setBarcodeImageEnabled(true);
            //Poner forma de codigo QR
            options.setOrientationLocked(false);
            barcodeLauncher.launch(options);
        });

        btnAdmin.setOnClickListener((v)->{
            //Buscar si hay una seccion activa
            Database.findOnDataBaseBool("training", "finished", false, Training.class)
                    .thenAccept((training)->{
                        Training entrenamiento;
                        if(training.size() > 0) {
                            entrenamiento = training.get(0);
                            Intent intent = new Intent(MainMenuActivity.this, TrainingActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("training", entrenamiento);
                            intent.putExtras(bundle);
                            MainMenuActivity.this.startActivity(intent);
                        }else {
                            String codigo = UUID.randomUUID().toString();
                            entrenamiento = new Training(Usuario.getUsuarioLogueado().getId(), new Date(), null, false, codigo);
                            Database.saveInformationDatabase("training", entrenamiento, entrenamiento.getId())
                                    .thenAccept((result)->{
                                        if(result) {
                                            Intent intent = new Intent(MainMenuActivity.this, TrainingActivity.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putSerializable("training", entrenamiento);
                                            intent.putExtras(bundle);
                                            MainMenuActivity.this.startActivity(intent);
                                        }else {
                                            Toast.makeText(this, "Error al crear una nueva seccion", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }

                    });
        });
    }

    // Register the launcher and result handler
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                } else {
                    String codigo = result.getContents();
                    Database.findOnDataBaseBool("training", "finished", false, Training.class)
                            .thenAccept((training)->{
                                if(training.size() > 0) {
                                    Training entrenamiento = training.get(0);
                                    if(entrenamiento.getCode().equals(codigo)) {
                                        Database.findOnDataBase("users/"+Usuario.getUsuarioLogueado().getId()+"/schedules", "shortDate", new Date().getDate()+"/"+(new Date().getMonth()+1)+"/"+(new Date().getYear()+1900), Schedule.class)
                                                .thenAccept((schedules)->{
                                                    if(schedules.size() > 0) {
                                                        Schedule schedule = schedules.get(0);
                                                        Toast.makeText(this, "Cita agendada para hoy", Toast.LENGTH_SHORT).show();
                                                    }else {
                                                        Toast.makeText(this, "No tiene una cita agendada para hoy", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }else {
                                        Toast.makeText(this, "Codigo incorrecto", Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    Toast.makeText(this, "No hay ninguna seccion activa", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });

    public void setNombre() {
        String [] nombres = Usuario.getUsuarioLogueado().getNombre().split(" ");
        String nombre = (nombres.length <= 2) ? Usuario.getUsuarioLogueado().getNombre() : nombres[0] + " " + nombres[2];
        txtBienvenido.setText("Bienvenido, " + (nombre));
    }
    public void checkDaysThisMonth() {
        Date date = new Date();
        String filter = (date.getMonth()+1)+"/"+(date.getYear()+1900);

        Database.findOnDataBase("users/"+Usuario.getUsuarioLogueado().getId()+"/schedules", "shortDate",filter, Schedule.class)
                .thenAccept((schedules)->{
                    int count = 0;
                    if(schedules.size() > 0) {
                        for (Schedule schedule : schedules) {
                            if (schedule.isWent()) {
                                count++;
                            }
                        }
                    }

                    txtOcurrencyThisMonth.setText(count + ((count==1)?" dia": " dias")+" este mes");


                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkDaysThisMonth();
        Database.getInformationDatabase("users/"+ Usuario.getUsuarioLogueado().getId(), Usuario.class)
                .thenAccept((user)->{
                    Usuario.setUsuarioLogueado(user);
                    setNombre();
                });

    }
}