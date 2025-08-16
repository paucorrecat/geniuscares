package com.gruixuts.geniuscares;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class act_pregunta_val extends AppCompatActivity {

    Integer IdPers;
    private static final int OK_RESULT_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregunta_val);
        TextView txtResp = findViewById(R.id.txtValResposta);
        TextView txtDicc = findViewById(R.id.txtValCorrecta);
        TextView txtPreg = findViewById(R.id.txtValPregunta);
        TextView txtTipus = findViewById(R.id.txtValProva);
        TextView txtData = findViewById(R.id.txtValNextData);
        IdPers = getIntent().getIntExtra("IdPers",0);
        String Resposta = getIntent().getStringExtra("Resposta");
        String Persones = getIntent().getStringExtra("Correcta");
        String Pregunta = getIntent().getStringExtra("Pregunta");
        String NextTip = getIntent().getStringExtra("Prova");
        String NextDat = getIntent().getStringExtra("Next");

        txtResp.setText(Resposta);
        txtDicc.setText(Persones);
        txtPreg.setText(Pregunta);
        txtTipus.setText(NextTip);
        txtData.setText(NextDat);


    }

    public void rstPer(View view) {returnParams(classResultats.VAL_PERFECTE);}
    public void rstRev(View view) {returnParams(classResultats.VAL_REVISAR);}
    public void rstApr(View view) {returnParams(classResultats.VAL_APRES);}
    public void rstObl(View view) {returnParams(classResultats.VAL_OBLIDAT);}

    public void rstMod(View view) {
        Context context = view.getContext();
        Intent intent = new Intent(context, act_manteniment_modificar.class);
        intent.putExtra(act_manteniment_modificar.ARG_ITEM_ID, IdPers.toString());

        context.startActivity(intent);

    }


    protected void returnParams(String Param) {
        Intent intent = new Intent();
        intent.putExtra("Resultat", Param);
        setResult(OK_RESULT_CODE, intent);
        finish();
    }
}

