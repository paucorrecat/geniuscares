package com.gruixuts.geniuscares;

import android.content.DialogInterface;
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
    private ActivityResultLauncher<Intent> folderPickerLauncher;
    private Uri carpetaImatgesUri;
    private classPersones mItem;
    Boolean ResetApres = false;

    private DocumentFile[] llistaImatgesSAF;
    private int posicioImgSAF = 0;

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
                            desaImatgeSAF(imgBitmap, mItem.getImatges());

                            // Actualitza la llista d’imatges després d’afegir-ne una nova
                            llistaImatgesSAF = obtenirLlistaImatgesSAF(mItem.getImatges());
                            posicioImgSAF = llistaImatgesSAF.length - 1;
                        }
                    }
                }
        );

        folderPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Uri uri = result.getData().getData();
                        getContentResolver().takePersistableUriPermission(
                                uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        );
                        carpetaImatgesUri = uri;
                        getSharedPreferences("config", MODE_PRIVATE)
                                .edit()
                                .putString("imatgesUri", uri.toString())
                                .apply();
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

        String uriString = getSharedPreferences("config", MODE_PRIVATE).getString("imatgesUri", null);
        if (uriString != null) {
            carpetaImatgesUri = Uri.parse(uriString);
        }

        ResetApres = false;
        String Clau = getIntent().getStringExtra(ARG_ITEM_ID);
        db = new GestorDB(getApplicationContext());
        db.open();
        if (Clau.length() > 0) {
            mItem = db.selEntDic(Integer.parseInt(Clau));
        } else {
            mItem = new classPersones();
        }

        llistaImatgesSAF = obtenirLlistaImatgesSAF(mItem.getImatges());
        if (llistaImatgesSAF.length > 0) {
            posicioImgSAF = 0;
            mostraImatgeSAF(llistaImatgesSAF[posicioImgSAF]);
        }

        if (mItem != null) {
            ((TextView) findViewById(R.id.txtModId)).setText("" + mItem.getId());
            ((TextView) findViewById(R.id.edtModNom)).setText(mItem.getNom());
            ((TextView) findViewById(R.id.edtModCognom1)).setText(mItem.getCognom1());
            ((TextView) findViewById(R.id.edtModCognom2)).setText(mItem.getNum());
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
        if (carpetaImatgesUri == null) return new DocumentFile[0];

        DocumentFile carpetaBase = DocumentFile.fromTreeUri(this, carpetaImatgesUri);
        if (carpetaBase == null) return new DocumentFile[0];

        DocumentFile subcarpeta = carpetaBase.findFile(nomSubcarpeta);
        if (subcarpeta == null || !subcarpeta.isDirectory()) return new DocumentFile[0];

        return subcarpeta.listFiles();
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
        if (llistaImatgesSAF != null && posicioImgSAF < llistaImatgesSAF.length - 1) {
            posicioImgSAF++;
            mostraImatgeSAF(llistaImatgesSAF[posicioImgSAF]);
        }
    }

    public void fotoAnterior(View view) {
        if (llistaImatgesSAF != null && posicioImgSAF > 0) {
            posicioImgSAF--;
            mostraImatgeSAF(llistaImatgesSAF[posicioImgSAF]);
        }
    }

    public void fotoAfegeix(View view) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(cameraIntent);
        }
    }

    private void desaImatgeSAF(Bitmap bitmap, String nomSubcarpeta) {
        if (carpetaImatgesUri == null) return;

        DocumentFile carpetaBase = DocumentFile.fromTreeUri(this, carpetaImatgesUri);
        if (carpetaBase == null || !carpetaBase.canWrite()) return;

        DocumentFile subcarpeta = carpetaBase.findFile(nomSubcarpeta);
        if (subcarpeta == null || !subcarpeta.isDirectory()) {
            subcarpeta = carpetaBase.createDirectory(nomSubcarpeta);
        }
        if (subcarpeta == null || !subcarpeta.canWrite()) return;

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + ".jpg";

        DocumentFile nouFitxer = subcarpeta.createFile("image/jpeg", imageFileName);
        if (nouFitxer == null) return;

        try (OutputStream out = getContentResolver().openOutputStream(nouFitxer.getUri())) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void triaCarpeta(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        folderPickerLauncher.launch(intent);
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
                db.delEntDic(mItem.getId());
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
        mItem.setCognom1(((TextView) findViewById(R.id.edtModCognom1)).getText().toString());
        mItem.setNum(((TextView) findViewById(R.id.edtModCognom2)).getText().toString());
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
