package mx.com.cesarcorona.directorio.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

import mx.com.cesarcorona.directorio.R;
import mx.com.cesarcorona.directorio.adapter.PromosAdapter;
import mx.com.cesarcorona.directorio.pojo.Negocio;
import mx.com.cesarcorona.directorio.pojo.PremiumBanner;
import mx.com.cesarcorona.directorio.pojo.Promocion;

import static mx.com.cesarcorona.directorio.activities.CategoriaActivity.ALL_NEGOCIO_REFERENCE;
import static mx.com.cesarcorona.directorio.activities.CategoriaActivity.PREMIUM_REFERENCE;
import static mx.com.cesarcorona.directorio.adapter.PromosAdapter.ADAPTER_TYPE_EDIT;
import static mx.com.cesarcorona.directorio.adapter.PromosAdapter.ADAPTER_TYPE_INFO;

public class PromocionesActivity extends BaseAnimatedActivity implements PromosAdapter.OnPromoInterface {



    public static final String TAG = PromocionesActivity.class.getSimpleName();
    public static String ALL_PROMOS_REFERENCE ="premium";


    private EditText textSearch;
    private Button buttonSearch;
    private CheckBox abierto,alimentos,tipo,aDomicilio,masCercano,always,creditCard,promocion;
    private ListView promosList;
    private LinkedList<Promocion> premiumNegocios;
    private LinkedList<Promocion> filteredNegocios;
    private ProgressDialog pDialog;
    private PromosAdapter promosAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promociones);

        textSearch = (EditText) findViewById(R.id.palabra_clave_text);
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
        reloadPromociones();


        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });


        textSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    seachAttepmt();
                    return true;
                }
                return false;
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textSearch.setText("");
                promosAdapter = new PromosAdapter(PromocionesActivity.this,premiumNegocios,ADAPTER_TYPE_INFO);
                promosAdapter.setOnPromoInterface(PromocionesActivity.this);
                promosList.setAdapter(promosAdapter);
            }
        });

    }


    private void seachAttepmt(){
        if(textSearch.getText().length() >0){
            promosAdapter = new PromosAdapter(PromocionesActivity.this,new LinkedList<Promocion>(),ADAPTER_TYPE_INFO);
            promosAdapter.setOnPromoInterface(this);
            promosList.setAdapter(promosAdapter);
            for(Promocion laPromocion:premiumNegocios){
                if(laPromocion.containsTag(textSearch.getText().toString())){
                    promosAdapter.addPromocion(laPromocion);
                }
            }
        }else{
            Toast.makeText(PromocionesActivity.this,"Introudce una palabra clave",Toast.LENGTH_LONG).show();
        }


    }

    private void reloadPromociones(){
            promosAdapter = new PromosAdapter(PromocionesActivity.this,new LinkedList<Promocion>(),ADAPTER_TYPE_INFO);
            promosAdapter.setOnPromoInterface(this);
            promosList.setAdapter(promosAdapter);
            DatabaseReference promosReference = FirebaseDatabase.getInstance().getReference("Example/promociones");
            promosReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot promoStap:dataSnapshot.getChildren()){
                        Promocion promocion = promoStap.getValue(Promocion.class);
                        promocion.setDataBasereference(promoStap.getKey());
                        promosAdapter.addPromocion(promocion);
                        if(!premiumNegocios.contains(promocion)){
                            premiumNegocios.add(promocion);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


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
    public void OnPromoSelected(final Promocion promoselected) {

        final Intent detailIntent = new Intent(PromocionesActivity.this,NegocioDetailActivity.class);
        final Bundle extras = new Bundle();
        DatabaseReference negocioReference = FirebaseDatabase.getInstance().getReference("Example/allnegocios/"+
        promoselected.getNegocioId());
        negocioReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Negocio negocio = dataSnapshot.getValue(Negocio.class);
                extras.putSerializable(NegocioDetailActivity.KEY_NEGOCIO,negocio);
                detailIntent.putExtras(extras);
                startActivity(detailIntent);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(PromocionesActivity.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();

            }
        });






    }

    @Override
    public void OnEditSelected(Promocion promocion) {

    }

    @Override
    public void OnDeletePromo(Promocion promocion) {

    }
}
