package com.gruixuts.geniuscares;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.documentfile.provider.DocumentFile;

public class act_manteniment_modificar extends AppCompatActivity {

    public static final String ARG_ITEM_ID = "item_id";
    GestorDB db;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> buscaCodiLauncher;
    private DocumentFile carpetaImatges = classGlobal.carpetaImatges;
    private Uri carpetaUri; // p2
    private classPersones mItem;
    Boolean ResetApres = false;

    private DocumentFile[] llistaImatges;
    private int posicioImg = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manteniment_modificar);

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getExtras() != null) {
                            Bitmap imgBitmap = (Bitmap) data.getExtras().get("data");
                            ImageView imgView = findViewById(R.id.imgImatges);
                            imgView.setImageBitmap(imgBitmap);
                            desaImatge(imgBitmap, mItem.getImatges());

                            // Actualitza la llista d’imatges després d’afegir-ne una nova
                            llistaImatges = obtenirLlistaImatgesSAF(mItem.getImatges());
                            posicioImg = llistaImatges.length - 1;
                        }
                    }
                }
        );
        buscaCodiLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.hasExtra("Codi")) {
                            String codi = data.getStringExtra("Codi");
                            ((TextView) findViewById(R.id.edtModCodi)).setText(codi);
                        }
                    }
                }
        );

