package com.gruixuts.geniuscares;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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
    private String CarpetaImatges; // Inicialitzada dinàmicament amb FileUtils
    private Integer NumImg;
    private String nomImatge[];
    static final int REQUEST_IMAGE_CAPTURE = 17;

    private classPersones mItem;
    Boolean ResetApres = false;

    //protected static final int REQUEST_CODE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_manteniment_modificar);

        // Registre del launcher modern per a la càmera
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getExtras() != null) {
                            Bitmap imgBitmap = (Bitmap) data.getExtras().get("data");
                            ImageView imgView = findViewById(R.id.imgImatges);
                            imgView.setImageBitmap(imgBitmap);

                            // Crear nom de fitxer amb data
                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                            String imageFileName = "JPEG_" + timeStamp + "_";

                            File image = null;
                            try {
                                image = File.createTempFile(
                                        imageFileName,
                                        ".jpg",
                                        new File(CarpetaImatges)
                                );
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            NumImg = nomImatge.length + 1;
                            nomImatge = Arrays.copyOf(nomImatge, NumImg);
                            nomImatge[NumImg - 1] = imageFileName + ".jpg";

                            try {
                                FileOutputStream fOut = new FileOutputStream(image);
                                imgBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                                fOut.flush();
                                fOut.close();
                            } catch (IOException ignored) {}
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


        // Inicialitzar carpeta d'imatges amb FileUtils (compatible Android 11+)
        CarpetaImatges = FileUtils.getCarpetaImatges(this);
        
        ResetApres = false;
        String Clau = getIntent().getStringExtra(ARG_ITEM_ID);
        db = new GestorDB(getApplicationContext());
        db.open();
        if (Clau.length()>0) {
            mItem = db.selEntDic(Integer.parseInt(Clau));
        } else {
            mItem = new classPersones( );
        }
        CarpetaImatges += "/" + mItem.getImatges();
        // Todo: Carregar la llista de noms de fitxers de les imatges

        File carpeta = new File(CarpetaImatges);
        if(!carpeta.exists()) { // Crear-la
            carpeta.mkdirs();
        }
        nomImatge = carpeta.list();
        if (nomImatge.length != 0) {
            NumImg = 1;
            Drawable d = Drawable.createFromPath(CarpetaImatges + "/" + nomImatge[NumImg - 1]);
            ((ImageView) findViewById(R.id.imgImatges)).setImageDrawable(d);
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
                case "t":
                    ((RadioButton) findViewById(R.id.rdT)).setChecked(true); break;
                case "a":
                    ((RadioButton) findViewById(R.id.rdA)).setChecked(true); break;
                case "1h":
                    ((RadioButton) findViewById(R.id.rd1h)).setChecked(true); break;
                case "1d":
                    ((RadioButton) findViewById(R.id.rd1d)).setChecked(true); break;
                case "1s":
                    ((RadioButton) findViewById(R.id.rd1s)).setChecked(true); break;
                case "1m":
                    ((RadioButton) findViewById(R.id.rd1m)).setChecked(true); break;
                case "6m":
                    ((RadioButton) findViewById(R.id.rd6m)).setChecked(true); break;
                default:
                    Toast toast = Toast.makeText(getApplicationContext(), "Tipus propera acció: '" + (mItem.getNextTipus()) + "' no identificat", Toast.LENGTH_LONG);
                    toast.show();;
                    ((RadioButton) findViewById(R.id.rdA)).setChecked(true); break;
            }
            ((TextView) findViewById(R.id.edtModNextData)).setText(mItem.getNextDataTxt());
        }


    }

    public void fotoSeguent(View view) {
        if (NumImg < nomImatge.length ) {
            NumImg++;
            Drawable d = Drawable.createFromPath(CarpetaImatges + "/" + nomImatge[NumImg - 1]);
            ((ImageView) findViewById(R.id.imgImatges)).setImageDrawable(d);
        }
    }
    public void fotoAnterior(View view) {
        if (NumImg > 1 ) {
            NumImg--;
            Drawable d = Drawable.createFromPath(CarpetaImatges + "/" + nomImatge[NumImg - 1]);
            ((ImageView) findViewById(R.id.imgImatges)).setImageDrawable(d);
        }
    }

