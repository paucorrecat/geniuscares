package com.gruixuts.geniuscares;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class act_pregunta extends AppCompatActivity {

    protected static final int REQUEST_CODE = 10;
    Integer Actual;
    Integer NumProva;
    String TipusProva;
    ArrayList<classPersones> Llista;
    Integer NumBe = 0;
    Integer NumRev = 0;
    Integer NumApr = 0;
    Integer NumObl = 0;
    Long TempsIniciPregunta;
    Long TempsProva;
    Long TempsPregunta;
    classProves ProvaActual;
    GestorDB db;
    SimpleDateFormat frmtData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregunta);
        db = new GestorDB(getApplicationContext());
        String Filtre = getIntent().getStringExtra("Filtre");
        TipusProva = getIntent().getStringExtra("Tipus");
        Integer Top = Integer.parseInt(getIntent().getStringExtra("Top"));
        String Ordre = getIntent().getStringExtra("Ordre");
        String Data;
        Date Avui = new Date();
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("Europe/Madrid"));
        String Ref = frmtData.format(new Date(cal.getTimeInMillis()));
        db.open();
        NumProva = db.UltimaProva() + 1;
        TempsProva = 0L;

        if (TipusProva.equals(classProves.TIP_EXAMEN) || TipusProva.equals(classProves.TIP_REPAS)) {
            if (Ordre.equals("Ant")) {
                Llista = db.selPersonesAntiguetat(Filtre, "", Top);
            } else {
                Llista = db.selPersones(Filtre, "");
            }
        } else if (TipusProva.equals(classProves.TIP_REVISIO)) {
            Llista = db.selPersonesRevisar(Filtre, Ordre, Top);
        }

        ProvaActual = new classProves(NumProva,
                Avui,
                getIntent().getStringExtra("Tipus"),
                Filtre + "|x|" + Ordre + "|x|" + Top,
                (Integer) Llista.size(),
                0,
                0L,
                false);
        db.insProves(ProvaActual);
        db.close();
        Actual = -1;
        PreguntaSeguent();

    }

    private void PreguntaSeguent() {
        EditText edtBasc = (EditText) findViewById(R.id.edtBasc);
        TextView txtCat = (TextView) findViewById(R.id.txtCatala);
        Actual++;
        edtBasc.setText("");
        if (Actual < Llista.size()) {
            TempsIniciPregunta = SystemClock.currentThreadTimeMillis();
            TextView txtCompt = (TextView) findViewById(R.id.txtComptador);
            txtCompt.setText("Actual: " + (Actual + 1) + "/" + Llista.size());
//            txtCat.setText(Llista.get(Actual).getCatala());
            txtCat.setText("de moment ¿1?");
        } else {
            Button bt = (Button) findViewById(R.id.cmdOkBasc);
            txtCat.setText("");
            bt.setEnabled(false);
            edtBasc.setEnabled(false);
            ProvaActual.setAcabada(true);
            db.open();
            db.actProves(ProvaActual);
            db.close();
        }
    }

    public void Valorar(View view) {
        String Resposta = ((EditText) findViewById(R.id.edtBasc)).getText().toString();
        // ADAPTACIONS PENDENTS
        //Todo:1 String Persones = Llista.get(Actual).getBasc();
        String Persones = Llista.get(Actual).getNom() + " " + Llista.get(Actual).getCognom();
        //Todo:1 String Pregunta = Llista.get(Actual).getCatala();
        String Pregunta = "Foto ¿2?";
        String tProva = TipusProva;
        String Next;
        TempsPregunta = SystemClock.currentThreadTimeMillis() - TempsIniciPregunta;
        TempsProva += SystemClock.currentThreadTimeMillis() - TempsIniciPregunta;
        Next = Llista.get(Actual).getNextTipus() + "  " + Llista.get(Actual).getNextDataTxt();

        //Todo: Dissenyar el sistema de valoració de respostes
        // Ara és una còpia de Basc quan es responia perfecte
        TextView txtCnt = (TextView) findViewById(R.id.txtPrePer);
        NumBe++;
        txtCnt.setText("Bé: " + NumBe);
        RegistraResultat(classResultats.VAL_PERFECTE);
        PreguntaSeguent();

        /*
        if (SonIguals(Resposta, Persones)) {
            TextView txtCnt = (TextView) findViewById(R.id.txtPrePer);
            NumBe++;
            txtCnt.setText("Bé: " + NumBe);
            RegistraResultat(classResultats.VAL_PERFECTE);
            PreguntaSeguent();
        } else {
            Intent intent = new Intent(act_pregunta.this, act_pregunta_val.class);

            // Passem les cadenes a comparar ja normalitzades
            intent.putExtra("IdPers", Llista.get(Actual).getId());
            intent.putExtra("Resposta", Resposta);
            intent.putExtra("Correcta", Persones);
            intent.putExtra("Pregunta", Pregunta);
            intent.putExtra("Prova", tProva);
            intent.putExtra("Next", Next);
            startActivityForResult(intent, REQUEST_CODE);

        }
        */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String Resultat;
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            // cogemos el valor devuelto por la otra actividad
            if (data != null) {
                Resultat = data.getStringExtra("Resultat");
            } else {  // s'ha tancat pantalla sense premer botó
                Resultat = "No";
                return;
            }
            // Avis per pantalla
            //Toast.makeText(this, "Resultat: " + Resultat, Toast.LENGTH_LONG).show();

            if (Resultat.equals(classResultats.VAL_PERFECTE)) {
                TextView txtCnt = findViewById(R.id.txtPrePer);
                NumBe++;
                txtCnt.setText("Bé: " + NumBe);

            } else if (Resultat.equals(classResultats.VAL_REVISAR)) {
                TextView txtCnt = findViewById(R.id.txtPreRev);
                NumRev++;
                txtCnt.setText("A Revisar: " + NumRev);

            } else if (Resultat.equals(classResultats.VAL_APRES)) {
                TextView txtCnt = findViewById(R.id.txtPreApr);
                NumApr++;
                txtCnt.setText("Apres: " + NumApr);

            } else if (Resultat.equals(classResultats.VAL_OBLIDAT)) {
                TextView txtCnt = findViewById(R.id.txtPreObl);
                NumObl++;
                txtCnt.setText("Oblidat: " + NumObl);

            } else return;
            RegistraResultat(Resultat);
            PreguntaSeguent();
        }
    }


    private Boolean SonIguals(String Resposta, String Persones) {

        if (Resposta == Persones) {
            return true;
        }

        //Normalitzem
        Resposta = Resposta.replaceAll("( )+", " ");
        Resposta = Resposta.replaceAll(", ", ",");
        Resposta = Resposta.replaceAll(" ,", ",");
        Resposta = Resposta.toLowerCase();
        Resposta = Resposta.trim();
        Persones = Persones.replaceAll("( )+", " ");
        Persones = Persones.replaceAll(", ", ",");
        Persones = Persones.replaceAll(" ,", ",");
        Persones = Persones.toLowerCase();
        Persones = Persones.trim();
        if (Resposta.equals(Persones)) {
            return true;
        }
        if (Resposta.contains(",")) {
            String[] Respostes;
            String[] Entrades;
            Respostes = Resposta.split(",");
            Entrades = Persones.split(",");
            int ln = Respostes.length;
            if (ln == Entrades.length) {
                int Iguals = 0;
                for (int rn = 0; rn < ln; rn++) {
                    for (int dn = 0; dn < ln; dn++) {
                        if (Respostes[rn].equals(Entrades[dn])) {
                            Entrades[dn] = "";
                            Iguals++;
                        }
                    }
                }
                if (ln == Iguals) {
                    return true;
                }
                return false;
            }
            return false;
        }
        return false;

    }

    private void RegistraResultat(String Resultat) {
        classResultats Result = new classResultats();
        EditText edtBasc = (EditText) findViewById(R.id.edtBasc);
        TextView txtCat = (TextView) findViewById(R.id.txtCatala);
        classPersones Pers;
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("Europe/Madrid"));

        //Crea Resultat
        Result.setDia(new Date());
        Result.setIdProva(NumProva);
        Result.setIdPers(Llista.get(Actual).getId());
        Result.setPregunta(txtCat.getText().toString());
        // Todo: Habilitar el registre de resultats a la resposta múltiple
        Result.setResposta(edtBasc.getText().toString());
        Result.setCorrecta(Llista.get(Actual).getNom() + "," + Llista.get(Actual).getCognom() + "," +
                           Llista.get(Actual).getNum() + "," + Llista.get(Actual).getCurs());
        Result.setTemps(TempsPregunta);
        Result.setValoracio(Resultat);

        db.open();
        db.insResultat(Result);
        // Actualitza Prova
        ProvaActual.setTemps(TempsProva);
        ProvaActual.setNumRespostes(NumBe + NumRev + NumApr + NumObl);
        db.actProves(ProvaActual);
        // Actualitza Persones
        Pers = Llista.get(Actual);
        cal.setTime(new Date());
        if ((Resultat.equals(classResultats.VAL_PERFECTE)) && (cal.getTime().after(Pers.getNextData()))) {
            if (TipusProva.equals( classProves.TIP_REPAS)) {

                switch (Pers.getNextTipus()) {
                    case "1h":
                        Pers.setNextTipus("1d");
                        cal.add(Calendar.HOUR, 23);
                        break;
                    case "1d":
                        Pers.setNextTipus("1s");
                        cal.add(Calendar.DATE, 6);
                        break;
                    case "1s":
                        Pers.setNextTipus("1m");
                        cal.add(Calendar.DATE, 21);
                        break;
                    case "1m":
                        Pers.setNextTipus("6m");
                        cal.add(Calendar.DATE, 28 * 5);
                        break;
                    case "6m":
                        cal.add(Calendar.DATE, 28 * 6);
                }
                Pers.setNextData(cal.getTime());
                db.actPersones(Pers);
            }
        } else if (Resultat.equals(classResultats.VAL_APRES)) {
            Pers.setNextTipus("1h");
            cal.add(Calendar.HOUR, 1);
            Pers.setNextData(cal.getTime());
            db.actPersones(Pers);
        } else if (Resultat.equals(classResultats.VAL_OBLIDAT)) {
            Pers.setNextTipus("a");
            Pers.setNextData("");
            db.actPersones(Pers);
        }
        // si és VAL_REVISIO no s'ha de gravar res

        db.close();
    }

    public void PregEdit(View view) {
        Context context = view.getContext();
        Intent intent = new Intent(context, act_manteniment_modificar.class);
        intent.putExtra(act_manteniment_modificar.ARG_ITEM_ID, Llista.get(Actual).getId().toString());

        context.startActivity(intent);

    }


}
