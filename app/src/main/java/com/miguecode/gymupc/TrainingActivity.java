package com.miguecode.gymupc;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.miguecode.gymupc.models.Training;
import com.miguecode.gymupc.utils.Database;

public class TrainingActivity extends AppCompatActivity {
    ImageView imgQR;
    TextView txtTime, txtInit;
    Button btnBack, btnFinish;
    Training training;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        training = (Training) getIntent().getSerializableExtra("training");


        imgQR = findViewById(R.id.imgQR);

        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        try {
            Bitmap bitmap = barcodeEncoder.encodeBitmap(training.getCode(), BarcodeFormat.QR_CODE, 1000, 1000);
            imgQR.setImageBitmap(bitmap);
        } catch (Exception e) {
            finish();
        }

        txtTime = findViewById(R.id.txtTime);
        txtInit = findViewById(R.id.txtInit);
        txtInit.setText((training.getDateInit().getHours() < 12 ?training.getDateInit().getHours(): (training.getDateInit().getHours() - 12)) + ":" + training.getDateInit().getMinutes() + ":" + training.getDateInit().getSeconds()+ (training.getDateInit().getHours() > 12 ? " PM" : " AM"));
        //Crear un hilo para actualizar el tiempo

        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    runOnUiThread(() -> {
                        long time = (new java.util.Date().getTime()-training.getDateInit().getTime()) / 1000;
                        txtTime.setText(time/60/60 + "h " + time/60%60 + "m " + time%60 + "s");
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        btnBack = findViewById(R.id.btnBack);
        btnFinish = findViewById(R.id.btnFinish);

        btnBack.setOnClickListener((v)->{
            finish();
        });

        btnFinish.setOnClickListener((v)->{
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Finalizar entrenamiento")
                    .setMessage("¿Está seguro que desea finalizar el entrenamiento?")
                    .setPositiveButton("Finalizar", (dialog1, which) -> {
                        //Eliminar el entrenamiento
                        training.setFinished(true);
                        training.setDateEnd(new java.util.Date());
                        Database.saveInformationDatabase("training", training, training.getId()).thenAccept((result)->{
                            if (result) {
                                Toast.makeText(this, "Entrenamiento finalizado", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(this, "Error al finalizar el entrenamiento", Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("Cancelar", (dialog12, which) -> {
                        dialog12.dismiss();
                    })
                    .create();
            dialog.show();
        });


    }



}