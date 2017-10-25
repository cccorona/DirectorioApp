package mx.com.cesarcorona.directorio.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

import mx.com.cesarcorona.directorio.R;
import mx.com.cesarcorona.directorio.adapter.PromosAdapter;
import mx.com.cesarcorona.directorio.pojo.Negocio;
import mx.com.cesarcorona.directorio.pojo.PremiumBanner;

import static mx.com.cesarcorona.directorio.activities.CategoriaActivity.ALL_NEGOCIO_REFERENCE;
import static mx.com.cesarcorona.directorio.activities.CategoriaActivity.PREMIUM_REFERENCE;

public class PromocionesActivity extends BaseAnimatedActivity implements PromosAdapter.OnPromoInterface {



    public static final String TAG = PromocionesActivity.class.getSimpleName();
    public static String ALL_PROMOS_REFERENCE ="premium";


    private EditText textSearch;
    private Button buttonSearch;
    private CheckBox abierto,alimentos,tipo,aDomicilio,masCercano,always,creditCard,promocion;
    private ListView promosList;
    private LinkedList<PremiumBanner> premiumNegocios;
    private ProgressDialog pDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promociones);

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
        promosList = (ListView) findViewById(R.id.result_grid);
        premiumNegocios = new LinkedList<>();
        pDialog = new ProgressDialog(PromocionesActivity.this);
        pDialog.setMessage("Por favor espera...");
        pDialog.setCancelable(false);
        showpDialog();
        showPremiunBanner();


        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }


    private void showPremiunBanner(){
        final DatabaseReference premiumReference = FirebaseDatabase.getInstance().getReference(PREMIUM_REFERENCE);
        premiumReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot keyNegocio:dataSnapshot.getChildren()){
                    PremiumBanner premiumBanner = keyNegocio.getValue(PremiumBanner.class);
                    premiumBanner.setDatabaseReference(keyNegocio.getKey());
                    premiumNegocios.add(premiumBanner);
                }
                PromosAdapter promosAdapter = new PromosAdapter(PromocionesActivity.this,premiumNegocios);
                promosAdapter.setOnPromoInterface(PromocionesActivity.this);
                promosList.setAdapter(promosAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //show default banner
                hidepDialog();


            }
        });

        hidepDialog();

    }



    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    @Override
    public void OnPromoSelected(PremiumBanner promoselected) {

        Intent detailIntent = new Intent(PromocionesActivity.this,NegocioDetailActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(NegocioDetailActivity.KEY_NEGOCIO,promoselected.getRelatedNegocio());
        detailIntent.putExtras(extras);
        startActivity(detailIntent);

    }
}
