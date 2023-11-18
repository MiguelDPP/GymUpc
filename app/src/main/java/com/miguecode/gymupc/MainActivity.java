package com.miguecode.gymupc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.miguecode.gymupc.models.Schedule;
import com.miguecode.gymupc.models.Usuario;
import com.miguecode.gymupc.utils.Database;
import com.miguecode.gymupc.utils.FileManager;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Esperar 5 segundos y luego abrir RegisterActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(FileManager.fileExists("user.txt", fileList())){
                    String[] data = FileManager.getInformation(MainActivity.this, "user.txt").split(";");
                    if(data.length > 0) {
                        String reference = "users/"+data[0];
                        Database.getInformationDatabase(reference, Usuario.class)
                                .thenAccept(u -> {
                                    if (u != null && u.getPassword().equals(data[1])){
                                        Usuario.setUsuarioLogueado(u);
                                        Intent intent = new Intent(MainActivity.this, MainMenuActivity.class);
                                        MainActivity.this.startActivity(intent);
                                        MainActivity.this.finish();
                                    }else {
                                        goToLoginActivity();
                                    }
                                })
                                .exceptionally(ex -> {
                                    Toast.makeText(MainActivity.this, "Error al iniciar sesion", Toast.LENGTH_SHORT).show();
                                    goToLoginActivity();
                                    return null;
                                });
                    }
                }else {
                    goToLoginActivity();
                }
            }
        }, 2000);

    }

    public void goToLoginActivity() {
        if(FileManager.fileExists("user.txt", fileList())) {
            FileManager.deleteFile(MainActivity.this, "user.txt");
        }
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        MainActivity.this.startActivity(intent);
        MainActivity.this.finish();
    }



    @Override
    protected void onResume() {
        super.onResume();

    }
}