//        carpetaImatgesUri = classGlobal.carpetaImatges;
        if (carpetaImatges == null) {
            classGlobal.mostraError(this,"Accés SAF", "No hi ha carpeta d'imatges amb permisos. Torna a iniciar la app");
            finish();
            return;
        }


        ResetApres = false;
        String Clau = getIntent().getStringExtra(ARG_ITEM_ID);
        db = new GestorDB(getApplicationContext());
        db.open();
        if (Clau.length() > 0) {
            mItem = db.selPers(Integer.parseInt(Clau));
        } else {
            mItem = new classPersones();
        }

        llistaImatges = obtenirLlistaImatgesSAF(mItem.getImatges());
        if (llistaImatges.length > 0) {
            posicioImg = 0;
            mostraImatgeSAF(llistaImatges[posicioImg]);
        }

        if (mItem != null) {
            ((TextView) findViewById(R.id.txtModId)).setText("" + mItem.getId());
            ((TextView) findViewById(R.id.edtModNom)).setText(mItem.getNom());
            ((TextView) findViewById(R.id.edtModCognom)).setText(mItem.getCognom());
            ((TextView) findViewById(R.id.edtModNum)).setText(mItem.getNum());
            ((TextView) findViewById(R.id.edtModCurs)).setText(mItem.getCurs());
            ((TextView) findViewById(R.id.edtModCodi)).setText(mItem.getCodi());
            ((Switch) findViewById(R.id.schTraduible)).setChecked(mItem.getAMemoritzar());
            ((TextView) findViewById(R.id.edtModGrup)).setText(mItem.getGrup());
            switch (mItem.getNextTipus()) {
                case "t": ((RadioButton) findViewById(R.id.rdT)).setChecked(true); break;
                case "a": ((RadioButton) findViewById(R.id.rdA)).setChecked(true); break;
                case "1h": ((RadioButton) findViewById(R.id.rd1h)).setChecked(true); break;
                case "1d": ((RadioButton) findViewById(R.id.rd1d)).setChecked(true); break;
                case "1s": ((RadioButton) findViewById(R.id.rd1s)).setChecked(true); break;
                case "1m": ((RadioButton) findViewById(R.id.rd1m)).setChecked(true); break;
                case "6m": ((RadioButton) findViewById(R.id.rd6m)).setChecked(true); break;
                default:
                    Toast.makeText(getApplicationContext(), "Tipus propera acció: '" + mItem.getNextTipus() + "' no identificat", Toast.LENGTH_LONG).show();
                    ((RadioButton) findViewById(R.id.rdA)).setChecked(true);
            }
            ((TextView) findViewById(R.id.edtModNextData)).setText(mItem.getNextDataTxt());
        }
    }

    private DocumentFile[] obtenirLlistaImatgesSAF(String nomSubcarpeta) {

        // Si nom està buit
        if (nomSubcarpeta==null || nomSubcarpeta.isEmpty()) {
            classGlobal.mostraError(this,"Error","Aquesta persona no té nom de carpeta d'imatges");
            return new DocumentFile[0];
        }
        // Miro si existeix
        DocumentFile carpImg = carpetaImatges.findFile(nomSubcarpeta);
        if (carpImg==null) {
            return new DocumentFile[0]; // Si no existeix, torno llista buida
        }

        return carpImg.listFiles();
    }

    private void mostraImatgeSAF(DocumentFile docFile) {
        try {
            ImageView imgView = findViewById(R.id.imgImatges);
            Uri uri = docFile.getUri();
            imgView.setImageURI(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fotoSeguent(View view) {
        if (llistaImatges != null && posicioImg < llistaImatges.length - 1) {
            posicioImg++;
            mostraImatgeSAF(llistaImatges[posicioImg]);
        }
    }

    public void fotoAnterior(View view) {
        if (llistaImatges != null && posicioImg > 0) {
            posicioImg--;
            mostraImatgeSAF(llistaImatges[posicioImg]);
        }
    }

    public void fotoAfegeix(View view) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(cameraIntent);
        }
    }

    private void desaImatge(Bitmap bitmap, String nomSubcarpeta) {

        if (carpetaImatges == null) {
            return;
        }

        // Miro si existeix
        DocumentFile carpImg = carpetaImatges.findFile(nomSubcarpeta);
        if (carpImg==null) {
            carpImg = carpetaImatges.createDirectory(nomSubcarpeta);
        }
        if (carpImg==null) {
            classGlobal.mostraError(this,"Error", "No s'ha pogut crear carpeta d'imatges");
            return;
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + ".jpg";

        DocumentFile nouFitxer = carpImg.createFile("image/jpeg", imageFileName);
        if (nouFitxer == null) {
            classGlobal.mostraError(this,"Error", "No s'ha pogut crear carpeta d'imatges");
            return;
        }

        try (OutputStream out = getContentResolver().openOutputStream(nouFitxer.getUri())) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

       public void fotoElimina(View view) {
        // Implementar eliminació d'imatge si cal
    }

    public void PregEsborra(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Segur que vols esborrar?");
        builder.setTitle("Atenció!!");
        builder.setCancelable(false);
        builder.setNegativeButton("No", (dialog, id) -> dialog.cancel());
        builder.setPositiveButton("Sí", (dialog, id) -> {
            db.open();
            if (mItem.getId() != 0) {
                db.delPers(mItem.getId());
            }
            db.close();
            dialog.cancel();
            finish();
        });
        builder.create().show();
    }

    public void Grava(View view) {
        db.open();
        mItem.setNom(((TextView) findViewById(R.id.edtModNom)).getText().toString());
        mItem.setCognom(((TextView) findViewById(R.id.edtModCognom)).getText().toString());
        mItem.setNum(((TextView) findViewById(R.id.edtModNum)).getText().toString());
        mItem.setCurs(((TextView) findViewById(R.id.edtModCurs)).getText().toString());
        mItem.setCodi(((TextView) findViewById(R.id.edtModCodi)).getText().toString());
        mItem.setAMemoritzar(((Switch) findViewById(R.id.schTraduible)).isChecked());
        mItem.setGrup(((TextView) findViewById(R.id.edtModGrup)).getText().toString());
        if (((RadioButton) findViewById(R.id.rdT)).isChecked()) mItem.setNextTipus("t");
        else if (((RadioButton) findViewById(R.id.rdA)).isChecked()) mItem.setNextTipus("a");
        else if (((RadioButton) findViewById(R.id.rd1h)).isChecked()) mItem.setNextTipus("1h");
        else if (((RadioButton) findViewById(R.id.rd1d)).isChecked()) mItem.setNextTipus("1d");
        else if (((RadioButton) findViewById(R.id.rd1s)).isChecked()) mItem.setNextTipus("1s");
        else if (((RadioButton) findViewById(R.id.rd1m)).isChecked()) mItem.setNextTipus("1m");
        else if (((RadioButton) findViewById(R.id.rd6m)).isChecked()) mItem.setNextTipus("6m");
        else mItem.setNextTipus("t");

        mItem.setNextData(((TextView) findViewById(R.id.edtModNextData)).getText().toString());

        if (mItem.getNextTipus().equals("t") || mItem.getNextTipus().equals("a")) {
            mItem.setNextData("");
        } else {
            if (mItem.getNextDataTxt().equals("")) {
                Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("Europe/Madrid"));
                cal.setTime(new Date());
                switch (mItem.getNextTipus()) {
                    case "1h": cal.add(Calendar.HOUR, 1); break;
                    case "1d": cal.add(Calendar.DATE, 1); break;
                    case "1s": cal.add(Calendar.DATE, 7); break;
                    case "1m": cal.add(Calendar.DATE, 28); break;
                    case "6m": cal.add(Calendar.DATE, 168); break;
                }
                mItem.setNextData(cal.getTime());
            }
        }

        if (mItem.getId() == 0) db.creaDiccionari(mItem);
        else db.actDiccionari(mItem);

        db.close();
        finish();
    }

    public void AutomTraduible(View view) {
        if (((Switch) findViewById(R.id.schTraduible)).isChecked()) {
            ((RadioButton) findViewById(R.id.rdT)).setChecked(false);
            ((RadioButton) findViewById(R.id.rdA)).setChecked(false);
            ((RadioButton) findViewById(R.id.rd1h)).setChecked(false);
            ((RadioButton) findViewById(R.id.rd1d)).setChecked(false);
            ((RadioButton) findViewById(R.id.rd1s)).setChecked(false);
            ((RadioButton) findViewById(R.id.rd1m)).setChecked(false);
            ((RadioButton) findViewById(R.id.rd6m)).setChecked(false);
            ((TextView) findViewById(R.id.edtModNextData)).setText("");
        } else {
            ((RadioButton) findViewById(R.id.rdA)).setChecked(true);
        }
    }

    public void BuscaCodi(View view) {
        Intent intent = new Intent(act_manteniment_modificar.this, act_aux_Codi.class);
        intent.putExtra("Codi", ((TextView) findViewById(R.id.edtModCodi)).getText());
        buscaCodiLauncher.launch(intent);
    }
}
