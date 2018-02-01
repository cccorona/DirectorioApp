package mx.com.cesarcorona.directorio.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoQuery;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.Random;

import mx.com.cesarcorona.directorio.MainActivity;
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


    public static String NEGOCIOS_REFERENCE ="allnegocios";
    public static String GEO_REFERENCE ="geohash";

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
        getNearBusiness();
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


        ImageView back_button= (ImageView)findViewById(R.id.back_arrow_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NegocioPorCategoriaActivity.super.onBackPressed();
            }
        });

        ImageView homeButton=(ImageView)findViewById(R.id.home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(NegocioPorCategoriaActivity.this, MainActivity.class);
                startActivity(mainIntent);
            }
        });

    }



    private void getNearBusiness(){
        DatabaseReference geofireReference = FirebaseDatabase.getInstance().getReference(GEO_REFERENCE);
        GeoFire geoFire = new GeoFire(geofireReference);
    }







    private void initialize(){
        categoriaSelected = (Categoria) getIntent().getExtras().getSerializable(ITEM_SELECTED);
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
        Query categoriaQuery = FirebaseDatabase.getInstance().getReference("Example").child(NEGOCIOS_REFERENCE)
                .orderByChild("categoria").equalTo(categoriaSelected.getDataBaseReference());

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
        categoriaQuery.addListenerForSingleValueEvent(categoryListener);
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
            String startHour = negocio.getHora_apertura();
            String endHour = negocio.getHora_cierre();
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
        DatabaseReference premiumReference = FirebaseDatabase.getInstance().getReference("Example"+"/"+PREMIUM_REFERENCE +"/" +"premium1");
        premiumReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PremiumBanner premiumBanner =dataSnapshot.getValue(PremiumBanner.class);
                premiumNegocios.add(premiumBanner);

                if(premiumNegocios.size()>0){
                    DatabaseReference choosedPremiumNegocioReference =FirebaseDatabase.getInstance().getReference("Example"+"/"+ALL_NEGOCIO_REFERENCE +"/" +
                            premiumNegocios.get(0).getId_negocio());
                    choosedPremiumNegocioReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            negocionOnMainBanner = dataSnapshot.getValue(Negocio.class);
                            if(negocionOnMainBanner.getBanner_premium() == null){

                                FirebaseCrash.log(TAG+": No hay banner en negocio premium" );
                                //show default banner
                                Picasso.with(NegocioPorCategoriaActivity.this).load(R.drawable.hi_res_logo).fit().into(bannerPromo);
                                hidepDialog();

                            }else{
                                Picasso.with(NegocioPorCategoriaActivity.this).load(negocionOnMainBanner.getBanner_premium()).fit().into(bannerPromo);
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
