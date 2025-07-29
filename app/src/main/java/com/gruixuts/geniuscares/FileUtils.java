package com.gruixuts.geniuscares;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Classe d'utilitats per gestió de fitxers compatible amb Android 11+ (Scoped Storage)
 * Aquesta classe proporciona mètodes segurs per accedir als directoris de l'aplicació
 */
public class FileUtils {
    private static final String TAG = "FileUtils";

    /**
     * Obté el directori de fitxers externs de l'aplicació per una subcarpeta específica
     * Aquest mètode és compatible amb Android 11+ i no requereix permisos especials
     * 
     * @param context Context de l'aplicació
     * @param subdirectory Nom de la subcarpeta (ex: "Copies", "Imatges")
     * @return File objecte del directori, o null si hi ha error
     */
    public static File getAppExternalDirectory(Context context, String subdirectory) {
        try {
            ContextWrapper contextWrapper = new ContextWrapper(context);
            File directory = contextWrapper.getExternalFilesDir(subdirectory);
            
            if (directory != null && !directory.exists()) {
                boolean created = directory.mkdirs();
                if (!created) {
                    Log.w(TAG, "No s'ha pogut crear el directori: " + directory.getAbsolutePath());
                }
            }
            
            return directory;
        } catch (Exception e) {
            Log.e(TAG, "Error obtenint directori extern: " + e.getMessage());
            return null;
        }
    }

    /**
     * Obté el directori intern de l'aplicació
     * 
     * @param context Context de l'aplicació
     * @return File objecte del directori intern
     */
    public static File getAppInternalDirectory(Context context) {
        return context.getFilesDir();
    }

    /**
     * Verifica si l'emmagatzematge extern està disponible per escriptura
     * 
     * @return true si està disponible, false altrament
     */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * Verifica si l'emmagatzematge extern està disponible per lectura
     * 
     * @return true si està disponible, false altrament
     */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
               Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    /**
     * Obté la ruta completa d'un fitxer en un directori específic de l'aplicació
     * 
     * @param context Context de l'aplicació
     * @param subdirectory Subcarpeta
     * @param filename Nom del fitxer
     * @return Ruta completa del fitxer, o null si hi ha error
     */
    public static String getFilePath(Context context, String subdirectory, String filename) {
        File directory = getAppExternalDirectory(context, subdirectory);
        if (directory != null) {
            return new File(directory, filename).getAbsolutePath();
        }
        return null;
    }

    /**
     * Obté la ruta del directori d'imatges de l'aplicació
     * Mètode específic per compatibilitat amb codi existent
     * 
     * @param context Context de l'aplicació
     * @return Ruta del directori d'imatges
     */
    public static String getCarpetaImatges(Context context) {
        File directory = getAppExternalDirectory(context, "Imatges");
        return directory != null ? directory.getAbsolutePath() : "";
    }

    /**
     * Obté la ruta del directori de còpies de l'aplicació
     * Mètode específic per compatibilitat amb codi existent
     * 
     * @param context Context de l'aplicació
     * @return Ruta del directori de còpies
     */
    public static String getCarpetaCopies(Context context) {
        File directory = getAppExternalDirectory(context, "Copies");
        return directory != null ? directory.getAbsolutePath() : "";
    }
}

