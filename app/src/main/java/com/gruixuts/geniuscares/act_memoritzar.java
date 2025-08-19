package com.gruixuts.geniuscares;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

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
    classPersones PersonaActual;
    classProves Prova = null;
    String Filtre="";
    Integer IdPers;
    Long TempsIniciProva;
    Long TempsIniciPregunta;
    Long TempsProva;

    TextView txtNom;
    TextView txtCognom;
    TextView txtNum;
    TextView txtCurs;
    TextView txtGrup;
    TextView edtMemPAV;
    TextView edtMemComentaris;
    ImageView Imatges;

    private DocumentFile CarpetaImatges; // Inicialitzada dinàmicament amb FileUtils
    private Integer NumImg;
    private DocumentFile nomsImatges[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Integer NumProva;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memoritzar);
        
        // Inicialitzar carpeta d'imatges amb FileUtils (compatible Android 11+)
        CarpetaImatges = classGlobal.carpetaImatges;
        
        txtNom = (TextView) findViewById(R.id.txtMemNom);
        txtCognom = (TextView) findViewById(R.id.txtMemCognom);
        txtCurs = (TextView) findViewById(R.id.txtMemCurs);
        txtGrup = (TextView) findViewById(R.id.txtMemGrup);
        txtNum = (TextView) findViewById(R.id.txtMemNum);
        edtMemPAV = (TextView) findViewById(R.id.edtMemPAV);
        edtMemComentaris = (TextView) findViewById(R.id.edtMemComentaris);
        Imatges = (ImageView) findViewById(R.id.imgImatges);
        Filtre = getIntent().getStringExtra("Filtre");
        db=  GestorDB.getInstance(getApplicationContext());
        Llista = db.selPersones(Filtre,"");
        Actual=-1;  //Així la següent és la primera
        TempsIniciProva = SystemClock.currentThreadTimeMillis();
        PreguntaSeguent();

    }

    private void PreguntaSeguent() {
        // Mostla seguent persona a memoritzar

        Actual++;
        if (Actual<Llista.size()) {
            PersonaActual=Llista.get(Actual);
            IdPers= PersonaActual.getId();
            File carpeta = new File(CarpetaImatges + "/" + PersonaActual.getImatges());
            nomsImatges = LlistaImatgesSAF(PersonaActual.getImatges());
            if (nomsImatges.length != 0) {
                NumImg = 1;
            } else {
                NumImg = 0;
            }
            fotoPrimera();
            txtNom.setText(PersonaActual.getNom());
            txtCognom.setText(PersonaActual.getCognom());
            txtNum.setText(PersonaActual.getNum());
            txtCurs.setText(PersonaActual.getCurs());
            edtMemPAV.setText(PersonaActual.getPAV());
            edtMemComentaris.setText(PersonaActual.getComentaris());
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
            txtCognom.setText("");
            txtNum.setText("");
            txtCurs.setText("");
            edtMemPAV.setText("");
            edtMemComentaris.setText("");
            bt1.setEnabled(false);
            bt2.setEnabled(false);
            bt3.setEnabled(false);
            if (Prova != null) {
                Prova.setAcabada(true);
                db.actProves(Prova);
            }
        }

    }

    private DocumentFile[] LlistaImatgesSAF(String nomSubcarpeta) {

        // Si nom està buit
        if (nomSubcarpeta==null || nomSubcarpeta.isEmpty()) {
            classGlobal.mostraError(this,"Error","Aquesta persona no té nom de carpeta d'imatges");
            return new DocumentFile[0];
        }
        // Miro si existeix
        DocumentFile carpImg = classGlobal.carpetaImatges.findFile(nomSubcarpeta);
        if (carpImg==null) {
            return new DocumentFile[0]; // Si no existeix, torno llista buida
        }

        return carpImg.listFiles();
    }


    public void mostraFoto(Integer numImg) {
        ImageView iv = findViewById(R.id.imgImatges);

        if (nomsImatges == null || nomsImatges.length == 0 || NumImg <= 0 || nomsImatges.length < NumImg) {
            iv.setImageResource(R.drawable.ic_launcher_foreground);
            return;
        }

        DocumentFile f = nomsImatges[NumImg - 1];
        if (f != null) {
            iv.setImageURI(f.getUri());  // <- content://... del DocumentFile
        } else {
            iv.setImageResource(R.drawable.ic_launcher_foreground);
        }
    }
    public void fotoPrimera() {
        mostraFoto(1);
    }
    public void fotoSeguent(View view) {
        if ((NumImg < nomsImatges.length) && (NumImg>0) ) {
            NumImg++;
            mostraFoto(NumImg);
        }
    }
    public void fotoAnterior(View view) {
        if ((NumImg > 1) ) {
            NumImg--;
            mostraFoto(NumImg);
        }
    }

    public void fotoPrimera_ant() {
        if (NumImg==0) {
            ((ImageView) findViewById(R.id.imgImatges)).setImageResource(R.drawable.ic_launcher_foreground);
        } else {
            Drawable d = Drawable.createFromPath(CarpetaImatges + "/" + PersonaActual.getImatges() + "/" + nomsImatges[NumImg - 1]);
            ((ImageView) findViewById(R.id.imgImatges)).setImageDrawable(d);
        }
    }
    public void fotoSeguent_ant(View view) {
        if ((NumImg < nomsImatges.length) && (NumImg>0) ) {
            NumImg++;
            Drawable d = Drawable.createFromPath(CarpetaImatges + "/" + PersonaActual.getImatges() + "/" + nomsImatges[NumImg - 1]);
            ((ImageView) findViewById(R.id.imgImatges)).setImageDrawable(d);
        }
    }
    public void fotoAnterior_ant(View view) {
        if (NumImg > 1 ) {
            NumImg--;
            Drawable d = Drawable.createFromPath(CarpetaImatges + "/" + PersonaActual.getImatges() + "/" + nomsImatges[NumImg - 1]);
            ((ImageView) findViewById(R.id.imgImatges)).setImageDrawable(d);
        }
    }


    public void MemOk(View view) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Madrid"));
        Date Ara = cal.getTime();
        db.open();
        // Persones
        PersonaActual.setNextTipus("1h");
        cal.add(Calendar.HOUR,1);
        PersonaActual.setNextData(cal.getTime());
        PersonaActual.setPAV(((TextView) findViewById(R.id.edtMemPAV)).getText().toString());
        PersonaActual.setComentaris(((TextView) findViewById(R.id.edtMemComentaris)).getText().toString());
        db.actPersones(PersonaActual);
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
        rslt.setIdPers(PersonaActual.getId());
//Todo:1        rslt.setPregunta(PersonaActual.getCatala());
//Todo:1        rslt.setResposta(PersonaActual.getBasc());
//Todo:1        rslt.setCorrecta(PersonaActual.getBasc());
        rslt.setTemps((Long) (SystemClock.currentThreadTimeMillis() - TempsIniciPregunta));
        rslt.setValoracio(classResultats.VAL_PERFECTE);
        db.insResultat(rslt);
        PreguntaSeguent();
    }

    public void MemPasso(View view) {
        PreguntaSeguent();
    }

    public void MemEdit(View view) {
        Context context = view.getContext();
        Intent intent = new Intent(context, act_manteniment_modificar.class);
        intent.putExtra(act_manteniment_modificar.ARG_ITEM_ID, IdPers.toString());
        context.startActivity(intent);
    }

}
