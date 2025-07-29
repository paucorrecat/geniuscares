package com.gruixuts.geniuscares;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class act_repas_sel extends AppCompatActivity {

    GestorDB db;
    SimpleDateFormat frmtData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repas_sel);

        db = new GestorDB(getApplicationContext());
        TextView txtr1h = findViewById(R.id.txtRep1h);
        TextView txtr1d = findViewById(R.id.txtRep1d);
        TextView txtr1s = findViewById(R.id.txtRep1s);
        TextView txtr1m = findViewById(R.id.txtRep1m);
        TextView txtr6m = findViewById(R.id.txtRep6m);
        db.open();
        txtr1h.setText(db.QuantsRep("1h").toString());
        txtr1d.setText(db.QuantsRep("1d").toString());
        txtr1s.setText(db.QuantsRep("1s").toString());
        txtr1m.setText(db.QuantsRep("1m").toString());
        txtr6m.setText(db.QuantsRep("6m").toString());
        db.close();

    }

    public void GoToExamenRepas(View view){
        String Repassos;
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("Europe/Madrid"));

        CheckBox chk1h = findViewById(R.id.chkRep1h);
        CheckBox chk1d = findViewById(R.id.chkRep1d);
        CheckBox chk1s = findViewById(R.id.chkRep1s);
        CheckBox chk1m = findViewById(R.id.chkRep1m);
        CheckBox chk6m = findViewById(R.id.chkRep6m);
        String Filtre ="";
        if (chk1h.isChecked()) Filtre += "or NextTipus='1h' ";
        if (chk1d.isChecked()) Filtre += "or NextTipus='1d' ";
        if (chk1s.isChecked()) Filtre += "or NextTipus='1s' ";
        if (chk1m.isChecked()) Filtre += "or NextTipus='1m' ";
        if (chk6m.isChecked()) Filtre += "or NextTipus='6m' ";
        if (Filtre.length() > 0) {
            Filtre =  Filtre.substring(3);
        } else {
            Filtre =  "-1";
        }
        Filtre = "((" + Filtre + ") and ( NextData < '" +  frmtData.format(cal.getTime()) + "'))";
            Intent myIntent = new Intent(act_repas_sel.this, act_pregunta.class);
            myIntent.putExtra("Filtre", Filtre); //Optional parameters
            myIntent.putExtra("Tipus", classProves.TIP_REPAS); //Optional parameters
            myIntent.putExtra("Top", "0"); //Optional parameters
            myIntent.putExtra("Ordre", ""); //Optional parameters
            startActivity(myIntent);
    }

}
