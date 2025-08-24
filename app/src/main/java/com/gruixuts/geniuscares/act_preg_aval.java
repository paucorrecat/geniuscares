package com.gruixuts.geniuscares;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class act_preg_aval extends AppCompatActivity {

    private int numIndex; // Index 0..N del item que s'està veient dins la llista @objLlistaTrobades
    private classPersones mItem;
    private classResposta Resp;
    private classResposta Avaluacio;
    private String TallVeu;
    GestorDB db;

    // Imatges
    private String CarpetaImatges;
    private String CarpetaImatgesItem;  // La carpeta del Item actual
    private Integer numImatge;
    private String nomsImatge[] = {};

    // Paràmetres
    public static final String ARG_ID_ITEM = "id_item";
    public static final String ARG_RESP = "resposta";
    public static final String ARG_AVAL = "avaluacio";
    public static final String ARG_CORR = "corregit";
    public static final String ARG_TALLVEU = "TallVeu";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preg_aval);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        numIndex = Integer.parseInt(getIntent().getStringExtra( "item_id"));
//        numIndex = Integer.parseInt(getIntent().getStringExtra( Global.ARG_PREG_AVAL_ITEM_ID));
        numIndex = Integer.parseInt(getIntent().getStringExtra(ARG_ID_ITEM));
        Resp = new classResposta(getIntent().getStringExtra(ARG_RESP));
        Avaluacio = new classResposta(getIntent().getStringExtra(ARG_AVAL));
        TallVeu = getIntent().getStringExtra(ARG_TALLVEU);
