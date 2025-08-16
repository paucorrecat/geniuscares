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
        ((TextView) findViewById(R.id.edtMntCognom)).setText("");
        ((TextView) findViewById(R.id.edtMntCodi)).setText("");
        ((TextView) findViewById(R.id.edtMntCurs)).setText("");
        ((TextView) findViewById(R.id.edtMntGrup)).setText("");
        ((CheckBox) findViewById(R.id.chkMntSiImatge)).setChecked(false);
        ((CheckBox) findViewById(R.id.chkMntNoImatge)).setChecked(false);
        ((CheckBox) findViewById(R.id.chkMntSiAMem)).setChecked(false);
        ((CheckBox) findViewById(R.id.chkMntNoAMem)).setChecked(false);
        ((CheckBox) findViewById(R.id.chkMntSiApres)).setChecked(false);
        ((CheckBox) findViewById(R.id.chkMntNoApres)).setChecked(false);
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
        if (((TextView) findViewById(R.id.edtMntCognom)).getText().length()!=0) {
            Filtre += " and (Cognom like '" + ((TextView) findViewById(R.id.edtMntCognom)).getText() + "%' )";
        }
        if (((TextView) findViewById(R.id.edtMntCodi)).getText().length()!=0) {
            Filtre += " and (Num like '%" + ((TextView) findViewById(R.id.edtMntCodi)).getText() + "%' )";
        }
        if (((TextView) findViewById(R.id.edtMntCurs)).getText().length()!=0) {
            Filtre += " and (Curs like '" + ((TextView) findViewById(R.id.edtMntCurs)).getText() + "%' )";
        }
        if (((TextView) findViewById(R.id.edtMntGrup)).getText().length()!=0) {
            Filtre += " and (Codi like '" + ((TextView) findViewById(R.id.edtMntGrup)).getText() + "' )";
        }
        if (((CheckBox) findViewById(R.id.chkMntSiImatge)).isChecked()) {
            Filtre += " and (TeImatge <>0 )";
        }
        if (((CheckBox) findViewById(R.id.chkMntNoImatge)).isChecked()) {
            Filtre += " and (TeImatge = 0 )";
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
        if (rb==R.id.radCognom ) {
            Ordre = "Cognom,Nom";
        } else if (rb==R.id.radCurs ) {
            Ordre = "Curs,Grup,Num";
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