//    public void fotoAfegeix(View view) {
//        Intent CameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if(CameraIntent.resolveActivity(getPackageManager()) != null){
//            startActivityForResult(CameraIntent, REQUEST_IMAGE_CAPTURE);
//        }
//    }

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

        // Buscar o crear la subcarpeta
        DocumentFile subcarpeta = carpetaBase.findFile(nomSubcarpeta);
        if (subcarpeta == null || !subcarpeta.isDirectory()) {
            subcarpeta = carpetaBase.createDirectory(nomSubcarpeta);
        }
        if (subcarpeta == null || !subcarpeta.canWrite()) return;

        // Crear fitxer dins la subcarpeta
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





        public void fotoElimina(View view) {

    }
    // Es crida després d'haver fet la foto
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Bitmap imgBitmap;
//        ImageView imgView = findViewById(R.id.imgImatges);
//        File image = null;
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            imgBitmap = (Bitmap) extras.get("data");
//            imgView.setImageBitmap(imgBitmap);
//        }
//        else {
//            return;
//        }
//        // Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        try {
//            image = File.createTempFile(
//                    imageFileName,  /* prefix */
//                    ".jpg",         /* suffix */
//                    new File(CarpetaImatges)      /* directory */
//            );
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        NumImg = nomImatge.length +1;
//        nomImatge = Arrays.copyOf(  nomImatge,NumImg);
//        nomImatge[NumImg-1]= imageFileName + ".jpg";
//        try {
//            FileOutputStream fOut = new FileOutputStream(image);
//
//            imgBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
//            fOut.flush();
//            fOut.close();
////            MakeSureFileWasCreatedThenMakeAvabile(file);
////            AbleToSave();
//        }
//
//        catch(FileNotFoundException e) {
//            //          Toast.makeText(context, "¡No se ha podido guardar la imagen!", Toast.LENGTH_SHORT).show();
//        }
//        catch(IOException e) {
////            Toast.makeText(context, "¡No se ha podido guardar la imagen!", Toast.LENGTH_SHORT).show();
//        }
//
//
//
//        // Save a file: path for use with ACTION_VIEW intents
//        //currentPhotoPath = image.getAbsolutePath();
//
//    }


    public void PregEsborra(View view) {
        // MsgBox ¿?
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Segur que vols esborrar?");
        builder.setTitle("Atenció!!");
        builder.setCancelable(false);
        builder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.setPositiveButton("Sí",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        db.open();
                        if (mItem.getId()!=0) {
                            db.delEntDic(mItem.getId());
                        }
                        db.close();
                        // Todo: Eliminar les imatges

                        dialog.cancel();
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        // Fi MsgBox
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
        if (((RadioButton) findViewById(R.id.rdT)).isChecked()) {
            mItem.setNextTipus("t");
        } else if (((RadioButton) findViewById(R.id.rdA)).isChecked()) {
            mItem.setNextTipus("a");
        } else if (((RadioButton) findViewById(R.id.rd1h)).isChecked()) {
            mItem.setNextTipus("1h");
        } else if (((RadioButton) findViewById(R.id.rd1d)).isChecked()) {
            mItem.setNextTipus("1d");
        } else if (((RadioButton) findViewById(R.id.rd1s)).isChecked()) {
            mItem.setNextTipus("1s");
        } else if (((RadioButton) findViewById(R.id.rd1m)).isChecked()) {
            mItem.setNextTipus("1m");
        } else if (((RadioButton) findViewById(R.id.rd6m)).isChecked()) {
            mItem.setNextTipus("6m");
        } else mItem.setNextTipus("t");

        mItem.setNextData(((TextView) findViewById(R.id.edtModNextData)).getText().toString());

        // Consistència
        if (mItem.getNextTipus().equals("t") || mItem.getNextTipus().equals("a")) {
            mItem.setNextData("");             
        } else {
            if (mItem.getNextDataTxt().equals("")) {
                Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("Europe/Madrid"));
                cal.setTime(new Date());
                switch (mItem.getNextTipus()) {
                    case "1h":
                        cal.add(Calendar.HOUR, 1);
                        break;
                    case "1d":
                        cal.add(Calendar.DATE, 1);
                        break;
                    case "1s":
                        cal.add(Calendar.DATE, 7);
                        break;
                    case "1m":
                        cal.add(Calendar.DATE, 28);
                        break;
                    case "6m":
                        cal.add(Calendar.DATE, 28*6);
                }
                mItem.setNextData(cal.getTime());
            }
        }
        
        db.open();
        if (mItem.getId()==0) {
            db.creaDiccionari(mItem);
        } else {
            db.actDiccionari(mItem);
        }
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
