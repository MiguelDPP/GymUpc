package com.miguecode.gymupc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.miguecode.gymupc.adapters.ScheduleAdapter;
import com.miguecode.gymupc.models.Schedule;
import com.miguecode.gymupc.models.Usuario;
import com.miguecode.gymupc.utils.Database;
import com.miguecode.gymupc.utils.Dates;

import java.util.ArrayList;
import java.util.Date;

public class HistoryActivity extends AppCompatActivity {
    RecyclerView viewSchedules;
    ArrayList<Schedule> schedules;
    Button btnBack;
    EditText txtSearchHistory;
    ScheduleAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        viewSchedules = findViewById(R.id.recyclerViewSchedules);
        txtSearchHistory = findViewById(R.id.txtSearchHistory);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener((v)->{
            finish();
        });


        //Evento cuando cambie el texto
        txtSearchHistory.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(android.text.Editable s) {
                String text = txtSearchHistory.getText().toString();
                adapter.filtrado(text);
            }
        });

        getData();


    }

    public void getData() {
        Database.getInformationDatabaseList("users/"+ Usuario.getUsuarioLogueado().getId()+"/schedules", Schedule.class)
                .thenAccept((schedules) -> {
                    viewSchedules.removeAllViews();
                    this.schedules = schedules;
                    //Filtrar por fecha
                    this.schedules.sort((o1, o2) -> {
                        Date date1 = Dates.convertTODate(o1.getDate());
                        Date date2 = Dates.convertTODate(o2.getDate());
                        return date2.compareTo(date1);
                    });
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                    viewSchedules.setLayoutManager(linearLayoutManager);
                    adapter = new ScheduleAdapter(this.schedules);
                    viewSchedules.setAdapter(adapter);
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }
}