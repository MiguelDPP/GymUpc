package com.miguecode.gymupc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import com.miguecode.gymupc.models.Schedule;
import com.miguecode.gymupc.models.Usuario;
import com.miguecode.gymupc.utils.Database;
import com.miguecode.gymupc.utils.Dates;
import com.miguecode.gymupc.utils.FileManager;

import java.util.ArrayList;
import java.util.Date;

public class ChooseScheduleActivity extends AppCompatActivity {
    ArrayAdapter<String> scheduleAdapter;

    Date [] dates = new Date[4];
    CheckBox[] checkBoxes = new CheckBox[6];

    Button[] buttons = new Button[4];

    Button btnSave, btnBack;
    Spinner dropSchedule;

    int option = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_schedule);
        //Obtener bundle
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            finish();
        }
        defineCheckBoxes();
        defineDates();
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBackToMain);

        scheduleAdapter = new ArrayAdapter<>(this, R.layout.list_item, extras.getStringArrayList("horarios"));

        dropSchedule = findViewById(R.id.dropHoras);

        dropSchedule.setAdapter(scheduleAdapter);

        btnSave.setOnClickListener(
                (v)->{
                    ArrayList<String> selected = new ArrayList<>();
                    for (CheckBox checkBox : checkBoxes) {
                        if (checkBox.isChecked()) {
                            selected.add(checkBox.getText().toString());
                        }
                    }
                    if (selected.size() == 0) {
                        //Toast con icono
                        Toast.makeText(this, "Seleccione al menos un ejercicio", Toast.LENGTH_SHORT).show();
                    } else {
                        Schedule schedule = new Schedule(Dates.toDateString(dates[option]), dropSchedule.getSelectedItem().toString(), Usuario.getUsuarioLogueado().getId(), selected, false, Dates.toShortDateString(dates[option]));
                        Database.findOnDataBase("users/"+Usuario.getUsuarioLogueado().getId()+"/schedules", "date", schedule.getDate(), Schedule.class)
                                .thenAccept((schedule1)->{
                                    if (schedule1.size() == 0) {
                                        Database.saveInformationDatabase("users/" + Usuario.getUsuarioLogueado().getId() + "/schedules", schedule, schedule.getId())
                                                .thenAccept(result -> {
                                                    if (result) {
                                                        Toast.makeText(this, "Horario guardado", Toast.LENGTH_SHORT).show();
                                                        Bundle bundle = new Bundle();
                                                        bundle.putSerializable("schedule", schedule);
                                                        Intent intent = new Intent(ChooseScheduleActivity.this, DetailScheduleActivity.class);
                                                        intent.putExtras(bundle);
                                                        ChooseScheduleActivity.this.startActivity(intent);
                                                        finish();
                                                    } else {
                                                        Toast.makeText(this, "Error al guardar el horario", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }else {
                                        Toast.makeText(this, "Ya tienes un horario para esta fecha", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                      
                }
        );

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
    public void defineDates() {
        buttons[0] = findViewById(R.id.btnToday);
        setActive(buttons[0]);
        buttons[1] = findViewById(R.id.btnTomorrow);
        buttons[2] = findViewById(R.id.btnAfterTomorrow);
        buttons[3] = findViewById(R.id.btnAfterAfterTomorrow);
        for(int i = 0; i < buttons.length; i++) {
            int finalI = i;
            buttons[i].setOnClickListener(
                    (v)->{
                        setInactive(buttons[option]);
                        option = finalI;
                        setActive(buttons[option]);
                    }
            );
        }
        //La fecha de hoy
        dates[0] = new Date();
        //La fecha de mañana
        dates[1] = new Date(dates[0].getTime() + 86400000);
        //Definir boton con fecha corta en formato dd/MM
        buttons[1].setText(dates[1].getDate() + " " + Dates.getMes(dates[1].getMonth()));
        //La fecha de pasado mañana
        dates[2] = new Date(dates[1].getTime() + 86400000);
        buttons[2].setText(dates[2].getDate() + " " + Dates.getMes(dates[2].getMonth()));
        //La fecha de pasado pasado mañana
        dates[3] = new Date(dates[2].getTime() + 86400000);
        buttons[3].setText(dates[3].getDate() + " " + Dates.getMes(dates[3].getMonth()));
    }

    public void setActive (@NonNull Button button) {
        button.setBackground(getDrawable(R.drawable.button_secondary));
        button.setTextColor(getColor(R.color.white));
    }

    public void setInactive (@NonNull Button button) {
        button.setBackground(getDrawable(R.drawable.bg_rounded_white));
        button.setTextColor(getColor(R.color.black));
    }
}