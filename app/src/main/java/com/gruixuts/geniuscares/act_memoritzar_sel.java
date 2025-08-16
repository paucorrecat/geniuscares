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
        ((TextView) findViewById(R.id.edtMmrCognom)).setText("");
        ((TextView) findViewById(R.id.edtMmrCurs)).setText("");
    }


    public void MmrBusca(View view) {
        String Filtre = "";
        if (((TextView) findViewById(R.id.edtMmrId)).getText().length()!=0) {
            Filtre += " and (Id = " + ((TextView) findViewById(R.id.edtMmrId)).getText() + " )";
        }
        if (((TextView) findViewById(R.id.edtMmrNom)).getText().length()!=0) {
            Filtre += " and (Nom like '" + ((TextView) findViewById(R.id.edtMmrNom)).getText() + "%' )";
        }
        if (((TextView) findViewById(R.id.edtMmrCognom)).getText().length()!=0) {
            Filtre += " and (Cognom like '" + ((TextView) findViewById(R.id.edtMmrCognom)).getText() + "%' )";
        }
        if (((TextView) findViewById(R.id.edtMmrCurs)).getText().length()!=0) {
            Filtre += " and (Curs like '" + ((TextView) findViewById(R.id.edtMmrCurs)).getText() + "%' )";
        }
        if (((TextView) findViewById(R.id.edtMmrGrup)).getText().length()!=0) {
            Filtre += " and not(Grup like '" + ((TextView) findViewById(R.id.edtMmrGrup)).getText() + "%' )";
        }
        Filtre += " and (AMemoritzar <> 0  )  and (TeImatge <> 0  )";
        Filtre += " and (NextTipus = 'a')";

        if (Filtre.length()>4) Filtre=Filtre.substring(4);
        objLlistaTrobats.NouSQLtxt(Filtre,"",getApplicationContext());

        Intent myIntent = new Intent(act_memoritzar_sel.this, act_memoritzar.class);
        myIntent.putExtra("Filtre", Filtre);
        startActivity(myIntent);

    }

}
