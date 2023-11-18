package com.miguecode.gymupc;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.miguecode.gymupc.models.Schedule;
import com.miguecode.gymupc.models.Usuario;
import com.miguecode.gymupc.utils.Database;
import com.miguecode.gymupc.utils.Dates;

import java.util.ArrayList;
import java.util.Date;

public class DetailScheduleActivity extends AppCompatActivity {
    TextView txtData, txtHour, txtStatus;
    Schedule schedule;
    ArrayList<String> selected;
    Button btnBack, btnCancel;



    CheckBox[] checkBoxes = new CheckBox[6];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_schedule);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            finish();
        }
        defineCheckBoxes();
        txtData = findViewById(R.id.txtFecha);
        txtHour = findViewById(R.id.txtHora);
        txtStatus = findViewById(R.id.txtEstado);
        btnBack = findViewById(R.id.btnBack);
        btnCancel = findViewById(R.id.btnCancel);
        Schedule temp = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            temp = extras.getSerializable("schedule", Schedule.class);
        } else {
            temp = (Schedule) extras.getSerializable("schedule");
        }
        Database.getInformationDatabase("users/"+ Usuario.getUsuarioLogueado().getId()+"/schedules/"+temp.getId(), Schedule.class)
                .thenAccept((schedule)->{
                    this.schedule = schedule;
                    txtData.setText(schedule.getDate());
                    txtHour.setText(schedule.getHour());
                    selected = schedule.getExercises();
                    setInformationCheckBoxes();
                    Date date = Dates.convertTODate(schedule.getDate());
                    Date today = new Date();
                    if (Dates.isMayorQue(today, date) && schedule.isWent()) {
                        txtStatus.setText("Estado: Realizado");
                        //Ocultar boton cancelar
                        btnCancel.setEnabled(false);
                        btnCancel.setAlpha(0f);
                    } else if (Dates.isMayorQue(today, date) && !schedule.isWent()) {
                        txtStatus.setText("Estado: Perdido");
                        //Poner opacidad al boton cancelar
                        btnCancel.setEnabled(false);
                        btnCancel.setAlpha(0f);
                    } else {
                        txtStatus.setText("Estado: Pendiente");
                        btnCancel.setOnClickListener(
                                (v)->{
                                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                    builder.setTitle("Eliminar");
                                    builder.setMessage("¿Estas seguro de eliminar su visita?")
                                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // START THE GAME!
                                                    Database.deleteFromDatabase("users/"+ Usuario.getUsuarioLogueado().getId()+"/schedules/"+schedule.getId())
                                                            .thenAccept(result -> {
                                                                finish();
                                                            })
                                                            .exceptionally((exception) -> {
                                                                Toast.makeText(DetailScheduleActivity.this, "Error al eliminar el horario", Toast.LENGTH_SHORT).show();
                                                                return null;
                                                            });
                                                }
                                            })
                                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // User cancelled the dialog
                                                    //finish();
                                                }
                                            }).show();

                                }
                        );
                    }
                });

        btnBack.setOnClickListener(
                (v)->{
                    finish();
                }
        );


    }

    public void defineCheckBoxes() {
        checkBoxes[0] = findViewById(R.id.checkBoxPecho);
        checkBoxes[1] = findViewById(R.id.checkBoxEspalda);
        checkBoxes[2] = findViewById(R.id.checkBoxBrazo);
        checkBoxes[3] = findViewById(R.id.checkBoxAbdomen);
        checkBoxes[4] = findViewById(R.id.checkBoxHombros);
        checkBoxes[5] = findViewById(R.id.checkBoxPiernas);
    }

    public void setInformationCheckBoxes() {
        for (CheckBox checkBox : checkBoxes) {
            checkBox.setChecked(false);

            //Disable checkbox
            checkBox.setEnabled(false);
        }
        for (String exercise : selected) {
            switch (exercise) {
                case "Pecho":
                    checkBoxes[0].setChecked(true);
                    break;
                case "Espalda":
                    checkBoxes[1].setChecked(true);
                    break;
                case "Brazo":
                    checkBoxes[2].setChecked(true);
                    break;
                case "Abdomen":
                    checkBoxes[3].setChecked(true);
                    break;
                case "Hombros":
                    checkBoxes[4].setChecked(true);
                    break;
                case "Piernas":
                    checkBoxes[5].setChecked(true);
                    break;
            }
        }
    }
}