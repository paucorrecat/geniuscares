package com.gruixuts.geniuscares;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.List;

/**
 * An activity representing a list of ll_trobats. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link trobatDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class act_manteniment_llista extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */

    GestorDB db;

    public void GrAll(View view) {
        List<classPersones> items = objLlistaTrobats.ITEMS;
        String strNouGrup =  "" + ((TextView) findViewById(R.id.edtMmrGrTxt)).getText() ;
        db.open();
        for ( classPersones itm : items) {
            itm.setGrup(strNouGrup);
            db.actPersones(itm);
        }
        db.close();
    }

    public void GrDel(View view) {
        List<classPersones> items = objLlistaTrobats.ITEMS;
        String strNouGrup =  "" + ((TextView) findViewById(R.id.edtMmrGrTxt)).getText() ;
        db.open();
        for ( classPersones itm : items) {
            itm.setGrup("");
            db.actPersones(itm);
        }
        db.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manteniment_llista_marc);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Trobats: " + objLlistaTrobats.ITEMS.size());
        toolbar.setTitle(getTitle());
        db=  new GestorDB(getApplicationContext());
        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
/*p2
        if (findViewById(R.id.trobat_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
*/
        View recyclerView = findViewById(R.id.trobat_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }


    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, objLlistaTrobats.ITEMS));
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final act_manteniment_llista mParentActivity;
        private final List<classPersones> mValues;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                classPersones item = (classPersones) view.getTag();
                    Context context = view.getContext();
                    Intent intent = new Intent(context, act_manteniment_modificar.class);
                    intent.putExtra(act_manteniment_modificar.ARG_ITEM_ID, item.getId().toString());

                    context.startActivity(intent);
            }
        };

        SimpleItemRecyclerViewAdapter(act_manteniment_llista parent,
                                      List<classPersones> items) {
            mValues = items;
            mParentActivity = parent;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_manteniment_llista_element, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mIdView.setText(mValues.get(position).getId().toString());
            holder.mCodiView.setText(mValues.get(position).getCodi());
            holder.mCatalaView.setText(mValues.get(position).getNom());
            holder.mBascView.setText(mValues.get(position).getCognom());

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mCodiView;
            final TextView mCatalaView;
            final TextView mBascView;

            ViewHolder(View view) {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.ll_id);
                mCodiView = (TextView) view.findViewById(R.id.ll_codi);
                mCatalaView = (TextView) view.findViewById(R.id.ll_catala);
                mBascView = (TextView) view.findViewById(R.id.ll_basc);
            }
        }


    }
}
