package com.gruixuts.geniuscares;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class act_memoritzar extends AppCompatActivity {

    Integer Actual;
    ArrayList<classPersones> Llista;
    SimpleDateFormat frmtData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    GestorDB db;
    classPersones ParaulaActual;
    classProves Prova = null;
    String Filtre="";
    Integer IdEntDic;
    Long TempsIniciProva;
    Long TempsIniciPregunta;
    Long TempsProva;

    TextView txtNom;
    TextView txtCognom1;
    TextView txtCognom2;
    TextView txtCurs;
    TextView edtMemPAV;
    TextView edtMemComentaris;
    ImageView Imatges;

    private String CarpetaImatges; // Inicialitzada dinàmicament amb FileUtils
    private Integer NumImg;
    private String nomImatge[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Integer NumProva;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memoritzar);
        
        // Inicialitzar carpeta d'imatges amb FileUtils (compatible Android 11+)
        CarpetaImatges = FileUtils.getCarpetaImatges(this);
        
        txtNom = (TextView) findViewById(R.id.txtMemNom);
        txtCognom1 = (TextView) findViewById(R.id.txtMemCognom1);
        txtCognom2 = (TextView) findViewById(R.id.txtMemCognom2);
        txtCurs = (TextView) findViewById(R.id.txtMemCurs);
        edtMemPAV = (TextView) findViewById(R.id.edtMemPAV);
        edtMemComentaris = (TextView) findViewById(R.id.edtMemComentaris);
        Imatges = (ImageView) findViewById(R.id.imgImatges);
        Filtre = getIntent().getStringExtra("Filtre");
        db=  new GestorDB(getApplicationContext());
        db.open();
        Llista = db.selDiccionari(Filtre,"");
        db.close();
        Actual=-1;
        TempsIniciProva = SystemClock.currentThreadTimeMillis();
        PreguntaSeguent();

    }

    private void PreguntaSeguent() {
        // Mostla seguent paraula a memoritzar

        Actual++;
        if (Actual<Llista.size()) {
            ParaulaActual=Llista.get(Actual);
            IdEntDic= ParaulaActual.getId();
            File carpeta = new File(CarpetaImatges + "/" + ParaulaActual.getImatges());
            NumImg = 0;
            if (carpeta.exists()) {
                nomImatge = carpeta.list();
                if (nomImatge.length != 0) {
                    NumImg = 1;
                }
            }
            fotoPrimera();
            txtNom.setText(ParaulaActual.getNom());
            txtCognom1.setText(ParaulaActual.getCognom1());
            txtCognom2.setText(ParaulaActual.getNum());
            txtCurs.setText(ParaulaActual.getCurs());
            edtMemPAV.setText(ParaulaActual.getPAV());
            edtMemComentaris.setText(ParaulaActual.getComentaris());
            TextView txtCompt = (TextView) findViewById(R.id.txtCompt);
            txtCompt.setText("Actual: " + (Actual + 1) + "/" + Llista.size());
            TempsIniciPregunta = SystemClock.currentThreadTimeMillis();
        } else {
            Toast msg = Toast.makeText(this, "No hi ha més cares a memoritzar", Toast.LENGTH_SHORT);
            msg.setDuration(Toast.LENGTH_LONG);
            msg.show();
            Button bt1 = (Button) findViewById(R.id.cmdMemOk);
            Button bt2 = (Button) findViewById(R.id.cmdMemPasso);
            Button bt3 = (Button) findViewById(R.id.cmdMemEdit);
            txtNom.setText("");
            txtCognom1.setText("");
            txtCognom2.setText("");
            txtCurs.setText("");
            edtMemPAV.setText("");
            edtMemComentaris.setText("");
            bt1.setEnabled(false);
            bt2.setEnabled(false);
            bt3.setEnabled(false);
            if (Prova != null) {
                Prova.setAcabada(true);
                db.open();
                db.actProves(Prova);
                db.close();
            }
        }

    }

    public void fotoPrimera() {
        if (NumImg==0) {
            ((ImageView) findViewById(R.id.imgImatges)).setImageResource(R.drawable.ic_launcher_foreground);
        } else {
            Drawable d = Drawable.createFromPath(CarpetaImatges + "/" + ParaulaActual.getImatges() + "/" + nomImatge[NumImg - 1]);
            ((ImageView) findViewById(R.id.imgImatges)).setImageDrawable(d);
        }
    }
    public void fotoSeguent(View view) {
        if ((NumImg < nomImatge.length) && (NumImg>0) ) {
            NumImg++;
            Drawable d = Drawable.createFromPath(CarpetaImatges + "/" + ParaulaActual.getImatges() + "/" + nomImatge[NumImg - 1]);
            ((ImageView) findViewById(R.id.imgImatges)).setImageDrawable(d);
        }
    }
    public void fotoAnterior(View view) {
        if (NumImg > 1 ) {
            NumImg--;
            Drawable d = Drawable.createFromPath(CarpetaImatges + "/" + ParaulaActual.getImatges() + "/" + nomImatge[NumImg - 1]);
            ((ImageView) findViewById(R.id.imgImatges)).setImageDrawable(d);
        }
    }


    public void MemOk(View view) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Madrid"));
        Date Ara = cal.getTime();
        db.open();
        // Diccionari
        ParaulaActual.setNextTipus("1h");
        cal.add(Calendar.HOUR,1);
        ParaulaActual.setNextData(cal.getTime());
        ParaulaActual.setPAV(((TextView) findViewById(R.id.edtMemPAV)).getText().toString());
        ParaulaActual.setComentaris(((TextView) findViewById(R.id.edtMemComentaris)).getText().toString());
        db.actDiccionari(ParaulaActual);
        // Prova
        if (Prova == null) {
            Prova = new classProves();
            Prova.setId(db.NumNovaProva());
            Prova.setDia(Ara);
            Prova.setTipusProva(classProves.TIP_APRENDRE);
            Prova.setSeleccio(Filtre);
            Prova.setNumPreguntes(Llista.size());
            Prova.setAcabada(false);
        }
        Prova.setTemps((Long) (SystemClock.currentThreadTimeMillis() - TempsIniciProva));
        Prova.setNumRespostes(Actual);
        db.actProves(Prova);
        // Resultat
        classResultats rslt = new classResultats();
        rslt.setDia(Ara);
        rslt.setIdProva(Prova.getId());
        rslt.setIdEntDic(ParaulaActual.getId());
//Todo:1        rslt.setPregunta(ParaulaActual.getCatala());
//Todo:1        rslt.setResposta(ParaulaActual.getBasc());
//Todo:1        rslt.setCorrecta(ParaulaActual.getBasc());
        rslt.setTemps((Long) (SystemClock.currentThreadTimeMillis() - TempsIniciPregunta));
        rslt.setValoracio(classResultats.VAL_PERFECTE);
        db.insResultat(rslt);
        db.close();
        PreguntaSeguent();
    }

    public void MemPasso(View view) {
        PreguntaSeguent();
    }

    public void MemEdit(View view) {
        Context context = view.getContext();
        Intent intent = new Intent(context, act_manteniment_modificar.class);
        intent.putExtra(act_manteniment_modificar.ARG_ITEM_ID, IdEntDic.toString());
        context.startActivity(intent);
    }

}
