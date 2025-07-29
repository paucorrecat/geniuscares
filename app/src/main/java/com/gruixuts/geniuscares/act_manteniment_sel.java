package com.gruixuts.geniuscares;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;

public class act_manteniment_sel extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manteniment_sel);
        ((TextView) findViewById(R.id.edtMntId)).setText("");
        ((TextView) findViewById(R.id.edtMntNom)).setText("");
        ((TextView) findViewById(R.id.edtMntCognom1)).setText("");
        ((TextView) findViewById(R.id.edtMntCognom2)).setText("");
        ((TextView) findViewById(R.id.edtMntCurs)).setText("");
        ((TextView) findViewById(R.id.edtMntSiCodi)).setText("");
        ((TextView) findViewById(R.id.edtMntNoCodi)).setText("");
        ((TextView) findViewById(R.id.edtMntSiGrup)).setText("");
        ((TextView) findViewById(R.id.edtMntNoGrup)).setText("");
        ((RadioGroup) findViewById(R.id.grpOrdre)).clearCheck();
    }


    public void MntBusca(View view) {
        String Filtre = "";
        if (((TextView) findViewById(R.id.edtMntId)).getText().length()!=0) {
            Filtre += " and (Id = " + ((TextView) findViewById(R.id.edtMntId)).getText() + " )";
        }
        if (((TextView) findViewById(R.id.edtMntNom)).getText().length()!=0) {
            Filtre += " and (Nom like '" + ((TextView) findViewById(R.id.edtMntNom)).getText() + "%' )";
        }
        if (((TextView) findViewById(R.id.edtMntCognom1)).getText().length()!=0) {
            Filtre += " and (Cognom1 like '" + ((TextView) findViewById(R.id.edtMntCognom1)).getText() + "%' )";
        }
        if (((TextView) findViewById(R.id.edtMntCognom2)).getText().length()!=0) {
            Filtre += " and (Cognom2 like '" + ((TextView) findViewById(R.id.edtMntCognom2)).getText() + "%' )";
        }
        if (((TextView) findViewById(R.id.edtMntCurs)).getText().length()!=0) {
            Filtre += " and (Curs like '" + ((TextView) findViewById(R.id.edtMntCurs)).getText() + "%' )";
        }
        if (((TextView) findViewById(R.id.edtMntSiCodi)).getText().length()!=0) {
            Filtre += " and (Codi like '" + ((TextView) findViewById(R.id.edtMntSiCodi)).getText() + "' )";
        }
        if (((TextView) findViewById(R.id.edtMntNoCodi)).getText().length()!=0) {
            Filtre += " and not(Codi like '" + ((TextView) findViewById(R.id.edtMntNoCodi)).getText() + "' )";
        }
        if (((TextView) findViewById(R.id.edtMntSiGrup)).getText().length()!=0) {
            Filtre += " and (Grup like '" + ((TextView) findViewById(R.id.edtMntSiGrup)).getText() + "' )";
        }
        if (((TextView) findViewById(R.id.edtMntNoGrup)).getText().length()!=0) {
            Filtre += " and not(Grup like '" + ((TextView) findViewById(R.id.edtMntNoGrup)).getText() + "' )";
        }
        if (((CheckBox) findViewById(R.id.chkMntSiAMem)).isChecked()) {
            Filtre += " and (AMemoritzar <>0 )";
        }
        if (((CheckBox) findViewById(R.id.chkMntNoAMem)).isChecked()) {
            Filtre += " and (AMemoritzar = 0 )";
        }
        if (((CheckBox) findViewById(R.id.chkMntSiApres)).isChecked()) {
            Filtre += " and (NextTipus <> 'a')  and (NextTipus <> 't') and (NextTipus <> '')";
        }
        if (((CheckBox) findViewById(R.id.chkMntNoApres)).isChecked()) {
            Filtre += " and ((NextTipus = 'a') or (NextTipus = 't') or (NextTipus = ''))";
        }

        int rb = ((RadioGroup) findViewById(R.id.grpOrdre)).getCheckedRadioButtonId();
        String Ordre;
        if (rb==R.id.radNom ) {
            Ordre = "Nom";
        } else if (rb==R.id.radCognom ) {
            Ordre = "Cognom1";
        } else if (rb==R.id.radCod ) {
            Ordre = "Codi";
        } else {
            Ordre = "";
        }

        if (Filtre.length()>4) {
            Filtre=Filtre.substring(4);
        } else {
            Filtre = "";
        }
        objLlistaTrobats.NouSQLtxt(Filtre,Ordre,getApplicationContext());

        Intent myIntent = new Intent(act_manteniment_sel.this, act_manteniment_llista.class);
        startActivity(myIntent);
    }
    public void MntCrea(View view) {
        Intent myIntent = new Intent(act_manteniment_sel.this, act_manteniment_modificar.class);
        myIntent.putExtra(act_manteniment_modificar.ARG_ITEM_ID, "");
        startActivity(myIntent);

    }
}
