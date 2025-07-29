package com.gruixuts.geniuscares;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goToExamen(View view) {
        Intent myIntent = new Intent(MainActivity.this, act_examen_sel.class);
        MainActivity.this.startActivity(myIntent);
    }

    public void goToRepas(View view) {
        Intent myIntent = new Intent(MainActivity.this, act_repas_sel.class);
        MainActivity.this.startActivity(myIntent);
    }

    public void goToMemoritzar(View view) {
        Intent myIntent = new Intent(MainActivity.this, act_memoritzar_sel.class);
        MainActivity.this.startActivity(myIntent);
    }

    public void goToManteniment(View view) {
        Intent myIntent = new Intent(MainActivity.this, act_manteniment_sel.class);
        MainActivity.this.startActivity(myIntent);
    }

    public void goToImportExport(View view) {
        Intent myIntent = new Intent(MainActivity.this, act_import_export.class);
        MainActivity.this.startActivity(myIntent);
    }

    public void goToStats(View view) {
        Intent myIntent = new Intent(MainActivity.this, act_estadistica.class);
        MainActivity.this.startActivity(myIntent);
    }


};


