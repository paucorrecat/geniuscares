package com.gruixuts.geniuscares;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

public class act_examen_sel extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examen_sel);
    }

    public void GoToFerExamen(View view) {
        Pregunta (classProves.TIP_EXAMEN);
    }

    public void GoToFerRevisio(View view) { Pregunta (classProves.TIP_REVISIO);    }

    public void GoToSeguir(View view) {
        Pregunta ("Seguir");
    }

    private void Pregunta(String Tipus) {
        TextView txtUlt = findViewById(R.id.edtNumUlt);
        CheckBox chkAnt = findViewById(R.id.chkAntigues);
        Intent myIntent = new Intent(act_examen_sel.this, act_pregunta.class);
        String Filtre;
        String Ordre = "";
        Filtre ="(NextTipus <>'a' and NextTipus <>'t' and  NextTipus <>'' ) ";
        if (((TextView) findViewById(R.id.edtExmSiCodi)).getText().length()!=0) {
            Filtre += " and (Codi like '" + ((TextView) findViewById(R.id.edtExmSiCodi)).getText() + "' )";
        }
        if (((TextView) findViewById(R.id.edtExmNoCodi)).getText().length()!=0) {
            Filtre += " and not(Codi like '" + ((TextView) findViewById(R.id.edtExmNoCodi)).getText() + "' )";
        }
        if (((TextView) findViewById(R.id.edtExmSiGrup)).getText().length()!=0) {
            Filtre += " and (Grup like '" + ((TextView) findViewById(R.id.edtExmSiGrup)).getText() + "' )";
        }
        if (((TextView) findViewById(R.id.edtExmNoCodi)).getText().length()!=0) {
            Filtre += " and not(Grup like '" + ((TextView) findViewById(R.id.edtExmNoGrup)).getText() + "' )";
        }

        if (chkAnt.isChecked()) {
            Ordre = "Ant";
        }
        myIntent.putExtra("Ordre", Ordre); //Optional parameters
        myIntent.putExtra("Filtre", Filtre); //Optional parameters
        myIntent.putExtra("Top", "0"+txtUlt.getText()); //Optional parameters
        myIntent.putExtra("Tipus", Tipus); //Optional parameters
        startActivity(myIntent);
    }

}
