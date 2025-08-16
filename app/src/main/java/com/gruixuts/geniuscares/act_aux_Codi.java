package com.gruixuts.geniuscares;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class act_aux_Codi extends ListActivity {

    private ArrayList<String> llistaVisible;
    private ArrayList<String> llistaCodis;
    private ArrayAdapter adaptador;

    private static final int OK_RESULT_CODE = 1;

    String CodiInici;
    String Pare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aux_codi);
        TextView txtPare = (TextView) findViewById(R.id.pare);

        String CodiInici = getIntent().getExtras().get("Codi").toString();
        if (CodiInici == "") {
            Pare = "";
        } else {
            Pare = CodiInici.substring(0, CodiInici.length() - 1);
        }

        txtPare.setText(Pare);

        VeureCodis(Pare);

    }

    @SuppressLint("ResourceType")
    protected void VeureCodis(String Pare) {
        ArrayList<classPersones> Llista;
        GestorDB db = new GestorDB(getApplicationContext());
        String Filtre;
        llistaVisible = new ArrayList();
        llistaCodis = new ArrayList();
        classPersones Pers;
        Integer n;
        db.open();
        Filtre = "Codi like " + Pare + "_";
        Llista = db.selPersones(Filtre, "Codi");
        db.close();

        if (Llista.size() > 0) {
            for (n = 0; n < Llista.size(); n++) {
                llistaVisible.add(Llista.get(n).getCodi() + " " + Llista.get(n).getId());
                llistaCodis.add(Llista.get(n).getCodi());
            }
            adaptador = new ArrayAdapter(this, R.id.llistaCodis, llistaVisible);
            setListAdapter(adaptador);

        }
    }
}



