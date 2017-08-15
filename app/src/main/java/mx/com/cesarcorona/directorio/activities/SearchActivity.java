package mx.com.cesarcorona.directorio.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import mx.com.cesarcorona.directorio.MainActivity;
import mx.com.cesarcorona.directorio.R;
import mx.com.cesarcorona.directorio.adapter.NegocioPorCategoriaAdapter;
import mx.com.cesarcorona.directorio.pojo.Negocio;

import static mx.com.cesarcorona.directorio.activities.NegocioPorCategoriaActivity.NEGOCIOS_REFERENCE;

/**
 * Created by ccabrera on 15/08/17.
 */

public class SearchActivity extends AppCompatActivity {


    public static final String TAG = SearchActivity.class.getSimpleName();
    public static String CATEGORY_REFERENCE ="categorias";


    private EditText textSearch;
    private Button buttonSearch;
    private CheckBox abierto,alimentos,tipo,aDomicilio,masCercano,always,creditCard,promocion;
    private GridView resultGrind;

    private DatabaseReference mDatabase;
    private LinkedList<Negocio> allNegocios;
    private LinkedList<Negocio> filteredNegocios;
    private ProgressDialog pDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.search_dialog_layout);
        textSearch = (EditText) findViewById(R.id.search_edit_text);
        buttonSearch = (Button) findViewById(R.id.search_button_filter);
        abierto = (CheckBox) findViewById(R.id.open_filter);
        alimentos = (CheckBox) findViewById(R.id.food_filter);
        tipo = (CheckBox) findViewById(R.id.food_type_filter);
        aDomicilio = (CheckBox) findViewById(R.id.delivery_filter);
        masCercano = (CheckBox) findViewById(R.id.closest_filter);
        always = (CheckBox) findViewById(R.id.always_open_filter);
        creditCard = (CheckBox) findViewById(R.id.credit_filter);
        promocion = (CheckBox) findViewById(R.id.prom_filter);
        mDatabase = FirebaseDatabase.getInstance().getReference( NEGOCIOS_REFERENCE);
        allNegocios = new LinkedList<>();
        pDialog = new ProgressDialog(SearchActivity.this);
        pDialog.setMessage("Por favor espera...");
        pDialog.setCancelable(false);





    } // Fin onCreate()



    private void searchFunciones(){

    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void recoveryNegocios(){
        ValueEventListener categoryListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot categoriaSnap : dataSnapshot.getChildren()) {

                }

                hidepDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                FirebaseCrash.log(TAG + "loadPost:onCancelled" + databaseError.toException());
            }
        };
        mDatabase.addListenerForSingleValueEvent(categoryListener);
    }


}
