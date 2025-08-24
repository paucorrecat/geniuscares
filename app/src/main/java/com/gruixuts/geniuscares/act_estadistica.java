package com.gruixuts.geniuscares;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class act_estadistica extends AppCompatActivity {

    SimpleDateFormat frmtData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadistica);
        GestorDB db;
        ArrayList<classPersones> Llista;
        Integer N1;
        Integer N2;

        db = GestorDB.getInstance(getApplicationContext());
        N1=db.selPersones("","").size();
        ((TextView) findViewById(R.id.TauTotal)).setText(""+N1);  //Número total de persona a a dB

        N2=db.selPersones("AMemoritzar","").size();  // Número de persona a memoritzar
        ((TextView) findViewById(R.id.TauTrbSi)).setText("" + N2);

        ((TextView) findViewById(R.id.TauTrbNo)).setText("" + (N1-N2));

        N1=db.selPersones("AMemoritzar and TeImatge","").size();
        ((TextView) findViewById(R.id.TauTraSi)).setText(""+N1);

        N2=db.selPersones("AMemoritzar and NOT TeImatge","").size();
        ((TextView) findViewById(R.id.TauTraNo)).setText("" + N2);

        N1=db.selPersones("AMemoritzar and TeImatge and NextTipus='a'","").size();
        ((TextView) findViewById(R.id.TauAprNo)).setText(""+N1);

        N2=db.selPersones("AMemoritzar and TeImatge and (NextTipus IN ('1h', '1d', '1s', '1m', '6m'))","").size();
        ((TextView) findViewById(R.id.TauAprSi)).setText("" + N2);

        /*
        // Errors
        N2=db.selPersones("(Not(Traduible) and (Basc<>''))","").size();
        N2=db.selPersones("(Not(Traduible) and (NextTipus<>''))","").size();
        N2=db.selPersones("((Traduible) and (NextTipus=''))","").size();
        N2=db.selPersones("((Basc<>'') and (NextTipus=''))","").size();
        N2=db.selPersones("((Basc<>'') and (NextTipus='t'))","").size();
        N2=db.selPersones("((Traduible) and (Basc='') and (NextTipus<>'t'))","").size();

        */

        N1=db.selPersones("(NextTipus='1h')","").size();
        N2=db.selPersones("((NextTipus='1h') and (NextData < '" + frmtData.format(new Date()) + "'))","").size();
        ((TextView) findViewById(R.id.TauRp1hM)).setText(""+N2);
        ((TextView) findViewById(R.id.TauRp1hB)).setText(""+(N1-N2));

        N1=db.selPersones("(NextTipus='1d')","").size();
        N2=db.selPersones("((NextTipus='1d') and (NextData < '" + frmtData.format(new Date()) + "'))","").size();
        ((TextView) findViewById(R.id.TauRp1dM)).setText(""+N2);
        ((TextView) findViewById(R.id.TauRp1dB)).setText(""+(N1-N2));

        N1=db.selPersones("(NextTipus='1s')","").size();
        N2=db.selPersones("((NextTipus='1s') and (NextData < '" + frmtData.format(new Date()) + "'))","").size();
        ((TextView) findViewById(R.id.TauRp1sM)).setText(""+N2);
        ((TextView) findViewById(R.id.TauRp1sB)).setText(""+(N1-N2));

        N1=db.selPersones("(NextTipus='1m')","").size();
        N2=db.selPersones("((NextTipus='1m') and (NextData < '" + frmtData.format(new Date()) + "'))","").size();
        ((TextView) findViewById(R.id.TauRp1mM)).setText(""+N2);
        ((TextView) findViewById(R.id.TauRp1mB)).setText(""+(N1-N2));

        N1=db.selPersones("(NextTipus='6m')","").size();
        N2=db.selPersones("((NextTipus='6m') and (NextData < '" + frmtData.format(new Date()) + "'))","").size();
        ((TextView) findViewById(R.id.TauRp6mM)).setText(""+N2);
        ((TextView) findViewById(R.id.TauRp6mB)).setText(""+(N1-N2));

    }
}
