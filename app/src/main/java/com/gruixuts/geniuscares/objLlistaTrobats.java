package com.gruixuts.geniuscares;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 */
public class objLlistaTrobats {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<classPersones> ITEMS = new ArrayList<classPersones>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, classPersones> ITEM_MAP = new HashMap<String, classPersones>();

    /*private static final int COUNT = 25;*/

    /*static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(CreaTrobat(i));
        }
    }*/

    public static void NouSQLtxt (String filtre,String ordre,Context c) {
        ITEMS.clear();
        ITEM_MAP.clear();
        GestorDB db;
        db= new GestorDB(c);
        ArrayList<classPersones> Llista;
        db.open();
        Llista = db.selPersones(filtre,ordre);
        db.close();
        for (Integer i = 1; i <= Llista.size(); i++) {
            addItem(i.toString(), Llista.get(i-1));
        }

    }


    private static void addItem(String Pos, classPersones item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.getId().toString(), item);
    }
/*
    private static Persones CreaTrobat(int position, String cat, String basc) {
        return new Persones(String.valueOf(position), cat, basc);
    }
    */
/*
    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }
*/
    /**
     * A dummy item representing a piece of content.
     */
    /*
    public static class Persones {
        public final String Id;
        public final String Catala;
        public final String Basc;

        public Persones(String id, String cat, String basc) {
            this.Id = id;
            this.Catala = cat;
            this.Basc = basc;
        }

        @Override
        public String toString() {
            return Catala;
        }
    }*/
}
