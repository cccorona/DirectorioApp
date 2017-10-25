package mx.com.cesarcorona.directorio.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.Random;

import mx.com.cesarcorona.directorio.R;
import mx.com.cesarcorona.directorio.Utils.DateUtils;
import mx.com.cesarcorona.directorio.adapter.CategoryAdapter;
import mx.com.cesarcorona.directorio.adapter.NegocioPorCategoriaAdapter;
import mx.com.cesarcorona.directorio.custom.ExpandableGridView;
import mx.com.cesarcorona.directorio.pojo.Categoria;
import mx.com.cesarcorona.directorio.pojo.Negocio;
import mx.com.cesarcorona.directorio.pojo.PremiumBanner;

import static mx.com.cesarcorona.directorio.activities.CategoriaActivity.ALL_NEGOCIO_REFERENCE;
import static mx.com.cesarcorona.directorio.activities.CategoriaActivity.PREMIUM_REFERENCE;

public class NegocioPorCategoriaActivity extends BaseAnimatedActivity implements NegocioPorCategoriaAdapter.NegocioSelectedListener {


    public static String NEGOCIOS_REFERENCE ="negocios";
    public static String TAG = NegocioPorCategoriaActivity.class.getSimpleName();
    public static String ITEM_SELECTED ="categoria";
    public static int ELEMENT_SIZE = 120;


    private DatabaseReference mDatabase;
    private LinkedList<Negocio> allNegocios;
    private LinkedList<Negocio> openNegocios,closedNegocios;
    private ExpandableGridView openNegociosGrid,closedNegociosGrid;
    private NegocioPorCategoriaAdapter negocioPorCategoriaAdapterClosed,negocioPorCategoriaAdapterOpen;
    private ProgressDialog pDialog;
    private Categoria categoriaSelected;

