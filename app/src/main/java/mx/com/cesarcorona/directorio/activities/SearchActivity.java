package mx.com.cesarcorona.directorio.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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
import mx.com.cesarcorona.directorio.Utils.DateUtils;
import mx.com.cesarcorona.directorio.adapter.NegocioPorCategoriaAdapter;
import mx.com.cesarcorona.directorio.pojo.Negocio;

import static mx.com.cesarcorona.directorio.activities.NegocioPorCategoriaActivity.NEGOCIOS_REFERENCE;

/**
 * Created by ccabrera on 15/08/17.
 */

public class SearchActivity extends BaseAnimatedActivity  implements NegocioPorCategoriaAdapter.NegocioSelectedListener{


    public static final String TAG = SearchActivity.class.getSimpleName();
    public static String CATEGORY_REFERENCE ="categorias";
    public static String ALL_NEGOCIOS_REFERENCE ="allnegocios";


    private EditText textSearch;
    private Button buttonSearch;
    private CheckBox abierto,alimentos,tipo,aDomicilio,masCercano,always,creditCard,promocion;

    private DatabaseReference mDatabase;
    private LinkedList<Negocio> allNegocios;
    private LinkedList<Negocio> filteredNegocios;
    private ProgressDialog pDialog;
    private GridView resultGrid;
    private boolean needFromWebSeach;



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
        resultGrid = (GridView)findViewById(R.id.result_grid);
        mDatabase = FirebaseDatabase.getInstance().getReference(ALL_NEGOCIOS_REFERENCE);
        allNegocios = new LinkedList<>();
        needFromWebSeach = false;
        filteredNegocios = new LinkedList<>();
        pDialog = new ProgressDialog(SearchActivity.this);
        pDialog.setMessage("Por favor espera...");
        pDialog.setCancelable(false);
        recoveryNegocios();


        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchFunciones();
            }
        });





    } // Fin onCreate()


    private void searchFunciones(){
        filteredNegocios = new LinkedList<>(allNegocios);
        showpDialog();
        if(alimentos.isChecked()){
            //
        }else{
            if(abierto.isChecked()){
                removeClosedNegocios();
            }else if(aDomicilio.isChecked()){
                removeNoToDomicilio();
            }else if(always.isChecked()){
                removeNotOpened24();
            }else if(creditCard.isChecked()){
                removeNotCreditCard();
            }else if(promocion.isChecked()){
                removeNotWithPromotion();
            }
        }

        NegocioPorCategoriaAdapter negocioPorCategoriaAdapter = new NegocioPorCategoriaAdapter(filteredNegocios,SearchActivity.this);
        negocioPorCategoriaAdapter.setNegocioSelectedListener(SearchActivity.this);
        resultGrid.setAdapter(negocioPorCategoriaAdapter);
        hidepDialog();


    }

    private void removeClosedNegocios(){
        LinkedList<Negocio> negociosToRemove = new LinkedList<>();
        for(Negocio negocio:filteredNegocios){
            String startHour = negocio.getOpen_time();
            String endHour = negocio.getClose_time();
            if(DateUtils.isNowInInterval(startHour,endHour)){

            }else{
                negociosToRemove.add(negocio);
            }
        }

        for(Negocio negocioToRemove:negociosToRemove){
            filteredNegocios.remove(negocioToRemove);
        }
    }


    private void removeNoToDomicilio(){
        LinkedList<Negocio> negociosToRemove = new LinkedList<>();
        for(Negocio negocio:filteredNegocios){
            if(!negocio.isEntregaADomicilio()){
                negociosToRemove.add(negocio);
            }
        }

        for(Negocio negocioToRemove:negociosToRemove){
            filteredNegocios.remove(negocioToRemove);
        }
    }

    private void removeNotOpened24(){
        LinkedList<Negocio> negociosToRemove = new LinkedList<>();
        for(Negocio negocio:filteredNegocios){
            if(!negocio.isOpen24Hours()){
                negociosToRemove.add(negocio);
            }
        }

        for(Negocio negocioToRemove:negociosToRemove){
            filteredNegocios.remove(negocioToRemove);
        }
    }

    private void removeNotCreditCard(){
        LinkedList<Negocio> negociosToRemove = new LinkedList<>();
        for(Negocio negocio:filteredNegocios){
            if(!negocio.isAceptaTArjeta()){
                negociosToRemove.add(negocio);
            }
        }

        for(Negocio negocioToRemove:negociosToRemove){
            filteredNegocios.remove(negocioToRemove);
        }
    }

    private void removeNotWithPromotion(){
        LinkedList<Negocio> negociosToRemove = new LinkedList<>();
        for(Negocio negocio:filteredNegocios){
            if(!negocio.isPremiumStatus()){
                negociosToRemove.add(negocio);
            }
        }

        for(Negocio negocioToRemove:negociosToRemove){
            filteredNegocios.remove(negocioToRemove);
        }
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
                    allNegocios.add(categoriaSnap.getValue(Negocio.class));

                }
                NegocioPorCategoriaAdapter negocioPorCategoriaAdapter = new NegocioPorCategoriaAdapter(allNegocios,SearchActivity.this);
                negocioPorCategoriaAdapter.setNegocioSelectedListener(SearchActivity.this);
                resultGrid.setAdapter(negocioPorCategoriaAdapter);


                hidepDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                FirebaseCrash.log(TAG + "loadPost:onCancelled" + databaseError.toException());
            }
        };
        mDatabase.limitToFirst(200).addListenerForSingleValueEvent(categoryListener);
    }


    @Override
    public void OnNegocioClicked(Negocio negocio) {
        Intent negocioDetailIntent = new Intent(SearchActivity.this,NegocioDetailActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(NegocioDetailActivity.KEY_NEGOCIO,negocio);
        negocioDetailIntent.putExtras(extras);
        startActivity(negocioDetailIntent);
    }
}