//        CarpetaImatges = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pau/GeniusCares/Imatges";
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        CarpetaImatges=contextWrapper.getExternalFilesDir("Imatges").toString();//  o Environment.DIRECTORY_DOCUMENTS enlloc de Copies
        db=  new GestorDB(getApplicationContext());
        mItem = objLlistaTrobats.ITEMS.get(numIndex);
        CarregaItem(mItem);

    }

    private void CarregaItem(classPersones item) {
        if (item != null) {
            ((TextView) findViewById(R.id.txtValNomOk)).setText(item.getNom());
            ((TextView) findViewById(R.id.txtValCgnmOk)).setText(item.getCognom());
            ((TextView) findViewById(R.id.txtValCursOk)).setText(item.getCurs());
            ((TextView) findViewById(R.id.txtValGrupOk)).setText(item.getGrup());
            ((TextView) findViewById(R.id.txtValNumOk)).setText(item.getNum());
            ((TextView) findViewById(R.id.txtValNomRsp)).setText(Resp.getNom());
            ((TextView) findViewById(R.id.txtValCgnmRsp)).setText(Resp.getCognom());
            ((TextView) findViewById(R.id.txtValCursRsp)).setText(Resp.getCurs());
            ((TextView) findViewById(R.id.txtValGrupRsp)).setText(Resp.getGrup());
            ((TextView) findViewById(R.id.txtValNumRsp)).setText(Resp.getNum());

            switch (Avaluacio.getNom()) {
                case classResultats.VAL_PERFECTE:
                    ((RadioButton) findViewById(R.id.rdNomBe)).setChecked(true);
                    break;
                case classResultats.VAL_REPASSAR:
                    ((RadioButton) findViewById(R.id.rdNomRev)).setChecked(true);
                    break;
                case classResultats.VAL_APRES:
                    ((RadioButton) findViewById(R.id.rdNomApres)).setChecked(true);
                    break;
                case classResultats.VAL_OBLIDAT:
                    ((RadioButton) findViewById(R.id.rdNomOblid)).setChecked(true);
                    break;
            }

            switch (Avaluacio.getCognom()) {
                case classResultats.VAL_PERFECTE:
                    ((RadioButton) findViewById(R.id.rdCgnmBe)).setChecked(true);
                    break;
                case classResultats.VAL_REPASSAR:
                    ((RadioButton) findViewById(R.id.rdCgnmRev)).setChecked(true);
                    break;
                case classResultats.VAL_APRES:
                    ((RadioButton) findViewById(R.id.rdCgnmApres)).setChecked(true);
                    break;
                case classResultats.VAL_OBLIDAT:
                    ((RadioButton) findViewById(R.id.rdCgnmOblid)).setChecked(true);
                    break;
            }

            switch (Avaluacio.getCurs()) {
                case classResultats.VAL_PERFECTE:
                    ((RadioButton) findViewById(R.id.rdCursBe)).setChecked(true);
                    break;
                case classResultats.VAL_REPASSAR:
                    ((RadioButton) findViewById(R.id.rdCursRev)).setChecked(true);
                    break;
                case classResultats.VAL_APRES:
                    ((RadioButton) findViewById(R.id.rdCursApres)).setChecked(true);
                    break;
                case classResultats.VAL_OBLIDAT:
                    ((RadioButton) findViewById(R.id.rdCursOblid)).setChecked(true);
                    break;
            }

            switch (Avaluacio.getGrup()) {
                case classResultats.VAL_PERFECTE:
                    ((RadioButton) findViewById(R.id.rdGrupBe)).setChecked(true);
                    break;
                case classResultats.VAL_REPASSAR:
                    ((RadioButton) findViewById(R.id.rdGrupRev)).setChecked(true);
                    break;
                case classResultats.VAL_APRES:
                    ((RadioButton) findViewById(R.id.rdGrupApres)).setChecked(true);
                    break;
                case classResultats.VAL_OBLIDAT:
                    ((RadioButton) findViewById(R.id.rdGrupOblid)).setChecked(true);
                    break;
            }

            switch (Avaluacio.getNum()) {
                case classResultats.VAL_PERFECTE:
                    ((RadioButton) findViewById(R.id.rdNumBe)).setChecked(true);
                    break;
                case classResultats.VAL_REPASSAR:
                    ((RadioButton) findViewById(R.id.rdNumRev)).setChecked(true);
                    break;
                case classResultats.VAL_APRES:
                    ((RadioButton) findViewById(R.id.rdNumApres)).setChecked(true);
                    break;
                case classResultats.VAL_OBLIDAT:
                    ((RadioButton) findViewById(R.id.rdNumOblid)).setChecked(true);
                    break;
            }
            
            ((TextView) findViewById(R.id.txtValVeuRsp)).setText(TallVeu);

            // Imatges:
            if (item.getImatges()!=null) {
                assert (item.getImatges().length()>0);
                CarpetaImatgesItem = CarpetaImatges + "/" + item.getImatges();
                File carpeta = new File(CarpetaImatgesItem);
                if (!carpeta.exists()) { // Crear-la
                    // Global.MissatgeError("La cerpeta d'immatges " + CarpetaImatgesItem + " no existeix",this);
                    numImatge = 0;
                    //carpeta.mkdirs();
                    nomsImatge = new String[0];

                } else {
                    nomsImatge = carpeta.list();
                }
                if (nomsImatge.length != 0) {
                    numImatge = 1;
                    Drawable d = Drawable.createFromPath(CarpetaImatgesItem + "/" + nomsImatge[numImatge - 1]);
                    ((ImageView) findViewById(R.id.imgImatges)).setImageDrawable(d);
                } else {
                    numImatge = 0;
                }
            } else {
                CarpetaImatgesItem="";
                numImatge = 0;
                nomsImatge = new String[0];
            }
            if (numImatge==0) {
                ((ImageView) findViewById(R.id.imgImatges)).setImageResource(R.mipmap.ic_launcher);
            }
        } else {
            classGlobal.mostraError(this,"Error","item=null, i no hauria de ser");
            finish();
        }
    }

    public void cmd_Ok(View view) {
        classResposta aval= new classResposta();
        int rb = ((RadioGroup) findViewById(R.id.rdgNom)).getCheckedRadioButtonId();
        if (rb==R.id.rdNomBe ) {
            aval.setNom(classResultats.VAL_PERFECTE);
        } else if (rb==R.id.rdNomRev ) {
            aval.setNom(classResultats.VAL_REPASSAR);
        } else if (rb==R.id.rdNomApres ) {
            aval.setNom(classResultats.VAL_APRES);
        } else if (rb==R.id.rdNomOblid ) {
            aval.setNom(classResultats.VAL_OBLIDAT);
        } else {
            aval.setNom("");
        }
        rb = ((RadioGroup) findViewById(R.id.rdgCgnm)).getCheckedRadioButtonId();
        if (rb==R.id.rdCgnmBe ) {
            aval.setCognom(classResultats.VAL_PERFECTE);
        } else if (rb==R.id.rdCgnmRev ) {
            aval.setCognom(classResultats.VAL_REPASSAR);
        } else if (rb==R.id.rdCgnmApres ) {
            aval.setCognom(classResultats.VAL_APRES);
        } else if (rb==R.id.rdCgnmOblid ) {
            aval.setCognom(classResultats.VAL_OBLIDAT);
        } else {
            aval.setCognom("");
        }

        rb = ((RadioGroup) findViewById(R.id.rdgCurs)).getCheckedRadioButtonId();
        if (rb==R.id.rdCursBe ) {
            aval.setCurs(classResultats.VAL_PERFECTE);
        } else if (rb==R.id.rdCursRev ) {
            aval.setCurs(classResultats.VAL_REPASSAR);
        } else if (rb==R.id.rdCursApres ) {
            aval.setCurs(classResultats.VAL_APRES);
        } else if (rb==R.id.rdCursOblid ) {
            aval.setCurs(classResultats.VAL_OBLIDAT);
        } else {
            aval.setCurs("");
        }

        rb = ((RadioGroup) findViewById(R.id.rdgGrup)).getCheckedRadioButtonId();
        if (rb==R.id.rdGrupBe ) {
            aval.setGrup(classResultats.VAL_PERFECTE);
        } else if (rb==R.id.rdGrupRev ) {
            aval.setGrup(classResultats.VAL_REPASSAR);
        } else if (rb==R.id.rdGrupApres ) {
            aval.setGrup(classResultats.VAL_APRES);
        } else if (rb==R.id.rdGrupOblid ) {
            aval.setGrup(classResultats.VAL_OBLIDAT);
        } else {
            aval.setGrup("");
        }

        rb = ((RadioGroup) findViewById(R.id.rdgNum)).getCheckedRadioButtonId();
        if (rb==R.id.rdNumBe ) {
            aval.setNum(classResultats.VAL_PERFECTE);
        } else if (rb==R.id.rdNumRev ) {
            aval.setNum(classResultats.VAL_REPASSAR);
        } else if (rb==R.id.rdNumApres ) {
            aval.setNum(classResultats.VAL_APRES);
        } else if (rb==R.id.rdNumOblid ) {
            aval.setNum(classResultats.VAL_OBLIDAT);
        } else {
            aval.setNum("");
        }
        Intent data = new Intent();
        data.putExtra(ARG_CORR ,aval.toString());
        setResult(RESULT_OK,data);
        finish();
    }


    public void cmd_Edita(View view) {
        Context context = view.getContext();
        Intent intent = new Intent(context, act_manteniment_modificar.class);
        //No! num ordre obj...
        intent.putExtra(act_manteniment_modificar.ARG_ITEM_ID, numIndex);
        context.startActivity(intent);

    }

}