    private CardView categoryPromo;
    private ImageView bannerPromo;
    private Negocio negocionOnMainBanner;
    private LinkedList<PremiumBanner> premiumNegocios;
    static Random rand = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_negocio_por_categoria);
        initialize();
        showpDialog();
        recoveryNegocios();

        categoryPromo = (CardView)findViewById(R.id.categoria_card_view);
        bannerPromo = (ImageView) findViewById(R.id.banner_promo);
        categoryPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(negocionOnMainBanner != null){
                    Intent detailIntent = new Intent(NegocioPorCategoriaActivity.this,NegocioDetailActivity.class);
                    Bundle extras = new Bundle();
                    extras.putSerializable(NegocioDetailActivity.KEY_NEGOCIO,negocionOnMainBanner);
                    detailIntent.putExtras(extras);
                    startActivity(detailIntent);
                }

            }
        });

    }

    private void initialize(){
        categoriaSelected = (Categoria) getIntent().getExtras().getSerializable(ITEM_SELECTED);
        mDatabase = FirebaseDatabase.getInstance().getReference( NEGOCIOS_REFERENCE +"/" +categoriaSelected.getDataBaseReference());
        openNegociosGrid = (ExpandableGridView) findViewById(R.id.opened_negocios_grid_view);
        closedNegociosGrid = (ExpandableGridView) findViewById(R.id.close_negocios_grid_view);
        allNegocios = new LinkedList<>();
        openNegocios = new LinkedList<>();
        premiumNegocios = new LinkedList<>();
        closedNegocios = new LinkedList<>();
        pDialog = new ProgressDialog(NegocioPorCategoriaActivity.this);
        pDialog.setMessage("Por favor espera...");
        pDialog.setCancelable(false);
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
                for (DataSnapshot negocioSnap : dataSnapshot.getChildren()) {
                    Negocio negocio = negocioSnap.getValue(Negocio.class);
                    negocio.setNegocioDataBaseReference(negocioSnap.getRef().toString());
                    allNegocios.add(negocio);
                }


                clasifyOpenOrclosed();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                FirebaseCrash.log(TAG + "loadPost:onCancelled" + databaseError.toException());
            }
        };
        mDatabase.addListenerForSingleValueEvent(categoryListener);
    }


    @Override
    public void OnNegocioClicked(Negocio negocio) {
        Intent negocioDetailIntent = new Intent(NegocioPorCategoriaActivity.this,NegocioDetailActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(NegocioDetailActivity.KEY_NEGOCIO,negocio);
        negocioDetailIntent.putExtras(extras);
        startActivity(negocioDetailIntent);
    }


    private void clasifyOpenOrclosed(){
        for(Negocio negocio:allNegocios){
            String startHour = negocio.getOpen_time();
            String endHour = negocio.getClose_time();
            if(DateUtils.isNowInInterval(startHour,endHour)){
                openNegocios.add(negocio);
            }else{
                closedNegocios.add(negocio);
            }
        }

        negocioPorCategoriaAdapterClosed = new NegocioPorCategoriaAdapter(closedNegocios,NegocioPorCategoriaActivity.this);
        negocioPorCategoriaAdapterOpen = new NegocioPorCategoriaAdapter(openNegocios,NegocioPorCategoriaActivity.this);

        negocioPorCategoriaAdapterClosed.setNegocioSelectedListener(NegocioPorCategoriaActivity.this);
        negocioPorCategoriaAdapterOpen.setNegocioSelectedListener(NegocioPorCategoriaActivity.this);
        openNegociosGrid.setAdapter(negocioPorCategoriaAdapterOpen);
        openNegociosGrid.setExpanded(true);
        openNegociosGrid.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, openNegocios.size() * ELEMENT_SIZE));
        //openNegociosGrid.setMinimumHeight(allNegocios.size() * ELEMENT_SIZE);
        closedNegociosGrid.setAdapter(negocioPorCategoriaAdapterClosed);
        closedNegociosGrid.setExpanded(true);
        // closedNegociosGrid.setMinimumHeight(allNegocios.size() * ELEMENT_SIZE);
        closedNegociosGrid.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, closedNegocios.size() * ELEMENT_SIZE));



        showPremiunBanner();

    }


    private void showPremiunBanner(){
        DatabaseReference premiumReference = FirebaseDatabase.getInstance().getReference(PREMIUM_REFERENCE);
        premiumReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot keyNegocio:dataSnapshot.getChildren()){
                    PremiumBanner premiumBanner = keyNegocio.getValue(PremiumBanner.class);
                    premiumBanner.setDatabaseReference(keyNegocio.getKey());
                    premiumNegocios.add(premiumBanner);
                }
                if(premiumNegocios.size()>0){
                    DatabaseReference choosedPremiumNegocioReference =FirebaseDatabase.getInstance().getReference(ALL_NEGOCIO_REFERENCE +"/" +
                            getRandomChestItem().getDatabaseReference());
                    choosedPremiumNegocioReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            negocionOnMainBanner = dataSnapshot.getValue(Negocio.class);
                            if(negocionOnMainBanner.getPremiumBannerUrl() == null){

                                if(negocionOnMainBanner.getUrl_promos() == null){
                                    FirebaseCrash.log(TAG+": No hay banner en negocio premium" );
                                    //show default banner
                                    Picasso.with(NegocioPorCategoriaActivity.this).load(R.drawable.hi_res_logo).fit().into(bannerPromo);
                                    hidepDialog();
                                }else{
                                    for(String key:negocionOnMainBanner.getUrl_promos().keySet()){
                                        Picasso.with(NegocioPorCategoriaActivity.this).load(negocionOnMainBanner.getUrl_promos().get(key)).fit().into(bannerPromo);
                                        break;
                                    }
                                    hidepDialog();
                                }


                            }else{
                                Picasso.with(NegocioPorCategoriaActivity.this).load(negocionOnMainBanner.getPremiumBannerUrl()).fit().into(bannerPromo);
                                hidepDialog();

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //show default banner
                            Picasso.with(NegocioPorCategoriaActivity.this).load(R.drawable.hi_res_logo).fit().into(bannerPromo);
                            hidepDialog();


                        }
                    });

                }else{
                    //show defalt banner
                    Picasso.with(NegocioPorCategoriaActivity.this).load(R.drawable.hi_res_logo).fit().into(bannerPromo);
                    hidepDialog();


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //show default banner
                Picasso.with(NegocioPorCategoriaActivity.this).load(R.drawable.hi_res_logo).fit().into(bannerPromo);
                hidepDialog();


            }
        });

    }

    private  PremiumBanner getRandomChestItem() {
        try {
            return premiumNegocios.get(rand.nextInt(premiumNegocios.size()));
        }
        catch (Throwable e){
            return null;
        }
    }


}
