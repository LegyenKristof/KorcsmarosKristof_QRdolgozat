package com.example.qrdolgozat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private Button buttonScan, buttonKiir;
    private TextView textViewOutput;
    private boolean writePermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                intentIntegrator.initiateScan();
            }
        });

        buttonKiir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(writePermission){
                    if(textViewOutput.getText().toString().trim().isEmpty()){
                        Toast.makeText(MainActivity.this, "Üres szöveget nem lehet fájlbaírni!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Date date = Calendar.getInstance().getTime();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String s = textViewOutput.getText() + ", " + simpleDateFormat.format(date);



                        try {
                            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(MainActivity.this.openFileOutput("scannedCodes.csv", MODE_APPEND));
                            outputStreamWriter.write(s + "\n");
                            outputStreamWriter.close();

                            Toast.makeText(MainActivity.this, "A fájlbaírás sikeres!", Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "Engedélyezze a fájlbaírást!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        textViewOutput.setText(intentResult.getContents());

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            writePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void init(){
        buttonScan = findViewById(R.id.buttonScan);
        buttonKiir = findViewById(R.id.buttonKiir);
        textViewOutput = findViewById(R.id.textViewOutput);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            writePermission = true;
        } else {
            writePermission = false;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }
}