package com.gruixuts.geniuscares;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class act_memoritzar_sel extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memoritzar_sel);
        ((TextView) findViewById(R.id.edtMmrId)).setText("");
        ((TextView) findViewById(R.id.edtMmrNom)).setText("");
        ((TextView) findViewById(R.id.edtMmrCognom1)).setText("");
        ((TextView) findViewById(R.id.edtMmrCognom2)).setText("");
        ((TextView) findViewById(R.id.edtMmrCurs)).setText("");
        ((TextView) findViewById(R.id.edtMmrSiCodi)).setText("");
        ((TextView) findViewById(R.id.edtMmrNoCodi)).setText("");
        ((TextView) findViewById(R.id.edtMmrSiGrup)).setText("");
        ((TextView) findViewById(R.id.edtMmrNoGrup)).setText("");
    }


    public void MmrBusca(View view) {
        String Filtre = "";
        if (((TextView) findViewById(R.id.edtMmrId)).getText().length()!=0) {
            Filtre += " and (Id = " + ((TextView) findViewById(R.id.edtMmrId)).getText() + " )";
        }
        if (((TextView) findViewById(R.id.edtMmrNom)).getText().length()!=0) {
            Filtre += " and (Nom like '" + ((TextView) findViewById(R.id.edtMmrNom)).getText() + "' )";
        }
        if (((TextView) findViewById(R.id.edtMmrCognom1)).getText().length()!=0) {
            Filtre += " and (Cognom1 like '" + ((TextView) findViewById(R.id.edtMmrCognom1)).getText() + "' )";
        }
        if (((TextView) findViewById(R.id.edtMmrCognom2)).getText().length()!=0) {
            Filtre += " and (Cognom2 like '" + ((TextView) findViewById(R.id.edtMmrCognom2)).getText() + "' )";
        }
        if (((TextView) findViewById(R.id.edtMmrCurs)).getText().length()!=0) {
            Filtre += " and (Curs like '" + ((TextView) findViewById(R.id.edtMmrCurs)).getText() + "' )";
        }
        if (((TextView) findViewById(R.id.edtMmrSiCodi)).getText().length()!=0) {
            Filtre += " and (Codi like '" + ((TextView) findViewById(R.id.edtMmrSiCodi)).getText() + "' )";
        }
        if (((TextView) findViewById(R.id.edtMmrNoCodi)).getText().length()!=0) {
            Filtre += " and not(Codi like '" + ((TextView) findViewById(R.id.edtMmrNoCodi)).getText() + "' )";
        }
        if (((TextView) findViewById(R.id.edtMmrSiGrup)).getText().length()!=0) {
            Filtre += " and (Grup like '" + ((TextView) findViewById(R.id.edtMmrSiGrup)).getText() + "' )";
        }
        if (((TextView) findViewById(R.id.edtMmrNoGrup)).getText().length()!=0) {
            Filtre += " and not(Grup like '" + ((TextView) findViewById(R.id.edtMmrNoGrup)).getText() + "' )";
        }
        Filtre += " and (AMemoritzar <> 0  )";
        Filtre += " and (NextTipus = 'a')";

        if (Filtre.length()>4) Filtre=Filtre.substring(4);
        objLlistaTrobats.NouSQLtxt(Filtre,"",getApplicationContext());

        Intent myIntent = new Intent(act_memoritzar_sel.this, act_memoritzar.class);
        myIntent.putExtra("Filtre", Filtre);
        startActivity(myIntent);

    }

}
